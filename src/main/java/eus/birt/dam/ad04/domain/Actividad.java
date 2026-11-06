
package eus.birt.dam.ad04.domain;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "actividad")
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "localidad", nullable = false, length = 100)
    private String localidad;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "plazas_totales", nullable = false)
    private int plazasTotales;

    @Column(name = "plazas_disponibles", nullable = false)
    private int plazasDisponibles;

    @ManyToOne(optional = false)
    @JoinColumn(name = "monitor_id", nullable = false)
    private Monitor monitor;

    public Actividad() {
    }

    public Actividad(String nombre, String localidad,
            LocalDateTime fechaHora, int plazasTotales,
            Monitor monitor) {
        this.nombre = nombre;
        this.localidad = localidad;
        this.fechaHora = fechaHora;
        this.plazasTotales = plazasTotales;
        this.plazasDisponibles = plazasTotales;
        setMonitor(monitor);
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getLocalidad() {
        return localidad;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public int getPlazasTotales() {
        return plazasTotales;
    }

    public int getPlazasDisponibles() {
        return plazasDisponibles;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public void setPlazasDisponibles(int plazasDisponibles) {
        this.plazasDisponibles = plazasDisponibles;
    }

    public void setMonitor(Monitor nuevoMonitor) {
        if (this.monitor == nuevoMonitor) {
            return;
        }

        if (this.monitor != null) {
            this.monitor.getActividades().remove(this);
        }

        this.monitor = nuevoMonitor;

        if (nuevoMonitor != null
                && !nuevoMonitor.getActividades().contains(this)) {
            nuevoMonitor.getActividades().add(this);
        }
    }
}
