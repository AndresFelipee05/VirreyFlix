package org.example;

import org.example.model.Episodio;
import org.example.model.Genero;
import org.example.model.Serie;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class menuSerie {
    public final static Scanner sc = new Scanner(System.in);

    public static void menuSerie() {
        int opcion;
        do {
            System.out.println("\n --- Menú de Serie ---");
            System.out.println("1. Mostrar datos de Serie");
            System.out.println("2. Insertar Serie");
            System.out.println("3. Modificar datos de Serie");
            System.out.println("4. Eliminar Serie");
            System.out.println("5. Mostrar Series");
            System.out.println("6. Insertar Género");
            System.out.println("7. Mostrar Géneros");
            System.out.println("8. Series según la edad");
            System.out.println("9. Episodios de una serie");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elige una opción: ");
            opcion = introduceEntero();

            switch (opcion) {
                case 1 -> {
                    System.out.println("Mostrando datos de la serie...");
                    buscarSeriePorId();
                }
                case 2 -> {
                    insertarSerie();
                }

                case 3 -> {
                    modificarSeries();
                }

                case 4 -> {
                    eliminarSerie();
                }

                case 5 -> {
                    System.out.println("Mostrando las series registradas...");
                    System.out.println(mostrarSeries());
                }

                case 6 -> {
                    insertarGenero();
                }

                case 7 -> {
                    System.out.println("Mostrando los generos...");
                    System.out.println(mostrarGeneros());

                }

                case 8 -> {
                    seriesPorEdad();
                }

                case 9 -> {
                    episodiosPorSerie();
                }

                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción no válida. Por favor, elige una opción del menú.");
            }
        } while (opcion != 0);
    }


    public static void insertarGenero() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            System.out.println("Introduce el nombre del género:");
            String nombreGenero = sc.nextLine().trim();

            if (nombreGenero.isEmpty()) {
                System.out.println("El nombre del género no puede estar vacío. Operación cancelada.");
                return;
            }

            Genero nuevoGenero = new Genero(nombreGenero);
            session.persist(nuevoGenero);
            tx.commit();

            System.out.println("Género creado con éxito: " + nuevoGenero);

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static ArrayList<Genero> mostrarGeneros() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        ArrayList<Genero> listaGeneros = new ArrayList<>();
        try {
            tx = session.beginTransaction();
            List<Genero> generos = session.createQuery("FROM Genero", Genero.class).list();

            if (generos.isEmpty()) {
                System.out.println("La lista está vacía. No hay géneros registrados.");
            } else {
                // Añadir géneros a la lista
                listaGeneros.addAll(generos);
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return listaGeneros;
    }

    public static void insertarSerie() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            System.out.println("Introduce el titulo de la serie:");
            String titulo = sc.nextLine();

            if (titulo.isEmpty()) {
                System.out.println("El titulo no puede ser vacío. Operación cancelada.");
                return;
            }

            System.out.print("Introduce la calificación de edad: ");
            int calificacion_edad = introduceEntero();

            if (calificacion_edad < 0) {
                System.out.println("La calificación de la edad no es válida. Operación cancelada.");
                return;
            }

            // Mostrar los géneros disponibles
            System.out.println("Listado de géneros registrados:");
            List<Genero> generos = mostrarGeneros();

            if (generos.isEmpty()) {
                System.out.println("No hay géneros registrados. ¿Quieres crear una serie sin género? (True|False)");
                String confirmarString = sc.nextLine().trim();
                boolean confirmar;

                if (confirmarString.equalsIgnoreCase("true") || confirmarString.equalsIgnoreCase("false")) {
                    confirmar = Boolean.parseBoolean(confirmarString);
                } else {
                    System.out.println("No has introducido True o False. Operación cancelada.");
                    return;
                }

                if (confirmar) {
                    // El usuario ha decidido crear la serie sin género
                    System.out.println("¿Quieres crear la serie con titulo " + titulo + " y calificación de edad " + calificacion_edad + "? (True|False)");
                    String confirmarCrearString = sc.nextLine().trim();
                    boolean confirmaCrear;

                    if (confirmarCrearString.equalsIgnoreCase("true") || confirmarCrearString.equalsIgnoreCase("false")) {
                        confirmaCrear = Boolean.parseBoolean(confirmarString);
                    } else {
                        System.out.println("No has introducido True o False. Operación cancelada");
                        return;
                    }

                    if (!confirmaCrear) {
                        System.out.println("No se creará la serie");
                        return;
                    } else {

                        Serie nuevaSerie = new Serie(titulo, calificacion_edad);
                        session.persist(nuevaSerie);
                        tx.commit();

                        System.out.println("La serie ha sido creada sin género: " + nuevaSerie);
                        return;
                    }
                } else {
                    System.out.println("Operación cancelada.");
                    return;
                }
            }

            for (Genero g : generos) {
                System.out.println(g);
            }

            // El usuario selecciona el género
            System.out.println("Selecciona el ID de un género (o introduce -1 para no asociar género):");
            long idGeneroBuscar = introduceEntero();

            Genero generoBuscar = null;
            if (idGeneroBuscar != -1) {
                if (idGeneroBuscar < 0) {
                    System.out.println("El ID del género no cumple con el formato. Operación cancelada.");
                    return;
                }

                generoBuscar = session.find(Genero.class, idGeneroBuscar);

                if (generoBuscar == null) {
                    System.out.println("No se pudo encontrar un género con el ID: " + idGeneroBuscar);
                    return;
                }
            }

            // Confirmación antes de crear la serie
            System.out.println("¿Quieres crear la serie con titulo " + titulo + ", calificación de edad " + calificacion_edad +
                    (generoBuscar != null ? " y género: " + generoBuscar : " sin género") + "? (True|False)");
            String confirmarString = sc.nextLine().trim();
            boolean confirmar;

            if (confirmarString.equalsIgnoreCase("true") || confirmarString.equalsIgnoreCase("false")) {
                confirmar = Boolean.parseBoolean(confirmarString);
            } else {
                System.out.println("No has introducido True o False. Operación cancelada.");
                return;
            }

            if (!confirmar) {
                System.out.println("No se creará la serie.");
                return;
            } else {
                // Crear la serie con o sin género
                Serie nuevaSerie = new Serie(titulo, calificacion_edad);
                if (generoBuscar != null) {
                    nuevaSerie.getGeneros().add(generoBuscar);
                }
                session.persist(nuevaSerie);
                tx.commit();

                System.out.println("La serie ha sido creada: " + nuevaSerie);
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void buscarSeriePorId() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            System.out.print("Intoduce el ID de la serie para buscarla: ");
            long idBuscar = introduceEntero();

            if (idBuscar < 0) {
                System.out.println("El ID no cumple con el formato. Operación cancelada.");
                return;
            }

            Serie serie = session.find(Serie.class, idBuscar);

            if (serie == null) {
                System.out.println("No se ha pudo encontrar la serie con ID : " + idBuscar);
                return;
            } else {
                System.out.println("Serie encontrada. Sus datos: " + serie);
            }
        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
        } finally {
            session.close();
        }
    }

    public static void eliminarSerie() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            System.out.println("Listado de series disponibles:");
            List<Serie> series = mostrarSeries();

            if (series.isEmpty()) {
                System.out.println("Lo sentimos, no hay series registradas. Operación cancelada.");
                return;
            }

            for (Serie serie : series) {
                System.out.println(serie);
            }

            System.out.print("Introduce el ID de la serie para eliminarla: ");
            long idBuscar = introduceEntero();

            if (idBuscar < 0) {
                System.out.println("El ID no cumple con el formato. Operación cancelada.");
                return;
            }

            Serie serieBuscar = session.find(Serie.class, idBuscar);

            if (serieBuscar == null) {
                System.out.println("No se pudo encontrar una serie con ID: " + idBuscar);
                return;
            } else {
                System.out.println("Serie encontrada");
                System.out.println("¿Quieres eliminar la serie " + serieBuscar + "? (True|False)");
                String confirmarString = sc.nextLine().trim();
                boolean confirmar;

                if (confirmarString.equalsIgnoreCase("true") || confirmarString.equalsIgnoreCase("false")) {
                    confirmar = Boolean.parseBoolean(confirmarString);
                } else {
                    System.out.println("No has introducido True o False. Operación cancelada.");
                    return;
                }

                if (!confirmar) {
                    System.out.println("No se eliminará la serie.");
                    return;
                } else {
                    session.delete(serieBuscar);
                    tx.commit();

                    System.out.println("La serie ha sido eliminada.");
                }
            }
        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
        } finally {
            session.close();
        }
    }

    public static void modificarSeries() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            System.out.println("Listado de series registradas:");
            List<Serie> series = mostrarSeries();

            if (series.isEmpty()) {
                System.out.println("Lo sentimos, no hay series registradas. Operación cancelada.");
                return;
            }

            for (Serie s : series) {
                System.out.println(s);
            }
            System.out.print("Introduce el ID de la serie para modificarla: ");
            long idBuscar = introduceEntero();

            if (idBuscar < 0) {
                System.out.println("El formato del ID no es válido. Operación cancelada.");
                return;
            }

            Serie serie = session.find(Serie.class, idBuscar);

            if (serie == null) {
                System.out.println("No se pudo encontrar una serie con el ID: " + idBuscar);
                return;
            }

            System.out.println("Serie encontrada. Sus datos: " + serie);

            // Modificar título
            System.out.println("Introduce el nuevo título (o deja en blanco para mantener el actual):");
            String tituloNuevo = sc.nextLine().trim();
            if (!tituloNuevo.isEmpty()) {
                serie.setTitulo(tituloNuevo);
            }

            // Modificar calificación de edad
            System.out.println("Introduce la nueva calificación de edad (o -1 para mantener la actual):");
            int calificacionNueva = introduceEntero();
            if (calificacionNueva >= 0) {
                serie.setCalificacionEdad(calificacionNueva);
            }

            // Modificar género
            System.out.println("¿Quieres cambiar el género de la serie? (True|False):");
            String cambiarGeneroString = sc.nextLine().trim();
            boolean cambiarGenero;

            if (cambiarGeneroString.equalsIgnoreCase("true") || cambiarGeneroString.equalsIgnoreCase("false")) {
                cambiarGenero = Boolean.parseBoolean(cambiarGeneroString);
            } else {
                System.out.println("No has introducido True o False. Operación cancelada.");
                return;
            }

            if (cambiarGenero) {
                System.out.println("Listado de géneros registrados:");
                List<Genero> generos = mostrarGeneros();

                if (generos.isEmpty()) {
                    System.out.println("No hay géneros disponibles. Operación cancelada.");
                    return;
                }

                for (Genero g : generos) {
                    System.out.println(g);
                }

                System.out.print("Introduce el ID del nuevo género (o -1 para mantener el actual): ");
                long idGeneroNuevo = introduceEntero();

                if (idGeneroNuevo != -1) {
                    Genero generoNuevo = session.find(Genero.class, idGeneroNuevo);
                    if (generoNuevo != null) {
                        serie.getGeneros().add(generoNuevo);
                    } else {
                        System.out.println("No se encontró un género con el ID: " + idGeneroNuevo + ". Manteniendo el género actual.");
                    }
                }
            }

            // Confirmación antes de aplicar los cambios
            System.out.println("¿Quieres que la serie tenga título: " + serie.getTitulo() + ", género: " + serie.getGeneros() + " y calificación de edad: " + serie.getCalificacionEdad() + "? (True|False)");
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
                System.out.println("Serie actualizada con éxito: " + serie);
            } else {
                System.out.println("No se harán cambios.");
                return;
            }
        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static ArrayList<Serie> mostrarSeries() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        ArrayList<Serie> listaSeries = new ArrayList<>();

        try {
            tx = session.beginTransaction();

            // Consulta SQL para obtener todos los registros de la tabla Serie
            List<Serie> series = session.createQuery("FROM Serie", Serie.class).list();

            for (Serie s : series) {
                Hibernate.initialize(s.getGeneros()); // Cargar los generos antes de cerrar sesion (Sino da error)
            }

            if (series.isEmpty()) {
                System.out.println("La lista está vacía. No hay series registradas.");
                return listaSeries;
            } else {
                listaSeries.addAll(series);
            }
            tx.commit();
        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }

        return listaSeries;
    }

    public static void seriesPorEdad() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {

            System.out.print("Introduce una edad: ");
            int edad = introduceEntero();

            if (edad < 0) {
                System.out.println("La edad no cumple el formato. Operación cancelada");
                return;
            }

            List<Serie> series = session.createQuery("FROM Serie", Serie.class).list();

            if (series.isEmpty()) {
                System.out.println("No hay series registradas.");
                return;
            }

            System.out.println("Series permitidas con la edad: " + edad);
            for (Serie serie : series) {
                if (serie.getCalificacionEdad() <= edad) {
                    System.out.println("Serie: " + serie.getTitulo() + ", calificación de edad " + serie.getCalificacionEdad());
                }
            }
        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void episodiosPorSerie() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            System.out.println("Listado de series creadas: ");
            List<Serie> series = mostrarSeries();

            if (series.isEmpty()) {
                System.out.println("Lo sentimos, no hay series registradas. Operación cancelada");
                return;
            }

            for (Serie s : series) {
                System.out.println(s);
            }

            System.out.print("Introduce el ID de la serie para ver sus capítulos: ");
            long idSerie = introduceEntero();

            if (idSerie < 0) {
                System.out.println("El ID de la serie no cumple con el formato. Operación cancelada.");
                return;
            }

            Serie serie = session.find(Serie.class, idSerie);
            List<Episodio> episodios = session.createQuery("FROM Episodio", Episodio.class).list();

            if (episodios.isEmpty()) {
                System.out.println("No hay episodios registrados.");
                return;
            }

            System.out.println("Capítulos de la serie con nombre: " + serie.getTitulo() + " y ID: " + serie.getId());
            for (Episodio episodio : episodios) {
                if (episodio.getSerie().getId() == serie.getId()) {
                    System.out.println(episodio);
                }
            }
        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static int introduceEntero() {
        int numero;
        try {
            numero = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
        return numero;
    }
}
