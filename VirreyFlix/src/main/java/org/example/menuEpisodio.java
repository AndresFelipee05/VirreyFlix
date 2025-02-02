package org.example;

import org.example.model.Episodio;
import org.example.model.Serie;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.example.menuSerie.mostrarSeries;


public class menuEpisodio {
    public static final Scanner sc = new Scanner(System.in);

    public static void menuEpisodio() {
        int opcion;
        do {
            System.out.println("\n --- Menú de Episodio ---");
            System.out.println("1. Mostrar datos de Episodio");
            System.out.println("2. Insertar Episodio");
            System.out.println("3. Modificar datos de Episodio");
            System.out.println("4. Eliminar Episodio");
            System.out.println("5. Mostrar Episodios");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elige una opción: ");
            opcion = introduceEntero();

            switch (opcion) {
                case 1 -> {
                    System.out.println("Mostrando datos de episodio...");
                    buscarEpisodioPorID();
                }
                case 2 -> {
                    insertarEpisodio();
                }

                case 3 -> {
                    modificarEpisodio();
                }
                case 4 -> {
                    eliminarEpisodio();
                }

                case 5 -> {
                    System.out.println("Mostrando los Episodios registrados");
                    System.out.println(mostrarEpisodios());
                }

                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción no válida. Por favor, elige una opción del menú.");
            }
        } while (opcion != 0);
    }

    public static void insertarEpisodio() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            System.out.println("Introduce el titulo del episodio:");
            String titulo = sc.nextLine();

            if (titulo.isEmpty()) {
                System.out.println("El titulo no puede ser vacío. Operación cancelada.");
                return;
            }

            System.out.print("Introduce la duración: ");
            int duracion = introduceEntero();

            if (duracion < 0) {
                System.out.println("El formato de la duración no es válido. Operación cancelada.");
                return;
            }

            // Mostrar las series disponibles
            System.out.println("Listado de series registradas:");
            List<Serie> series = mostrarSeries();

            if (series.isEmpty()) {
                System.out.println("No hay series registardas. ¿Quieres crear el episodio sin serie asociada? (True|False)");
                String confirmarString = sc.nextLine().trim();
                boolean confirmar;

                if (confirmarString.equalsIgnoreCase("true") || confirmarString.equalsIgnoreCase("false")) {
                    confirmar = Boolean.parseBoolean(confirmarString);
                } else {
                    System.out.println("No has introducido True o False. Operación cancelada.");
                    return;
                }

                if (confirmar) {
                    // Creamos el episodio sin serie
                    System.out.println("¿Quieres crear el episodio con titulo " + titulo + " y duración " + duracion + "? (True|False)");
                    String confirmarCreacionString = sc.nextLine().trim();
                    boolean confirmarCreacion;

                    if (confirmarCreacionString.equalsIgnoreCase("true") || confirmarCreacionString.equalsIgnoreCase("false")) {
                        confirmarCreacion = Boolean.parseBoolean(confirmarCreacionString);
                    } else {
                        System.out.println("No has introducido True o False. Operación cancelada.");
                        return;
                    }

                    if (!confirmarCreacion) {
                        System.out.println("No se creará el episodio. Operación cancelada");
                        return;
                    } else {
                        Episodio episodioNuevo = new Episodio(titulo, duracion);
                        session.persist(episodioNuevo);
                        tx.commit();

                        System.out.println("El episodio ha sido creado: " + episodioNuevo);
                        return;
                    }
                } else {
                    System.out.println("No se creará el episodio. Operación cancelada.");
                    return;
                }
            }

            for (Serie s : series) {
                System.out.println(s);
            }

            System.out.print("Selecciona el ID de una serie (o introduce -1 para no asociar serie):");
            long idSerieBuscar = introduceEntero();

            Serie serieBuscar = null;
            if (idSerieBuscar != -1) {
                if (idSerieBuscar < 0) {
                    System.out.println("El ID de la serie no cumple el formato. Operación cancelada.");
                    return;
                }

                serieBuscar = session.find(Serie.class, idSerieBuscar);

                if (serieBuscar == null) {
                    System.out.println("No se pudo encontrar una serie con el ID: " + idSerieBuscar);
                }
            }

            System.out.println("¿Quieres crear el episodio con titulo " + titulo + ", duración " + duracion +
                    (serieBuscar != null ? " y serie asociada " + serieBuscar : " sin serie asociada") + "? (True|False)");
            String confirmarString = sc.nextLine().trim();
            boolean confirmar;

            if (confirmarString.equalsIgnoreCase("true") || confirmarString.equalsIgnoreCase("false")) {
                confirmar = Boolean.parseBoolean(confirmarString);
            } else {
                System.out.println("No has introducido True o False. Operación cancelada.");
                return;
            }

            if (!confirmar) {
                System.out.println("No se creará el episodio");
                return;
            } else {
                Episodio episodioNuevo = new Episodio(titulo, duracion);
                if (serieBuscar != null) {
                    episodioNuevo.setSerie(serieBuscar);
                }
                session.persist(episodioNuevo);
                tx.commit();

                System.out.println("Episodio creado: " + episodioNuevo);
            }
        } catch (
                HibernateException ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void eliminarEpisodio() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            System.out.println("Listado de episodios registrados:");
            List<Episodio> episodios = mostrarEpisodios();

            if (episodios.isEmpty()) {
                System.out.println("Lo sentimos, no hay episodios registrados. Operación cancelada");
                return;
            }

            for (Episodio episodio : episodios) {
                System.out.println(episodio);
            }

            System.out.print("Introduce el ID del episodio para eliminarlo: ");
            long idBuscar = introduceEntero();

            if (idBuscar < 0) {
                System.out.println("El ID no cumple con el formato. Operación cancelada.");
                return;
            }

            Episodio episodioBuscar = session.find(Episodio.class, idBuscar);

            if (episodioBuscar == null) {
                System.out.println("No se pudo encontar un episodio con el ID: " + idBuscar);
                return;
            } else {
                System.out.println("Episodio encontrado.");
                System.out.println("¿Quieres eliminar el episodio: " + episodioBuscar + "? (True|False)");
                String confirmarString = sc.nextLine().trim();
                boolean confirmar;

                if (confirmarString.equalsIgnoreCase("true") || confirmarString.equalsIgnoreCase("false")) {
                    confirmar = Boolean.parseBoolean(confirmarString);
                } else {
                    System.out.println("No has introducido True o False. Operación cancelada.");
                    return;
                }

                if (!confirmar) {
                    System.out.println("No se eliminará el episodio.");
                    return;
                } else {
                    session.delete(episodioBuscar);
                    tx.commit();

                    System.out.println("El episodio ha sido eliminado");
                }
            }
        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void buscarEpisodioPorID() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            System.out.print("Introduce el ID del episodio para modificarlo: ");
            long idBuscar = introduceEntero();

            if (idBuscar < 0) {
                System.out.println("El ID no cumple con el formato. Operación cancelada.");
                return;
            }

            Episodio episodioBuscar = session.find(Episodio.class, idBuscar);

            if (episodioBuscar == null) {
                System.out.println("No se pudo encontrar el episodio con el ID: " + idBuscar);
                return;
            } else {
                System.out.println("Episodio encontrado: " + episodioBuscar);
            }
        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void modificarEpisodio() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            System.out.println("Listado de episodios disponibles:");
            List<Episodio> episodios = mostrarEpisodios();

            if (episodios.isEmpty()) {
                System.out.println("Lo sentimos, no hay episodios registrados. Operación cancelada");
                return;
            }

            for (Episodio episodio : episodios) {
                System.out.println(episodio);
            }

            System.out.print("Introduce el ID del episodio para modificarlo: ");
            long idBuscar = introduceEntero();

            if (idBuscar < 0) {
                System.out.println("El ID no cumple con el formato. Operación cancelada.");
                return;
            }

            Episodio episodioBuscar = session.find(Episodio.class, idBuscar);

            if (episodioBuscar == null) {
                System.out.println("No se pudo encontrar un episodio con el ID: " + idBuscar);
                return;
            }

            System.out.println("Episodio encontrado. Sus datos: " + episodioBuscar);

            // Modificar título
            System.out.println("Introduce el nuevo título (o deja en blanco para mantener el actual):");
            String tituloNuevo = sc.nextLine().trim();
            if (!tituloNuevo.isEmpty()) {
                episodioBuscar.setTitulo(tituloNuevo);
            }

            System.out.println("Introduce la nueva duración (o introduce -1 para mantener la actual):");
            int duracionNueva = introduceEntero();
            if (duracionNueva >= 0) {
                episodioBuscar.setDuracion(duracionNueva);
            }

            System.out.println("¿Quieres cambiar la serie asociada? (True|False)");
            String cambiarSerieString = sc.nextLine().trim();
            boolean cambiarSerie;

            if (cambiarSerieString.equalsIgnoreCase("true") || cambiarSerieString.equalsIgnoreCase("false")) {
                cambiarSerie = Boolean.parseBoolean(cambiarSerieString);
            } else {
                System.out.println("No has introducido True o False. Operación cancelada.");
                return;
            }

            if (cambiarSerie) {
                System.out.println("Listado de series registradas:");
                List<Serie> series = mostrarSeries();

                if (series.isEmpty()) {
                    System.out.println("No hay series disponibles. Operación cancelada");
                    return;
                }

                for (Serie s : series) {
                    System.out.println(s);
                }
                System.out.print("Introduce el ID de la serie nueva (o introduce -1 para mantener la actual): ");
                int idSerie = introduceEntero();

                if (idSerie != -1) {
                    Serie serieBuscar = session.find(Serie.class, idSerie);
                    if (serieBuscar != null) {
                        episodioBuscar.setSerie(serieBuscar);
                    } else {
                        System.out.println("No se pudo encontrar una serie con el ID: " + idSerie);
                    }
                }
            }

            // Confirmación antes de aplicar los cambios
            System.out.println("¿Quieres que el episodio tenga de título: " + episodioBuscar.getTitulo() + ", serie asociada: " + episodioBuscar.getSerie() + " y duración: " + episodioBuscar.getDuracion() + "? (True|False)");
            String confirmarString = sc.nextLine().trim();
            boolean confirmar;
            if (confirmarString.equalsIgnoreCase("true") || confirmarString.equalsIgnoreCase("false")) {
                confirmar = Boolean.parseBoolean(confirmarString);
            } else {
                System.out.println("No has introducido True o False. Operación cancelada.");
                return;
            }

            if (confirmar) {
                // Aplicar los cambios
                tx.commit();
                System.out.println("Episodio actualizada con éxito: " + episodioBuscar);
            } else {
                System.out.println("No se harán cambios.");
                return;
            }
        } catch (
                HibernateException ex) {
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

    public static ArrayList<Episodio> mostrarEpisodios() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        ArrayList<Episodio> listaEpisodios = new ArrayList<>();

        try {
            tx = session.beginTransaction();

            // Consulta SQL para obtener todos los registros de la tabla Episodio.
            List<Episodio> episodios = session.createQuery("FROM Episodio", Episodio.class).list();

            for (Episodio e : episodios) {
                Hibernate.initialize(e.getSerie()); // Cargar las series antes de cerrar sesion (Sino da error)
                if (e.getSerie() != null) {
                    Hibernate.initialize(e.getSerie().getGeneros()); // Cargar los géneros también (Sino da error)
                }
            }

            if (episodios.isEmpty()) {
                System.out.println("La lista está vacía. No hay episodios que mostrar");
                return listaEpisodios;
            } else {
                listaEpisodios.addAll(episodios);
            }
            tx.commit();
        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
        return listaEpisodios;
    }
}
