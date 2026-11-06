
package eus.birt.dam.ad04.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import eus.birt.dam.ad04.domain.Actividad;
import eus.birt.dam.ad04.domain.Cliente;
import eus.birt.dam.ad04.domain.Reserva;

public class ReservaService {

    private final SessionFactory sessionFactory;

    public ReservaService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Crear una reserva y descontar plazas
    public Reserva crearReserva(
            Long clienteId,
            Long actividadId,
            int numeroPlazas) {

        // Validaciones iniciales
        if (clienteId == null || actividadId == null) {
            throw new IllegalArgumentException(
                    "Debes indicar un cliente y una actividad");
        }

        if (numeroPlazas <= 0) {
            throw new IllegalArgumentException(
                    "El número de plazas debe ser positivo");
        }

        try (Session session = sessionFactory.openSession()) {

            Transaction tx = session.beginTransaction();

            try {
                // Buscar el cliente
                Cliente cliente = session.get(Cliente.class, clienteId);

                if (cliente == null) {
                    throw new IllegalArgumentException(
                            "No existe el cliente indicado");
                }

                // Buscar la actividad
                Actividad actividad = session.get(
                        Actividad.class, actividadId);

                if (actividad == null) {
                    throw new IllegalArgumentException(
                            "No existe la actividad indicada");
                }

                // Comprobar plazas disponibles
                if (actividad.getPlazasDisponibles() < numeroPlazas) {
                    throw new IllegalArgumentException(
                            "No hay suficientes plazas disponibles");
                }

                // Crear la reserva
                Reserva reserva = new Reserva(
                        cliente,
                        actividad,
                        numeroPlazas);

                // Descontar las plazas
                actividad.setPlazasDisponibles(
                        actividad.getPlazasDisponibles() - numeroPlazas);

                // Guardar la reserva
                session.persist(reserva);

                // Confirmar ambas operaciones
                tx.commit();

                // Devolver la reserva con los datos actualizados
                return reserva;

            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    // Cancelar una reserva y devolver las plazas
    public int[] cancelarReserva(Long reservaId) {

        if (reservaId == null) {
            throw new IllegalArgumentException(
                    "Debes indicar el ID de la reserva");
        }

        try (Session session = sessionFactory.openSession()) {

            Transaction tx = session.beginTransaction();

            try {
                // Buscar la reserva
                Reserva reserva = session.get(
                        Reserva.class, reservaId);

                if (reserva == null) {
                    throw new IllegalArgumentException(
                            "No existe una reserva con ID " + reservaId);
                }

                // Obtener la actividad asociada
                Actividad actividad = reserva.getActividad();

                // Devolver las plazas reservadas
                actividad.setPlazasDisponibles(
                        actividad.getPlazasDisponibles()
                                + reserva.getNumeroPlazas());

                // Eliminar únicamente la reserva
                session.remove(reserva);

                // Confirmar ambas operaciones
                tx.commit();

                return new int[] {
                        reserva.getNumeroPlazas(),
                        actividad.getPlazasDisponibles()
                };

            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }
}
