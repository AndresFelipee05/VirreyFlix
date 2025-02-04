package org.example;

import org.example.model.Perfil;
import org.example.model.Usuario;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class menuUsuario {

    public final static Scanner sc = new Scanner(System.in);

    public static void menuUsuario() {
        int opcion;
        do {
            System.out.println("\n --- Menú de Usuario ---");
            System.out.println("1. Mostrar datos de Usuario");
            System.out.println("2. Insertar Usuario");
            System.out.println("3. Modificar datos de Usuario");
            System.out.println("4. Eliminar Usuario");
            System.out.println("5. Mostrar todos los usuarios");
            System.out.println("6. Capitulos vistos por usuario");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elige una opción: ");
            opcion = introduceEntero();

            switch (opcion) {
                case 1 -> {
                    System.out.println("Mostrando datos de usuario...");
                    buscarUsuarioPorID();
                }

                case 2 -> {
                    insertarUsuario();
                }

                case 3 -> {
                    modificarUsuario();
                }

                case 4 -> {
                    eliminarUsuario();
                }

                case 5 -> {
                    System.out.println("Mostrando los usuarios registrados...");
                    System.out.println(mostrarUsuarios());
                }

                case 6 -> {
                    capitulosVistos();
                }
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción no válida. Por favor, elige una opción del menú.");
            }
        } while (opcion != 0);
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

    public static void buscarUsuarioPorID() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            System.out.print("Introduce el id del usuario a buscar: ");
            long id;

            try {
                id = Long.parseLong(sc.nextLine());
            } catch (NumberFormatException ex) {
                System.out.println("Formato del ID no válido. Operación cancelada.");
                return;
            }

            Usuario usuario = session.find(Usuario.class, id);

            if (usuario == null) {
                System.out.println("No se pudo encontrar al usuario con el id " + id);
                return;
            } else {
                System.out.println("Usuario encontrado: " + usuario);
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void insertarUsuario() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            System.out.println("Introduce el nombre de usuario:");
            String nombre = sc.nextLine();

            if (nombre.isEmpty()) {
                System.out.println("El nombre del usuario no puede ser vacío. Operación cancelada.");
                return;
            }

            System.out.println("Introduce el email de usuario:");
            String email = sc.nextLine();

            if (email.isEmpty()) {
                System.out.println("El email no puede ser vacío. Operación cancelada.");
                return;
            }

            System.out.println("¿Quieres crear al usuario con nombre " + nombre + " y email " + email + "? (True|False)");
            String confirmarString = sc.nextLine().trim();
            boolean confirmar;

            if (confirmarString.equalsIgnoreCase("true") || confirmarString.equalsIgnoreCase("false")) {
                confirmar = Boolean.parseBoolean(confirmarString);
            } else {
                System.out.println("No has introducido True o False. Operación cancelada");
                return;
            }

            if (!confirmar) {
                System.out.println("No se creará el usuario. Operación cancelada.");
                return;
            } else {
                Usuario usuarioNuevo = new Usuario(nombre, email);
                session.persist(usuarioNuevo);

                System.out.println("El usuario ha sido creado: " + usuarioNuevo);
                tx.commit();
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void eliminarUsuario() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            System.out.println("Listado de usuarios registrados:");
            List<Usuario> usuarios = mostrarUsuarios();

            if (usuarios.isEmpty()) {
                System.out.println("Lo sentimos, no hay usuarios registrados, operación cancelada.");
                return;
            }

            for (Usuario u : usuarios) {
                System.out.println(u);
            }

            System.out.print("Introduce el id del usuario a eliminar: ");
            long id;

            try {
                id = Long.parseLong(sc.nextLine());
            } catch (NumberFormatException ex) {
                System.out.println("Formato del ID no válido. Operación cancelada.");
                return;
            }

            Usuario usuario = session.find(Usuario.class, id);

            if (usuario == null) {
                System.out.println("No se pudo encontrar al usuario con el id " + id);
                return;
            } else {
                System.out.println("Usuario encontrado.");
                System.out.println("¿Quieres eliminar al usuario con nombre " + usuario.getNombre() + " y email " + usuario.getEmail() + "? (True|False)");
                String confirmarString = sc.nextLine().trim();
                boolean confirmar;

                if (confirmarString.equalsIgnoreCase("true") || confirmarString.equalsIgnoreCase("false")) {
                    confirmar = Boolean.parseBoolean(confirmarString);
                } else {
                    System.out.println("No has introducido True o False. Operación cancelada.");
                    return;
                }

                if (!confirmar) {
                    System.out.println("No se eliminará al usuario. Operación cancelada.");
                    return;
                } else {
                    session.delete(usuario);
                    System.out.println("El usuario ha sido eliminado.");
                    tx.commit();
                }
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void modificarUsuario() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            System.out.println("Listado de usuarios registrados:");
            List<Usuario> usuarios = mostrarUsuarios();

            if (usuarios.isEmpty()) {
                System.out.println("Lo sentimos, no hay usuarios registrados. Operación cancelada.");
                return;
            }

            for (Usuario u : usuarios) {
                System.out.println(u);
            }
            System.out.print("Introduce el id del usuario a modificar: ");
            long id = introduceEntero();

            if (id < 0) {
                System.out.println("El ID no cumple el formato. Operación cancelada.");
                return;
            }

            Usuario usuario = session.find(Usuario.class, id);

            if (usuario == null) {
                System.out.println("No se pudo encontrar al usuario con el id " + id);
                return;
            } else {
                System.out.println("Usuario encontrado. Sus datos: " + usuario);

                // Modificar el nombre del usuario
                System.out.println("Introduce el nuevo nombre de usuario (o deja en blanco para mantener el actual):");
                String nombreNuevo = sc.nextLine().trim();
                if (!nombreNuevo.isEmpty()) {
                    usuario.setNombre(nombreNuevo);
                }

                // Modificar el email del usuario
                System.out.println("Introduce el nuevo email del usuario (o deja en blanco para mantener el actual):");
                String emailNuevo = sc.nextLine().trim();
                if (!emailNuevo.isEmpty()) {
                    usuario.setEmail(emailNuevo);
                }

                if (nombreNuevo.isEmpty() && emailNuevo.isEmpty()) {
                    System.out.println("No hay datos nuevos. No habrá cambios.");
                    return;
                }

                System.out.print("¿Quieres que el usuario tenga de nombre: " + usuario.getNombre() + " y de email: " + usuario.getEmail() + "? (True|False): ");
                String confirmaString = sc.nextLine().trim();
                boolean confirmar;

                if (confirmaString.equalsIgnoreCase("true") || confirmaString.equalsIgnoreCase("false")) {
                    confirmar = Boolean.parseBoolean(confirmaString);
                } else {
                    System.out.println("No has introducido True o False. Operación cancelada.");
                    return;
                }

                if (!confirmar) {
                    System.out.println("No se realizarán cambios.");
                    return;
                } else {
                    System.out.println("Los datos han sido modificados.");
                    tx.commit();
                    System.out.println("Nuevos datos del usuario: " + usuario);
                }
            }
        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static ArrayList<Usuario> mostrarUsuarios() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        ArrayList<Usuario> listaUsuarios = new ArrayList<>();

        try {
            tx = session.beginTransaction();

            // Consulta SQL para obtener todos los registros de la tabla Usuario
            List<Usuario> usuarios = session.createQuery("FROM Usuario", Usuario.class).list();

            if (usuarios.isEmpty()) {
                System.out.println("La lista está vacía. No hay usuarios registrados.");
                return listaUsuarios;
            } else {
                // Añadir usuarios a la lista
                listaUsuarios.addAll(usuarios);
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return listaUsuarios;
    }

    public static void capitulosVistos() {
        // Mostrar los capítulos que ha visto un usuario a partir de un ID: título de la serie, nombre del capítulo, duracion, y fecha de reproduccion.
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            System.out.println("Listado de usuarios registrados: ");
            List<Usuario> usuarios = mostrarUsuarios();

            if (usuarios.isEmpty()) {
                System.out.println("Lo sentimos, no hay usuarios disponibles. Operación cancelada.");
                return;
            }

            for (Usuario usuario : usuarios) {
                System.out.println(usuario);
            }
            System.out.print("Selecciona el ID de un usuario: ");
            long idUsuario = introduceEntero();

            if (idUsuario < 0) {
                System.out.println("El ID del usuario no cumple el formato. Operación cancelada.");
                return;
            }

            // Obtener el perfil del usuario
            Perfil perfilBuscar = session.createQuery("FROM Perfil pf WHERE pf.usuario.id = :idUsuario", Perfil.class)
                    .setParameter("idUsuario", idUsuario)
                    .uniqueResult();

            if (perfilBuscar == null) {
                System.out.println("No se pudo encontrar un Perfil con el ID de usuario: " + idUsuario);
                return;
            }

            // Consultamos los episodios que ha visto el usuario
            List<Object[]> resultados = session.createNativeQuery(
                            "SELECT s.titulo, e.titulo, e.duracion, h.fecha_reproduccion " +
                                    "FROM historial h " +
                                    "JOIN episodio e ON h.episodio_id = e.id " +
                                    "JOIN serie s ON e.serie_id = s.id " +
                                    "WHERE h.perfil_id = :perfilId " +
                                    "ORDER BY h.fecha_reproduccion DESC",
                            Object[].class)
                    .setParameter("perfilId", perfilBuscar.getId())
                    .list();

            if (resultados.isEmpty()) {
                System.out.println("No se han encontrado episodios vistos por el usuario");
                return;
            }

            for (Object[] o : resultados) {
                String tituloSerie = (String) o[0];
                String tituloEpisodio = (String) o[1];
                int duracion = (Integer) o[2];
                Timestamp timestamp = (Timestamp) o[3];

                LocalDateTime fechaReproduccion = timestamp.toLocalDateTime();

                System.out.println("Serie: " + tituloSerie + ", nombre del episodio: " + tituloEpisodio +
                        ", Duración: " + duracion + " min, Fecha de reproducción: " + fechaReproduccion);
            }

        } catch (HibernateException exception) {
            exception.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void mostrarUsuarioPerfil() {
        // Mostrar todos los usuarios junto con la informacion de sus perfiles.
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {



        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }
}
