package ar.edu.unq.epersgeist.persistence.mapper.interfaces;

import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.persistence.mongo.entity.CoordenadaMongo;
import ar.edu.unq.epersgeist.persistence.mongo.entity.UbicacionMongo;
import ar.edu.unq.epersgeist.persistence.sql.entity.espiritu.EspirituSQL;

public interface EspirituMapper extends EntityMapper<Espiritu, EspirituSQL> {
    Espiritu aModelo(EspirituSQL espirituSQL, CoordenadaMongo coordenadaMongo, UbicacionMongo ubicacionMongo);
}
