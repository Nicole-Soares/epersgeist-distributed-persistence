package ar.edu.unq.epersgeist.persistence.sql.entity;

import ar.edu.unq.epersgeist.persistence.sql.entity.espiritu.EspirituSQL;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@Entity(name = "Medium")
public class MediumSQL {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nombre;

    @Column(nullable = false, columnDefinition = "INTEGER CHECK (mana_max >= 0)")
    private Integer manaMax;

    @Column(nullable = false, columnDefinition = "INTEGER CHECK (mana <= mana_max AND mana >= 0)")
    private Integer mana;

    @OneToMany(mappedBy = "medium", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Where(clause = "deleted_at = false")
    private List<EspirituSQL> espiritus;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ubicacion_id", nullable = false)
    private UbicacionSQL ubicacion;

    @Column(name = "cordura")
    private Double cordura;

    public MediumSQL(String nombre, Integer manaMax, Integer mana, UbicacionSQL ubicacion, Double cordura) {
        this.nombre = nombre;
        this.manaMax = manaMax;
        this.mana = mana;
        this.ubicacion = ubicacion;
        this.espiritus = new ArrayList<>();
        this.cordura = cordura;
    }
}