
package eus.birt.dam.ad04.config;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import eus.birt.dam.ad04.domain.Monitor;
import eus.birt.dam.ad04.domain.Actividad;
import eus.birt.dam.ad04.domain.Cliente;
import eus.birt.dam.ad04.domain.Reserva;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {

        if (sessionFactory == null) {

            String password = System.getenv("ACTIVA_DB_PASSWORD");

            if (password == null || password.isBlank()) {
                throw new IllegalStateException(
                        "Falta la variable ACTIVA_DB_PASSWORD");
            }

            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml")
                    .applySettings(Map.of(
                            "hibernate.connection.password", password))
                    .build();

            try {
                Metadata metadata = new MetadataSources(registry)
                        .addAnnotatedClass(Monitor.class)
                        .addAnnotatedClass(Actividad.class)
                        .addAnnotatedClass(Cliente.class)
                        .addAnnotatedClass(Reserva.class)
                        .buildMetadata();

                sessionFactory = metadata
                        .buildSessionFactory();

            } catch (Exception e) {
                StandardServiceRegistryBuilder.destroy(registry);
                throw e;
            }
        }

        return sessionFactory;
    }

    public static void cerrar() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}
