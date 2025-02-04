package org.example;

import org.example.model.*;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.example.menuEpisodio.mostrarEpisodios;
import static org.example.menuPerfil.mostrarPerfiles;
import static org.example.menuSerie.mostrarSeries;
import static org.example.menuUsuario.mostrarUsuarios;

public class menuHistorial {

    public static final Scanner sc = new Scanner(System.in);

    public static void menuHistorial() {
        int opcion;
        do {
            System.out.println("\n --- Menú de Historial ---");
            System.out.println("1. Buscar historial");
            System.out.println("2. Insertar en Historial");
            System.out.println("3. Modificar Historial");
            System.out.println("4. Eliminar Historial");
            System.out.println("5. Mostrar Historiales");
            System.out.println("6. Las 5 Series más vistas");
            System.out.println("7. Insertar episodios en un historial");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elige una opción: ");
            opcion = introduceEntero();

            switch (opcion) {
                case 1 -> {
                    System.out.println("Mostrando historial...");
                    buscarHistorialPorID();
                }
                case 2 -> {
                    insertarHistorial();
                }
                case 3 -> {

                }

                case 4 -> {
                    eliminarHistorial();
                }

                case 5 -> {
                    System.out.println("Mostrando todos los historiales registrados:");
                    System.out.println(mostrarHistoriales());
                }

                case 6 -> {
                    System.out.println("Las 5 Series más vistas...");
                    seriesMasVistas();
                }

                case 7 -> {
                    insertarCapitulos();
                }
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción no válida. Por favor, elige una opción del menú.");
            }
        } while (opcion != 0);
    }

    public static ArrayList<Historial> mostrarHistoriales() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        ArrayList<Historial> listaHistoriales = new ArrayList<>();

        try {
            tx = session.beginTransaction();

            // Consulta SQL para obtener todos los registros de la tabla Historial.
            List<Historial> historiales = session.createQuery("FROM Historial", Historial.class).list();

            for (Historial h : historiales) {
                Hibernate.initialize(h.getPerfil());   // Cargar perfil
                Hibernate.initialize(h.getEpisodio()); // Cargar episodio
            }

            if (historiales.isEmpty()) {
                System.out.println("La lista está vacía. No hay historiales que mostrar.");
            } else {
                listaHistoriales.addAll(historiales);
            }
            tx.commit();
        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
        return listaHistoriales;
    }

    public static void buscarHistorialPorID() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            System.out.println("Listado de historiales registrados:");
            List<Historial> historiales = mostrarHistoriales();

            if (historiales.isEmpty()) {
                System.out.println("Lo sentimos, no hay historiales disponibles. Operación cancelada");
                return;
            }
            for (Historial h : historiales) {
                System.out.println(h);
            }
            System.out.print("Introduce el ID del historial a buscar: ");
            long id = introduceEntero();

            Historial historial = session.find(Historial.class, id);
            if (historial == null) {
                System.out.println("Historial no encontrado.");
            } else {
                System.out.println("Historial encontrado: " + historial);
            }
        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void eliminarHistorial() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            System.out.println("Listado de historiales registrados:");
            List<Historial> historiales = mostrarHistoriales();

            if (historiales.isEmpty()) {
                System.out.println("Lo sentimos, no hay historiales registrados. Operación cancelada.");
                return;
            }

            for (Historial h : historiales) {
                System.out.println(h);
            }

            System.out.print("Introduce el ID del historial a eliminar: ");
            long id = introduceEntero();

            Historial historial = session.find(Historial.class, id);
            if (historial == null) {
                System.out.println("Historial no encontrado. Operación cancelada.");
                return;
            }

            System.out.println("¿Deseas eliminar este historial? (True|False)");
            String confirmacion = sc.nextLine().trim();
            if (!confirmacion.equalsIgnoreCase("true")) {
                System.out.println("Operación cancelada.");
                return;
            }

            // Desvincula el perfil asociado, si existe
            if (historial.getPerfil() != null) {
                historial.getPerfil().setHistoriales(null);  // Desvincula el perfil si es necesario
            }

            // Elimina el historial
            session.delete(historial);
            tx.commit();
            System.out.println("Historial eliminado exitosamente.");
        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }


    public static void insertarHistorial() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            // Obtener la fecha de hoy
            LocalDateTime fechaHoy = LocalDateTime.now();

            // Preguntar si quiere asociar un perfil
            System.out.println("¿Quieres asociar un perfil al historial? (True|False)");
            String confirmarPerfilString = sc.nextLine().trim();
            boolean confirmarPerfil;

            if (confirmarPerfilString.equalsIgnoreCase("true") || confirmarPerfilString.equalsIgnoreCase("false")) {
                confirmarPerfil = Boolean.parseBoolean(confirmarPerfilString);
            } else {
                System.out.println("No has introducido True o False. Operación cancelada.");
                return;
            }

            Perfil perfilSeleccionado = null;
            if (confirmarPerfil) {
                List<Perfil> perfiles = mostrarPerfiles();

                if (perfiles.isEmpty()) {
                    System.out.println("No hay perfiles registrados.");
                } else {
                    for (Perfil p : perfiles) {
                        System.out.println(p);
                    }
                    System.out.print("Introduce el ID del perfil: ");
                    long idPerfil = introduceEntero();

                    if (idPerfil < 0) {
                        System.out.println("El ID del perfil no cumple el formato. Operación cancelada.");
                        return;
                    }

                    perfilSeleccionado = session.find(Perfil.class, idPerfil);

                    if (perfilSeleccionado == null) {
                        System.out.println("Perfil no encontrado. Se creará sin perfil.");
                    }
                }
            }

            // Preguntar si quiere asociar un episodio
            System.out.println("¿Quieres asociar un episodio al historial? (True|False)");
            String confirmarEpisodioString = sc.nextLine().trim();
            boolean confirmarEpisodio;

            if (confirmarEpisodioString.equalsIgnoreCase("true") || confirmarEpisodioString.equalsIgnoreCase("false")) {
                confirmarEpisodio = Boolean.parseBoolean(confirmarEpisodioString);
            } else {
                System.out.println("No has introducido True o False. Operación cancelada.");
                return;
            }

            Episodio episodioSeleccionado = null;
            if (confirmarEpisodio) {
                List<Episodio> episodios = mostrarEpisodios();

                if (episodios.isEmpty()) {
                    System.out.println("No hay episodios registrados.");
                } else {
                    for (Episodio e : episodios) {
                        System.out.println(e);
                    }
                    System.out.print("Introduce el ID del episodio: ");
                    long idEpisodio = introduceEntero();

                    if (idEpisodio < 0) {
                        System.out.println("El ID del episodio no cumple el formato. Operación cancelada.");
                        return;
                    }

                    episodioSeleccionado = session.find(Episodio.class, idEpisodio);

                    if (episodioSeleccionado == null) {
                        System.out.println("Episodio no encontrado. Se creará sin episodio.");
                    }
                }
            }

            // Confirmación final antes de crear el historial
            System.out.println("¿Quieres crear el historial con fecha " + fechaHoy +
                    (perfilSeleccionado != null ? ", perfil asociado: " + perfilSeleccionado : ", sin perfil") +
                    (episodioSeleccionado != null ? ", episodio asociado: " + episodioSeleccionado : ", sin episodio") +
                    "? (True|False)");

            String confirmarString = sc.nextLine().trim();
            boolean confirmar;

            if (confirmarString.equalsIgnoreCase("true") || confirmarString.equalsIgnoreCase("false")) {
                confirmar = Boolean.parseBoolean(confirmarString);
            } else {
                System.out.println("No has introducido True o False. Operación cancelada.");
                return;
            }

            if (!confirmar) {
                System.out.println("No se creará el historial. Operación cancelada.");
                return;
            }

            // Crear y persistir el historial
            Historial nuevoHistorial = new Historial();
            nuevoHistorial.setFecha_reproduccion(fechaHoy);
            nuevoHistorial.setPerfil(perfilSeleccionado);
            nuevoHistorial.setEpisodio(episodioSeleccionado);

            session.persist(nuevoHistorial);
            tx.commit();

            System.out.println("Historial creado exitosamente: " + nuevoHistorial);

        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }


    private static int introduceEntero() {
        int numero;
        try {
            numero = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
        return numero;
    }

    public static void seriesMasVistas() {
        // Obtener las 5 series más vistas del catálogo
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List<Object[]> listaSeries = session.createQuery(
                            "SELECT s, COUNT(h.id) AS reproducciones " +
                                    "FROM Historial h " +
                                    "JOIN h.episodio e " +
                                    "JOIN e.serie s " +
                                    "GROUP BY s " +  // Agrupamos por Serie
                                    "ORDER BY reproducciones DESC",
                            Object[].class
                    ).setMaxResults(5) // En HQL no se puede usar LIMIT, se usa setMaxResults
                    .list();

            int i = 1;
            for (Object[] o : listaSeries) {
                Serie serie = (Serie) o[0];
                Long reproducciones = (Long) o[1];
                System.out.println(i + ". Nombre de la serie: " + serie.getTitulo() + " - " + reproducciones + " reproducciones");
                i++;
            }

        } catch (HibernateException ex) {
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void insertarCapitulos() {
        // Añadir al historial de un usuario a traves de su ID, todos los de una
        // serie a traves de su ID de golpe (con la fecha de hoy). Si hubiera
        // registrado algun capítulo de esa serie ya, solo se actualizaría la fecha
        //del día (o se borraría y insertaría uno nuevo).
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            System.out.println("Listado de usuarios registrados: ");
            List<Usuario> listaUsuarios = mostrarUsuarios();

            if (listaUsuarios.isEmpty()) {
                System.out.println("Lo sentimos, no hay usuarios disponibles. Operación cancelada");
                return;
            }

            for (Usuario usuario : listaUsuarios) {
                System.out.println(usuario);
            }

            System.out.print("Selecciona el ID de un usuario: ");
            long idUsuario = introduceEntero();

            if (idUsuario < 0) {
                System.out.println("El ID de usuario no cumple con el formato. Operación cancelada.");
                return;
            }

            Usuario usuarioBuscar = session.find(Usuario.class, idUsuario);

            if (usuarioBuscar == null) {
                System.out.println("No se pudo encontrar un usuario con el ID: " + idUsuario);
                return;
            }

            Perfil perfilBuscar = session.find(Perfil.class, usuarioBuscar.getId());

            if (perfilBuscar == null) {
                System.out.println("El usuario: " + usuarioBuscar + " no tiene un perfil asociado. Operación cancelada.");
                return;
            }

            List<Historial> historialesDePerfil = session.createQuery("FROM Historial h WHERE h.perfil.id = :idPerfil", Historial.class)
                    .setParameter("idPerfil", perfilBuscar.getId())
                    .getResultList();

            if (historialesDePerfil.isEmpty()) {
                System.out.println("El perfil no tiene ningún historial asociado. Operación cancelada.");
                return;
            }

            System.out.println("Listado de historiales registrados:");
            for (Historial historial : historialesDePerfil) {
                System.out.println(historial);
            }

            System.out.print("Selecciona el ID del historial que quieres modificar: ");
            long idHistorialModificar = introduceEntero();

            if (idHistorialModificar < 0) {
                System.out.println("El ID del historial no cumple con el formato. Operación cancelada");
                return;
            }

            Historial historialModificar = session.createQuery("SELECT h FROM Historial h WHERE h.perfil.id = :idPerfil AND h.id = :idHistorial", Historial.class)
                    .setParameter("idPerfil", perfilBuscar.getId())
                    .setParameter("idHistorial", idHistorialModificar)
                    .uniqueResult();


            if (historialModificar == null) {
                System.out.println("No se encontró un historial con perfil: " + perfilBuscar.getNombre() + ", e ID historial: " + idHistorialModificar);
                return;
            }

            System.out.println("¿Quieres insertar episodios a historial? (True|False)");
            String confirmarString = sc.nextLine().trim();
            boolean confirmar;

            if (confirmarString.equalsIgnoreCase("true") || confirmarString.equalsIgnoreCase("false")) {
                confirmar = Boolean.parseBoolean(confirmarString);
            } else {
                System.out.println("No has introducido True o False. Operación cancelada.");
                return;
            }

            if (!confirmar) {
                System.out.println("No se insertarán episodios. Operación cancelada.");
                return;
            }

            System.out.println("Listados de series registradas:");
            List<Serie> listaSeries = mostrarSeries();

            if (listaSeries.isEmpty()) {
                System.out.println("Lo sentimos, no hay series disponibles. Operación cancelada");
                return;
            }

            for (Serie serie : listaSeries) {
                System.out.println(serie);
            }
            System.out.print("Selecciona el ID de una serie: ");
            long idSerieBuscar = introduceEntero();

            if (idSerieBuscar < 0) {
                System.out.println("El ID de la serie no cumpple con el formato. Operación cancelada");
                return;
            }

            Serie serieBuscar = session.find(Serie.class, idSerieBuscar);

            if (serieBuscar == null) {
                System.out.println("No se pudo encontrar una serie con el ID: " + idSerieBuscar);
                return;
            }

            List<Episodio> episodiosDeSerie = session.createQuery("SELECT e FROM Episodio e WHERE e.serie.id = :idSerie", Episodio.class)
                    .setParameter("idSerie", serieBuscar.getId())
                    .getResultList();

            if (episodiosDeSerie.isEmpty()) {
                System.out.println("No hay episodios asociados a esta serie. Operación cancelada.");
                return;
            }

            System.out.println("Episodios que están en la serie: " + serieBuscar);
            for (Episodio episodio : episodiosDeSerie) {
                System.out.println(episodio);
            }
            System.out.println("¿Quieres añadir estos episodios al historial: " + historialModificar + "? (True|False)");
            String confirmarEpisodiosString = sc.nextLine().trim();
            boolean confirmarEpisodios;

            if (confirmarEpisodiosString.equalsIgnoreCase("true") || confirmarEpisodiosString.equalsIgnoreCase("false")) {
                confirmarEpisodios = Boolean.parseBoolean(confirmarEpisodiosString);
            } else {
                System.out.println("No has introducido True o False. Operación cancelada.");
                return;
            }

            if (!confirmarEpisodios) {
                System.out.println("No se añadirán episodios al historial. Operación cancelada.");
                return;
            }

            LocalDateTime fechaHoy = LocalDateTime.now();

            // Obtenemos los episodios que hay en el historial (el ID)
            List<Long> episodiosEnHistorial = session.createQuery(
                            "SELECT h.episodio.id FROM Historial h WHERE h.perfil.id = :idPerfil", Long.class)
                    .setParameter("idPerfil", perfilBuscar.getId())
                    .getResultList();

            for (Episodio episodio : episodiosDeSerie) {
                if (episodiosEnHistorial.contains(episodio.getId())) {
                    // Si el episodio ya está en el historial, actualizar la fecha de reproducción
                    Historial historialExistente = session.createQuery(
                                    "SELECT h FROM Historial h WHERE h.perfil.id = :idPerfil AND h.episodio.id = :idEpisodio", Historial.class)
                            .setParameter("idPerfil", perfilBuscar.getId())
                            .setParameter("idEpisodio", episodio.getId())
                            .uniqueResult();

                    if (historialExistente != null) {
                        historialExistente.setFecha_reproduccion(fechaHoy);
                    }
                } else {
                    // Si el episodio no está en el historial, insertarlo
                    Historial nuevoHistorial = new Historial();
                    nuevoHistorial.setPerfil(perfilBuscar);
                    nuevoHistorial.setEpisodio(episodio);
                    nuevoHistorial.setFecha_reproduccion(fechaHoy);

                    session.persist(nuevoHistorial);
                }
            }

            tx.commit();
            System.out.println("Episodios añadidos o actualizados en el historial correctamente.");

        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

}