package ar.edu.unq.epersgeist.persistence.sql.entity.espiritu;

import ar.edu.unq.epersgeist.persistence.sql.entity.MediumSQL;
import ar.edu.unq.epersgeist.persistence.sql.entity.UbicacionSQL;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Setter
@Getter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@SQLDelete(sql = "UPDATE espiritu SET deleted_at = true, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at = false")
@EntityListeners(AuditingEntityListener.class)
@Entity(name = "Espiritu")
public abstract class EspirituSQL{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Setter
    private Integer nivelDeConexion;
    private String nombre;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ubicacion_id", nullable = false)
    private UbicacionSQL ubicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    private MediumSQL medium;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dominante_id")
    private EspirituSQL espirituDominante;

    @Column(name = "hostilidad")
    private Double hostilidad;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.util.Date created_at;

    @LastModifiedDate
    @Column(name = "updated_at")
    private java.util.Date updated_at = null;

    @Column(name = "deleted_at", nullable = false)
    private boolean deletedAt = false;

    public EspirituSQL(String nombre, Integer nivelDeConexion, UbicacionSQL ubicacion, Double hostilidad) {
        this.nivelDeConexion = nivelDeConexion;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.hostilidad = hostilidad;
    }
