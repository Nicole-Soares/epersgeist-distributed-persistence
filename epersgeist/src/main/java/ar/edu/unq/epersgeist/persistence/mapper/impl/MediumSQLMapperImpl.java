package ar.edu.unq.epersgeist.persistence.mapper.impl;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistence.mapper.interfaces.CoordenadasMapper;
import ar.edu.unq.epersgeist.persistence.mapper.interfaces.EspirituMapper;
import ar.edu.unq.epersgeist.persistence.mapper.interfaces.MediumMapper;
import ar.edu.unq.epersgeist.persistence.mapper.interfaces.UbicacionMapper;
import ar.edu.unq.epersgeist.persistence.mongo.entity.CoordenadaMongo;
import ar.edu.unq.epersgeist.persistence.mongo.entity.UbicacionMongo;
import ar.edu.unq.epersgeist.persistence.sql.entity.MediumSQL;
import ar.edu.unq.epersgeist.persistence.sql.entity.UbicacionSQL;
import ar.edu.unq.epersgeist.persistence.sql.entity.espiritu.EspirituSQL;
import org.springframework.stereotype.Component;


@Component
public class MediumSQLMapperImpl implements MediumMapper {

    private final UbicacionMapper ubicacionMapper;
    private final EspirituMapper espirituMapper;
    private final CoordenadasMapper coordenadasMapper;

    public MediumSQLMapperImpl(UbicacionMapper ubicacionMapper, EspirituMapper espirituMapper, CoordenadasMapper coordenadasMapper) {
        this.ubicacionMapper = ubicacionMapper;
        this.espirituMapper = espirituMapper;
        this.coordenadasMapper = coordenadasMapper;
    }

    @Override
    public MediumSQL aEntidad(Medium medium) {
        UbicacionSQL ubicacionSQL = ubicacionMapper.aEntidad(medium.getUbicacion());
        MediumSQL mediumSQL = new MediumSQL(medium.getNombre(), medium.getManaMax(), medium.getMana(), ubicacionSQL, medium.getCordura());
        mediumSQL.setId(medium.getId());
        if ( !medium.getEspiritus().isEmpty()) {
            medium.getEspiritus().forEach(e -> {
                EspirituSQL espirituSQL = espirituMapper.aEntidad(e);
                mediumSQL.getEspiritus().add(espirituSQL);
            });
        }
        return mediumSQL;
    }

    @Override
    public MediumSQL aEntidadSinEspiritus(Medium medium) {
        UbicacionSQL ubicacionSQL = ubicacionMapper.aEntidad(medium.getUbicacion());
        MediumSQL mediumSQL = new MediumSQL(medium.getNombre(), medium.getManaMax(), medium.getMana(), ubicacionSQL, medium.getCordura());
        mediumSQL.setId(medium.getId());
        return mediumSQL;
    }

    @Override
    public Medium aModelo(MediumSQL mediumSQL, CoordenadaMongo coordenadaMongo, UbicacionMongo ubicacionMongo) {
        Coordenadas coordenadas = coordenadasMapper.aModelo(coordenadaMongo);

        Ubicacion ubicacion = ubicacionMapper.aModelo(mediumSQL.getUbicacion(), ubicacionMongo);
        Medium mediumModelo = new Medium(mediumSQL.getNombre(), mediumSQL.getManaMax(), mediumSQL.getMana(), ubicacion);
        mediumModelo.setId(mediumSQL.getId());
        mediumModelo.setCoordenadas(coordenadas);
        mediumModelo.setCordura(mediumSQL.getCordura());
        if ( !mediumSQL.getEspiritus().isEmpty()) {
            mediumSQL.getEspiritus().forEach(e -> {
                Espiritu espiritu = espirituMapper.aModelo(e, coordenadaMongo, ubicacionMongo);
                mediumModelo.getEspiritus().add(espiritu);
            });
        }
        return mediumModelo;
    }

    @Override
    public Medium aModelo(MediumSQL mediumSQL) {
        throw new UnsupportedOperationException(
                "Usar aModelo(MediumSQL, UbicacionMongo) — esta versión no tiene acceso a la UbicacionMongo necesaria"
        );
    }

    @Override
    public Medium aModeloSinEspiritus(MediumSQL mediumSQL, CoordenadaMongo coordenadaMongo, UbicacionMongo ubicacionMongo) {
        Coordenadas coordenadas = coordenadasMapper.aModelo(coordenadaMongo);

        Ubicacion ubicacion = ubicacionMapper.aModelo(mediumSQL.getUbicacion(), ubicacionMongo);
        Medium mediumModelo = new Medium(mediumSQL.getNombre(), mediumSQL.getManaMax(), mediumSQL.getMana(), ubicacion);
        mediumModelo.setId(mediumSQL.getId());
        mediumModelo.setCoordenadas(coordenadas);
        mediumModelo.setCordura(mediumSQL.getCordura());
        return mediumModelo;
    }
}
