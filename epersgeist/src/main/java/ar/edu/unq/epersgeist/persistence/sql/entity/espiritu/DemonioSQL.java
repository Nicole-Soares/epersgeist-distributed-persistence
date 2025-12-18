package ar.edu.unq.epersgeist.persistence.sql.entity.espiritu;

import ar.edu.unq.epersgeist.persistence.sql.entity.UbicacionSQL;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity(name = "Demonio")
public class DemonioSQL extends EspirituSQL {

    public DemonioSQL(String nombre,Integer nivelDeConexion, UbicacionSQL ubicacionSQL, Double hostilidad) {
        super(nombre, nivelDeConexion, ubicacionSQL, hostilidad);
    }
}
