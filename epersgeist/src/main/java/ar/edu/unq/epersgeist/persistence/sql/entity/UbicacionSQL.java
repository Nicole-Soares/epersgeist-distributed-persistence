package ar.edu.unq.epersgeist.persistence.sql.entity;

import ar.edu.unq.epersgeist.modelo.ubicacion.TipoUbicacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import static jakarta.persistence.GenerationType.*;

@Setter
@Getter
@NoArgsConstructor
@Entity(name = "Ubicacion")
public class UbicacionSQL {
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @Setter
    @Column(nullable = false, length = 120, unique = true)
    private String nombre;

    @Column(nullable = false, columnDefinition = "INTEGER CHECK (flujo_energia >= 0 AND flujo_energia <= 100)")
    private Integer flujoEnergia;

    @Enumerated(EnumType.STRING)
    private TipoUbicacion tipo;

    @Column(name = "temperatura")
    private Integer temperatura;

    public UbicacionSQL(String nombre, Integer flujoEnergia, TipoUbicacion tipo, Integer temperatura) {
        this.nombre = nombre;
        this.flujoEnergia = flujoEnergia;
        this.tipo = tipo;
        this.temperatura = temperatura;
    }
}