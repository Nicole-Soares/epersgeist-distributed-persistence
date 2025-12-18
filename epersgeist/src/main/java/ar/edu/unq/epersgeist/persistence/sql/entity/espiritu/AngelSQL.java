package ar.edu.unq.epersgeist.persistence.sql.entity.espiritu;

import ar.edu.unq.epersgeist.persistence.sql.entity.UbicacionSQL;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity(name = "Angel")
public class AngelSQL extends EspirituSQL {

    public AngelSQL(String nombre,Integer nivelDeConexion, UbicacionSQL ubicacionSQL, Double hostilidad) {
        super(nombre, nivelDeConexion, ubicacionSQL, hostilidad);
    }
}
