
package eus.birt.dam.ad04.service;

import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import eus.birt.dam.ad04.domain.Cliente;

public class ClienteService {

    private final SessionFactory sessionFactory;

    public ClienteService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Long crearCliente(String nombre, String email) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);

        if (!emailNormalizado.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Formato de email no válido");
        }

        
Transaction tx = null;

try (Session session = sessionFactory.openSession()) {

    try {
        tx = session.beginTransaction();

        Long existentes = session.createSelectionQuery(
            """
            SELECT COUNT(c)
            FROM Cliente c
            WHERE LOWER(c.email) = :email
            """,
            Long.class
        )
        .setParameter("email", emailNormalizado)
        .getSingleResult();

        if (existentes > 0) {
            throw new IllegalArgumentException(
                "Ya existe un cliente con ese email"
            );
        }

        Cliente cliente = new Cliente(
            nombre.trim(),
            emailNormalizado
        );

        session.persist(cliente);
        tx.commit();

        return cliente.getId();

    } catch (RuntimeException e) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
        throw e;
    }
}

}
}

