package ar.edu.unq.epersgeist.persistence.mapper.interfaces;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistence.mongo.entity.CoordenadaMongo;
import ar.edu.unq.epersgeist.persistence.mongo.entity.UbicacionMongo;
import ar.edu.unq.epersgeist.persistence.sql.entity.MediumSQL;

public interface MediumMapper extends EntityMapper<Medium, MediumSQL>{
    Medium aModeloSinEspiritus(MediumSQL mediumSQL, CoordenadaMongo coordenadaMongo, UbicacionMongo ubicacionMongo);
    MediumSQL aEntidadSinEspiritus(Medium medium);
    Medium aModelo(MediumSQL mediumSQL, CoordenadaMongo coordenadaMongo, UbicacionMongo ubicacionMongo);
}
