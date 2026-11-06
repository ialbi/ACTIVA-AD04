
package eus.birt.dam.ad04.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import eus.birt.dam.ad04.domain.Actividad;
import eus.birt.dam.ad04.domain.Monitor;

public class ActividadService {

    private final SessionFactory sessionFactory;

    public ActividadService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Crear una actividad
    public Long crearActividad(
            String nombre,
            String localidad,
            LocalDateTime fechaHora,
            int plazasTotales,
            Long monitorId) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio");
        }

        if (localidad == null || localidad.isBlank()) {
            throw new IllegalArgumentException(
                    "La localidad es obligatoria");
        }

        if (fechaHora == null) {
            throw new IllegalArgumentException(
                    "La fecha y hora son obligatorias");
        }

        if (plazasTotales <= 0) {
            throw new IllegalArgumentException(
                    "Las plazas deben ser positivas");
        }

        if (monitorId == null) {
            throw new IllegalArgumentException(
                    "Debes indicar un monitor");
        }

        try (Session session = sessionFactory.openSession()) {

            Transaction tx = session.beginTransaction();

            try {
                Monitor monitor = session.get(
                        Monitor.class, monitorId);

                if (monitor == null) {
                    throw new IllegalArgumentException(
                            "No existe un monitor con ID " + monitorId);
                }

                Actividad actividad = new Actividad(
                        nombre.trim(),
                        localidad.trim(),
                        fechaHora,
                        plazasTotales,
                        monitor);

                session.persist(actividad);

                tx.commit();

                return actividad.getId();

            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    // Buscar actividades disponibles
    public List<Actividad> buscarDisponibles(
            String localidad,
            LocalDate fecha,
            int plazasMinimas) {

        if (localidad == null || localidad.isBlank()) {
            throw new IllegalArgumentException(
                    "La localidad es obligatoria");
        }

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria");
        }

        if (plazasMinimas <= 0) {
            throw new IllegalArgumentException(
                    "Las plazas mínimas deben ser positivas");
        }

        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();

        try (Session session = sessionFactory.openSession()) {

            return session.createSelectionQuery(
                    """
                            SELECT a
                            FROM Actividad a
                            JOIN FETCH a.monitor
                            WHERE LOWER(a.localidad) = :localidad
                              AND a.fechaHora >= :inicio
                              AND a.fechaHora < :fin
                              AND a.plazasDisponibles >= :plazas
                            ORDER BY a.fechaHora
                            """,
                    Actividad.class)
                    .setParameter(
                            "localidad",
                            localidad.trim().toLowerCase(Locale.ROOT))
                    .setParameter("inicio", inicio)
                    .setParameter("fin", fin)
                    .setParameter("plazas", plazasMinimas)
                    .getResultList();
        }
    }

    // Listar todas las actividades, incluso las completas
    public List<Actividad> listarTodas() {

        try (Session session = sessionFactory.openSession()) {

            return session.createSelectionQuery(
                    """
                            SELECT a
                            FROM Actividad a
                            JOIN FETCH a.monitor
                            ORDER BY a.fechaHora, a.id
                            """,
                    Actividad.class)
                    .getResultList();
        }
    }
}
