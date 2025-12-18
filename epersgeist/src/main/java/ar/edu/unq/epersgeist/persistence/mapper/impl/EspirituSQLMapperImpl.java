package ar.edu.unq.epersgeist.persistence.mapper.impl;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
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
import ar.edu.unq.epersgeist.persistence.sql.entity.espiritu.AngelSQL;
import ar.edu.unq.epersgeist.persistence.sql.entity.espiritu.DemonioSQL;
import ar.edu.unq.epersgeist.persistence.sql.entity.espiritu.EspirituSQL;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class EspirituSQLMapperImpl implements EspirituMapper {

    private final UbicacionMapper ubicacionMapper;
    private final MediumMapper mediumMapper;
    private final CoordenadasMapper coordenadasMapper;

    public EspirituSQLMapperImpl(UbicacionMapper ubicacionMapper, @Lazy MediumMapper mediumMapper, CoordenadasMapper coordenadasMapper) {
        this.ubicacionMapper = ubicacionMapper;
        this.mediumMapper = mediumMapper;
        this.coordenadasMapper = coordenadasMapper;
    }

    @Override
    public EspirituSQL aEntidad(Espiritu espiritu) {
        UbicacionSQL ubicacionSQL = ubicacionMapper.aEntidad(espiritu.getUbicacion());

        MediumSQL mediumSQL = (espiritu.getMedium() != null ? mediumMapper.aEntidadSinEspiritus(espiritu.getMedium()) : null);
        EspirituSQL espirituDominanteSQL = aEntidadReferencia(espiritu.getEspirituDominante());

        if (espiritu instanceof Angel angel) {
            AngelSQL angelSQL = new AngelSQL(angel.getNombre(), angel.getNivelDeConexion(), ubicacionSQL, angel.getHostilidad());
            angelSQL.setId(angel.getId());
            angelSQL.setMedium(mediumSQL);
            angelSQL.setEspirituDominante(espirituDominanteSQL);
            return angelSQL;
        }
        DemonioSQL demonioSQL = new DemonioSQL(espiritu.getNombre(), espiritu.getNivelDeConexion(), ubicacionSQL, espiritu.getHostilidad());
        demonioSQL.setId(espiritu.getId());
        demonioSQL.setMedium(mediumSQL);
        demonioSQL.setEspirituDominante(espirituDominanteSQL);
        return demonioSQL;
    }

    private EspirituSQL aEntidadReferencia(Espiritu espiritu) {
        if (espiritu == null || espiritu.getId() == null) return null;

        EspirituSQL ref;
        if (espiritu instanceof Angel) {
            ref = new AngelSQL();
        } else {
            ref = new DemonioSQL();
        }
        ref.setId(espiritu.getId());
        return ref;
    }

    @Override
    public Espiritu aModelo(EspirituSQL espirituSQL) {
        throw new UnsupportedOperationException(
                "Usar aModelo(EspirituSQL, UbicacionMongo) ya que esta version esta deprecada"
        );
    }

    @Override
    public Espiritu aModelo(EspirituSQL espirituSQL, CoordenadaMongo coordenadaMongo, UbicacionMongo ubicacionMongo) {

        Coordenadas coordenadas = coordenadasMapper.aModelo(coordenadaMongo);

        Ubicacion ubicacion = ubicacionMapper.aModelo(espirituSQL.getUbicacion(), ubicacionMongo);
        Medium mediumOwner = (espirituSQL.getMedium() != null ? mediumMapper.aModeloSinEspiritus(espirituSQL.getMedium(), coordenadaMongo, ubicacionMongo) : null);
        Espiritu espirituDominanteModelo = aModeloReferencia(espirituSQL.getEspirituDominante());

        if (espirituSQL instanceof AngelSQL angelSQL) {
            Angel angel = new Angel(angelSQL.getNombre(),ubicacion, angelSQL.getHostilidad());
            angel.setNivelDeConexion(angelSQL.getNivelDeConexion());
            angel.setId(angelSQL.getId());
            angel.setMedium(mediumOwner);
            angel.setEspirituDominante(espirituDominanteModelo);
            angel.setCoordenadas(coordenadas);
            return angel;
        }
        Demonio demonio = new Demonio(espirituSQL.getNombre(), ubicacion, espirituSQL.getHostilidad());
        demonio.setNivelDeConexion(espirituSQL.getNivelDeConexion());
        demonio.setId(espirituSQL.getId());
        demonio.setMedium(mediumOwner);
        demonio.setEspirituDominante(espirituDominanteModelo);
        demonio.setCoordenadas(coordenadas);
        return demonio;
    }

    private Espiritu aModeloReferencia(EspirituSQL espirituDominanteSQL) {
        if (espirituDominanteSQL == null || espirituDominanteSQL.getId() == null) return null;

        Espiritu ref;
        if (espirituDominanteSQL instanceof AngelSQL) {
            ref = new Angel();
        } else {
            ref = new Demonio();
        }
        ref.setId(espirituDominanteSQL.getId());
        return ref;
    }
}
