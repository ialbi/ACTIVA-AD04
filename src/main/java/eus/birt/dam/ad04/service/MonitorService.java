
package eus.birt.dam.ad04.service;

import eus.birt.dam.ad04.domain.Monitor;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class MonitorService {

    private final SessionFactory sessionFactory;

    public MonitorService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Long crearMonitor(String nombre, String especialidad) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio");
        }

        if (especialidad == null || especialidad.isBlank()) {
            throw new IllegalArgumentException(
                    "La especialidad es obligatoria");
        }

        Monitor monitor = new Monitor(
                nombre.trim(),
                especialidad.trim());

        try (Session session = sessionFactory.openSession()) {

            Transaction tx = session.beginTransaction();

            try {
                session.persist(monitor);

                tx.commit();

                return monitor.getId();

            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }
}
