
package eus.birt.dam.ad04.main;

import java.util.Scanner;

import org.hibernate.SessionFactory;

import eus.birt.dam.ad04.config.HibernateUtil;
import eus.birt.dam.ad04.service.MonitorService;
import eus.birt.dam.ad04.service.ActividadService;
import eus.birt.dam.ad04.service.ClienteService;
import eus.birt.dam.ad04.service.ReservaService;

import eus.birt.dam.ad04.domain.Actividad;
import eus.birt.dam.ad04.domain.Reserva;

public class Main {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

            MonitorService monitorService = new MonitorService(sessionFactory);

            ActividadService actividadService = new ActividadService(sessionFactory);

            ClienteService clienteService = new ClienteService(sessionFactory);

            ReservaService reservaService = new ReservaService(sessionFactory);

            int opcion = 0;

            while (opcion != 8) {

                System.out.println("\n=== ACTIVA · Gestión de actividades ===");
                System.out.println("1. Crear monitor");
                System.out.println("2. Crear actividad");
                System.out.println("3. Buscar actividades disponibles");
                System.out.println("4. Crear cliente");
                System.out.println("5. Crear reserva");
                System.out.println("6. Cancelar reserva");
                System.out.println("7. Listar actividades");
                System.out.println("8. Salir");

                System.out.print("Elige una opción: ");

                try {
                    opcion = Integer.parseInt(scanner.nextLine());

                    switch (opcion) {
                        case 1 -> {
                            System.out.print("Nombre del monitor: ");
                            String nombre = scanner.nextLine();

                            System.out.print("Especialidad: ");
                            String especialidad = scanner.nextLine();

                            try {
                                Long id = monitorService.crearMonitor(nombre, especialidad);
                                System.out.println("Monitor creado correctamente. ID: " + id);
                            } catch (RuntimeException e) {
                                System.out.println("No se pudo crear el monitor: " + e.getMessage());
                            }
                        }
                        case 2 -> {
                            try {
                                System.out.print("Nombre de la actividad: ");
                                String nombre = scanner.nextLine();

                                System.out.print("Localidad: ");
                                String localidad = scanner.nextLine();

                                System.out.print("Fecha y hora (AAAA-MM-DD HH:MM): ");
                                String fechaTexto = scanner.nextLine();

                                java.time.LocalDateTime fechaHora = java.time.LocalDateTime.parse(
                                        fechaTexto,
                                        java.time.format.DateTimeFormatter.ofPattern(
                                                "yyyy-MM-dd HH:mm"));

                                System.out.print("Plazas totales: ");
                                int plazas = Integer.parseInt(scanner.nextLine());

                                System.out.print("ID del monitor: ");
                                Long monitorId = Long.parseLong(scanner.nextLine());

                                Long id = actividadService.crearActividad(
                                        nombre, localidad, fechaHora, plazas, monitorId);

                                System.out.println("Actividad creada correctamente. ID: " + id);

                            } catch (RuntimeException e) {
                                System.out.println("No se pudo crear la actividad: " + e.getMessage());
                            }
                        }

                        case 3 -> {
                            try {
                                System.out.print("Localidad: ");
                                String localidad = scanner.nextLine();

                                System.out.print("Fecha (AAAA-MM-DD): ");
                                java.time.LocalDate fecha = java.time.LocalDate.parse(scanner.nextLine());

                                System.out.print("Plazas mínimas: ");
                                int plazas = Integer.parseInt(scanner.nextLine());

                                var actividades = actividadService.buscarDisponibles(
                                        localidad, fecha, plazas);

                                if (actividades.isEmpty()) {
                                    System.out.println("No se encontraron actividades disponibles");
                                } else {
                                    System.out.println("\n=== ACTIVIDADES DISPONIBLES ===");

                                    for (Actividad actividad : actividades) {
                                        System.out.println(
                                                "ID: " + actividad.getId()
                                                        + " | " + actividad.getNombre()
                                                        + " | Fecha: " + actividad.getFechaHora()
                                                        + " | Monitor: " + actividad.getMonitor().getNombre()
                                                        + " | Plazas disponibles: "
                                                        + actividad.getPlazasDisponibles());
                                    }
                                }

                            } catch (RuntimeException e) {
                                System.out.println(
                                        "Error al buscar actividades: " + e.getMessage());
                            }
                        }

                        case 4 -> {
                            try {
                                System.out.print("Nombre del cliente: ");
                                String nombre = scanner.nextLine();

                                System.out.print("Email: ");
                                String email = scanner.nextLine();

                                Long id = clienteService.crearCliente(nombre, email);

                                System.out.println(
                                        "Cliente creado correctamente. ID: " + id);

                            } catch (RuntimeException e) {
                                System.out.println(
                                        "No se pudo crear el cliente: " + e.getMessage());
                            }
                        }

                        case 5 -> {
                            try {
                                System.out.print("ID del cliente: ");
                                Long clienteId = Long.parseLong(scanner.nextLine());

                                System.out.print("ID de la actividad: ");
                                Long actividadId = Long.parseLong(scanner.nextLine());

                                System.out.print("Número de plazas a reservar: ");
                                int numeroPlazas = Integer.parseInt(scanner.nextLine());

                                Reserva reserva = reservaService.crearReserva(
                                        clienteId,
                                        actividadId,
                                        numeroPlazas);

                                System.out.println(
                                        "Reserva creada correctamente. ID: " + reserva.getId());

                                System.out.println(
                                        "Plazas restantes: "
                                                + reserva.getActividad().getPlazasDisponibles());

                            } catch (RuntimeException e) {
                                System.out.println(
                                        "No se pudo crear la reserva: " + e.getMessage());
                            }
                        }

                        case 6 -> {
                            try {
                                System.out.print("ID de la reserva a cancelar: ");
                                Long reservaId = Long.parseLong(scanner.nextLine());

                                int[] resultado = reservaService.cancelarReserva(reservaId);

                                System.out.println("Reserva cancelada correctamente.");
                                System.out.println("Plazas recuperadas: " + resultado[0]);
                                System.out.println("Plazas disponibles: " + resultado[1]);

                            } catch (RuntimeException e) {
                                System.out.println(
                                        "No se pudo cancelar la reserva: " + e.getMessage());
                            }
                        }

                        case 7 -> {
                            try {
                                var actividades = actividadService.listarTodas();

                                if (actividades.isEmpty()) {
                                    System.out.println("No hay actividades registradas");
                                } else {
                                    System.out.println("\n=== LISTADO DE ACTIVIDADES ===");

                                    for (Actividad actividad : actividades) {
                                        System.out.println(
                                                "ID: " + actividad.getId()
                                                        + " | " + actividad.getNombre()
                                                        + " | " + actividad.getLocalidad()
                                                        + " | Fecha: " + actividad.getFechaHora()
                                                        + " | Plazas: " + actividad.getPlazasDisponibles()
                                                        + "/" + actividad.getPlazasTotales()
                                                        + " | Monitor: " + actividad.getMonitor().getNombre());
                                    }
                                }

                            } catch (RuntimeException e) {
                                System.out.println(
                                        "Error al listar actividades: " + e.getMessage());
                            }
                        }

                        case 8 -> System.out.println("Hasta pronto");
                        default -> System.out.println("Opción no válida");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Introduce un número válido");
                }
            }

        } finally {
            HibernateUtil.cerrar();
        }
    }
}
