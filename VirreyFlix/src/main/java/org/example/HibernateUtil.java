package org.example;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    // El registro de servicios estándar de Hibernate, que gestiona la configuración
    // y los servicios necesarios para la conexión.
    private static StandardServiceRegistry registry;

    // La fábrica de sesiones, que es responsable de crear y gestionar las sesiones
    // de Hibernate.
    private static SessionFactory sessionFactory;

    // Metodo que devuelve la instancia de SessionFactory, creándola si aún no existe.
    public static SessionFactory getSessionFactory() {

        if (sessionFactory == null) { // Verifica si sessionFactory ya está inicializado.
            try {
                // Construye el registro de servicios estándar a partir del archivo de
                // configuración (hibernate.cfg.xml).
                registry = new StandardServiceRegistryBuilder().configure().build();

                // Fuentes de metadatos, que recopilan información de las entidades
                // mapeadas y otras configuraciones.
                MetadataSources sources = new MetadataSources(registry);

                // Construye los metadatos necesarios para configurar y crear la fábrica
                // de sesiones.
                Metadata metadata = sources.getMetadataBuilder().build();

                // Construye y asigna la fábrica de sesiones utilizando los metadatos
                // configurados.
                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error al crear Session Factory " + e.getMessage());

                // Si ocurre un error, destruye el registro de servicios para liberar recursos.
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }
        return sessionFactory; // Devuelve la instancia de SessionFactory.
    }

    // Metodo para cerrar el registro de servicios y liberar los recursos asociados.
    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}