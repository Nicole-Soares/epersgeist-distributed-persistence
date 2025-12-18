package ar.edu.unq.servicio_mensajeria.sql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// SOLO LECTURA
@Entity
@Table(name = "espiritu")
@Getter
@Setter
public class EspirituCandidato {

    public final static int HOSTILIDAD_MIN = 0;
    public final static int HOSTILIDAD_MAX = 100; // MIN < MAX SIEMPRE

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "hostilidad")
    private double hostilidad;

    @Column(name = "ubicacion_id")
    private Long ubicacionId;

    @Column(name = "deleted_at", nullable = false)
    private boolean deletedAt = false;

    public EspirituCandidato() {}

    public EspirituCandidato(String nombre, Double hostilidad, Long ubicacionId) {
        this.nombre = nombre;
        this.hostilidad = hostilidad;
        this.ubicacionId = ubicacionId;
    }

    public double getHostilidadNormalizada() {
        return ((Math.max(HOSTILIDAD_MIN, Math.min(HOSTILIDAD_MAX, hostilidad))) - HOSTILIDAD_MIN) / (HOSTILIDAD_MAX - HOSTILIDAD_MIN);
    }
}
