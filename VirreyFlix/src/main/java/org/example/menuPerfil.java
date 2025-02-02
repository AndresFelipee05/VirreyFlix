package org.example;

import org.example.model.Perfil;
import org.example.model.Usuario;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.example.menuUsuario.introduceEntero;
import static org.example.menuUsuario.mostrarUsuarios;

public class menuPerfil {

    public final static Scanner sc = new Scanner(System.in);

    public static void menuPerfil() {
        int opcion;
        do {
            System.out.println("\n --- Menú de Perfil ---");
            System.out.println("1. Mostrar datos de Perfil");
            System.out.println("2. Insertar Perfil");
            System.out.println("3. Modificar datos de Perfil");
            System.out.println("4. Eliminar Perfil");
            System.out.println("5. Mostrar Perfiles");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elige una opción: ");
            opcion = introduceEntero();

            switch (opcion) {
                case 1 -> {
                    System.out.println("Mostrando datos de usuario...");
                    buscarPerfilPorID();
                }

                case 2 -> {
                    insertarPerfil();
                }

                case 3 -> {
                    modificarPerfil();
                }

                case 4 -> {
                    eliminarPerfil();
                }

                case 5 -> {
                    System.out.println("Mostrando los perfiles registrados...");
                    System.out.println(mostrarPerfiles());
                }

                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción no válida. Por favor, elige una opción del menú.");
            }
        } while (opcion != 0);
    }

    public static void insertarPerfil() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            // Solicitar el nombre del perfil
            System.out.println("Introduce el nombre del perfil:");
            String nombre = sc.nextLine().trim();

            if (nombre.isEmpty()) {
                System.out.println("El nombre no puede ser vacío. Operación cancelada.");
                return;
            }

            // Solicitar la edad del perfil
            System.out.println("Introduce la edad del perfil:");
            int edad = introduceEntero();
            if (edad < 0) {
                System.out.println("La edad no cumple el formato. Operación cancelada.");
                return;
            }

            // Mostrar los usuarios disponibles.
            System.out.println("Listado de usuarios registrados:");
            List<Usuario> usuarios = mostrarUsuarios();

            if (usuarios.isEmpty()) {
                System.out.println("No hay usuarios registrados. ¿Quieres crear el perfil sin usuario asociado? (True|False)");
                String confirmarString = sc.nextLine().trim();
                boolean confirmar;

                if (confirmarString.equalsIgnoreCase("true") || confirmarString.equalsIgnoreCase("false")) {
                    confirmar = Boolean.parseBoolean(confirmarString);
                } else {
                    System.out.println("No has introducido True o False. Operación cancelada.");
                    return;
                }

                if (confirmar) {
                    // Creamos el perfil sin usuario.
                    System.out.println("¿Quieres crear el perfil con nombre " + nombre + " y edad " + edad + "? (True|False)");
                    String confirmarCreacionString = sc.nextLine().trim();
                    boolean confirmarCreacion;

                    if (confirmarCreacionString.equalsIgnoreCase("true") || confirmarCreacionString.equalsIgnoreCase("false")) {
                        confirmarCreacion = Boolean.parseBoolean(confirmarCreacionString);
                    } else {
                        System.out.println("No has introducido True o False. Operación cancelada.");
                        return;
                    }

                    if (!confirmarCreacion) {
                        System.out.println("No se creará el perfil");
                        return;
                    } else {
                        Perfil perfilNuevo = new Perfil(nombre, edad);
                        session.persist(perfilNuevo);
                        tx.commit();

                        System.out.println("El perfil ha sido creado: " + perfilNuevo);
                        return;
                    }
                } else {
                    System.out.println("No se creará el episodio. Operación cancelada");
                    return;
                }
            }
            // Cuando si hay usuarios registrados
            for (Usuario u : usuarios) {
                System.out.println(u);
            }

            System.out.print("Selecciona el ID de un usuario (o introduce -1 para no asociar usuario):");
            long idUsuarioBuscar = introduceEntero();

            Usuario usuarioBuscar = null;
            if (idUsuarioBuscar != -1) {
                if (idUsuarioBuscar < 0) {
                    System.out.println("El ID del usuario no cumple con el formato. Operación cancelada.");
                    return;
                }

                usuarioBuscar = session.find(Usuario.class, idUsuarioBuscar);

                if (usuarioBuscar == null) {
                    System.out.println("No se pudo encontrar un usuario con el ID: " + idUsuarioBuscar);
                    return;
                }
            }

            System.out.println("¿Quieres crear el perfil con nombre " + nombre + ", edad " + edad +
                    (usuarioBuscar != null ? " y usuario asociado: " + usuarioBuscar : " sin usuario asociado") + "? (True|False)");
            String confirmarString = sc.nextLine().trim();
            boolean confirmar;

            if (confirmarString.equalsIgnoreCase("true") || confirmarString.equalsIgnoreCase("false")) {
                confirmar = Boolean.parseBoolean(confirmarString);
            } else {
                System.out.println("No has introducido True o False. Operación cancelada.");
                return;
            }

            if (!confirmar) {
                System.out.println("No se creará el usuario.");
                return;
            } else {
                Perfil perfilNuevo = new Perfil(nombre, edad);
                if (usuarioBuscar != null) {
                    perfilNuevo.setUsuario(usuarioBuscar);
                }
                session.persist(perfilNuevo);
                tx.commit();

                System.out.println("Perfil creado: " + perfilNuevo);
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }


    public static void buscarPerfilPorID() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            System.out.print("Introduce el id del perfil a buscar: ");
            long id;

            try {
                id = Long.parseLong(sc.nextLine());
            } catch (NumberFormatException ex) {
                System.out.println("El ID no cumple con el formato. Operación cancelada.");
                return;
            }

            Perfil perfil = session.find(Perfil.class, id);

            if (perfil == null) {
                System.out.println("No se pudo encontrar al perfil con el id " + id);
                return;
            } else {
                System.out.println("Perfil encontrado: " + perfil);
            }

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void modificarPerfil() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            System.out.println("Listado de perfiles registrados:");
            List<Perfil> perfiles = mostrarPerfiles();

            if (perfiles.isEmpty()) {
                System.out.println("Lo sentimos. No hay perfiles registrados. Operación cancelada.");
                return;
            }

            for (Perfil p : perfiles) {
                System.out.println(p);
            }
            System.out.print("Introduce el id del perfil a modificar: ");
            long id = introduceEntero();

            if (id < 0) {
                System.out.println("El ID no cumple el formato. Operación cancelada.");
                return;
            }

            Perfil perfilBuscar = session.find(Perfil.class, id);
            if (perfilBuscar == null) {
                System.out.println("No se pudo encontrar al perfil con el id " + id);
                return;
            } else {
                System.out.println("Perfil encontrado. Sus datos: " + perfilBuscar);

                // Modificar el nombre
                System.out.println("Introduce el nuevo nombre del perfil (o deja en blanco para mantener el actual):");
                String nombreNuevo = sc.nextLine();
                if (!nombreNuevo.isEmpty()) {
                    perfilBuscar.setNombre(nombreNuevo);
                }

                // Modificar la edad
                System.out.println("Introduce la nueva edad del perfil (o -1 para mantener la actual):");
                int edadNueva = introduceEntero();

                if (edadNueva != -1 && edadNueva < 0) {
                    System.out.println("La edad no cumple el formato. Operación cancelada.");
                    return;
                }

                if (edadNueva >= 0) {
                    perfilBuscar.setEdad(edadNueva);
                }

                System.out.println("¿Quieres modificar el usuario asociado? (True|False)");
                String usuarioAsociadoString = sc.nextLine().trim();
                boolean modificarUsuarioAsociado;

                if (usuarioAsociadoString.equalsIgnoreCase("true") || usuarioAsociadoString.equalsIgnoreCase("false")) {
                    modificarUsuarioAsociado = Boolean.parseBoolean(usuarioAsociadoString);
                } else {
                    System.out.println("No has introducido True o False. Operación cancelada.");
                    return;
                }

                if (!modificarUsuarioAsociado) {
                    System.out.println("¿Quieres que el perfil tenga de nombre: " + perfilBuscar.getNombre() + " y de edad: " + perfilBuscar.getEdad() + "? (True|False)");
                    String confirmarString = sc.nextLine().trim();
                    boolean confirmar;

                    if (confirmarString.equalsIgnoreCase("true") || confirmarString.equalsIgnoreCase("false")) {
                        confirmar = Boolean.parseBoolean(confirmarString);
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
                        System.out.println("Los nuevos datos del perfil: " + perfilBuscar);
                    }
                } else {
                    System.out.println("Listado de usuarios registrados:");
                    List<Usuario> usuarios = mostrarUsuarios();

                    if (usuarios.isEmpty()) {
                        System.out.println("Operación cancelada.");
                        return;
                    }

                    for (Usuario u : usuarios) {
                        System.out.println(u);
                    }

                    System.out.print("Selecciona el ID de un usuario: ");
                    long idBuscar = introduceEntero();

                    if (idBuscar < 0) {
                        System.out.println("El ID del usuario no cummple el formato. Operación cancelada.");
                        return;
                    }

                    Usuario usuarioBuscar = session.find(Usuario.class, idBuscar);

                    if (usuarioBuscar == null) {
                        System.out.println("No se ha podido encontrar un usuario con el ID: " + idBuscar);
                        return;
                    } else {
                        System.out.println("¿Quieres que el perfil tenga de nombre: " + perfilBuscar.getNombre() + " y de edad: " + perfilBuscar.getEdad() + " y usuario asociado: " + usuarioBuscar + "? (True|False)");
                        String confirmarString = sc.nextLine().trim();
                        boolean confirmar;

                        if (confirmarString.equalsIgnoreCase("true") || confirmarString.equalsIgnoreCase("false")) {
                            confirmar = Boolean.parseBoolean(confirmarString);
                        } else {
                            System.out.println("No has introducido True o False. Operación cancelada.");
                            return;
                        }

                        if (!confirmar) {
                            System.out.println("No se realizarán cambios.");
                            return;
                        } else {
                            System.out.println("Los datos han sido modificados.");
                            perfilBuscar.setUsuario(usuarioBuscar);
                            tx.commit();
                            System.out.println("Los nuevos datos del perfil: " + perfilBuscar);
                        }
                    }
                }
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void eliminarPerfil() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            System.out.println("Listado de perfiles registrados:");
            List<Perfil> perfiles = mostrarPerfiles();
            if (perfiles.isEmpty()) {
                System.out.println("Lo sentimos, no hay perfiles registrados. Operación cancelada");
                return;
            }
            for (Perfil p : perfiles) {
                System.out.println(p);
            }
            System.out.print("Introduce el id del perfil a eliminar: ");
            long id;
            try {
                id = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException ex) {
                System.out.println("El ID no cumple con el formato. Operación cancelada.");
                return;
            }

            Perfil perfil = session.find(Perfil.class, id);
            if (perfil == null) {
                System.out.println("No se pudo encontrar al perfil con el id " + id);
            } else {
                System.out.println("Perfil encontrado");
                System.out.println("¿Quieres eliminar al perfil con nombre " + perfil.getNombre() + " y edad " + perfil.getEdad() + "? (True|False)");
                String confirmarString = sc.nextLine().trim();
                boolean confirmar;

                if (confirmarString.equalsIgnoreCase("true") || confirmarString.equalsIgnoreCase("false")) {
                    confirmar = Boolean.parseBoolean(confirmarString);
                } else {
                    System.out.println("No has introducido True o False. Operación cancelada.");
                    return;
                }

                if (!confirmar) {
                    System.out.println("No se eliminará el perfil");
                    return;
                } else {
                    session.delete(perfil);
                    System.out.println("El perfil ha sido eliminado.");
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

    public static ArrayList<Perfil> mostrarPerfiles() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        ArrayList<Perfil> listaPerfiles = new ArrayList<>();

        try {
            tx = session.beginTransaction();

            // Consulta SQL para obtener todos los registros de la tabla Perfil
            List<Perfil> perfiles = session.createQuery("FROM Perfil", Perfil.class).list();

            if (perfiles.isEmpty()) {
                System.out.println("La lista está vacía. No hay perfiles registrados.");
                return listaPerfiles;
            } else {
                // Añadir perfiles a la lista
                listaPerfiles.addAll(perfiles);
            }
            tx.commit();
        } catch (HibernateException ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }

        return listaPerfiles;
    }
}
