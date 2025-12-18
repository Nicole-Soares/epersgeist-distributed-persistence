package ar.edu.unq.epersgeist.persistence.repository.impl;

import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.persistence.mapper.interfaces.CoordenadasMapper;
import ar.edu.unq.epersgeist.persistence.mapper.interfaces.EspirituMapper;
import ar.edu.unq.epersgeist.persistence.mongo.entity.CoordenadaMongo;
import ar.edu.unq.epersgeist.persistence.mongo.entity.UbicacionMongo;
import ar.edu.unq.epersgeist.persistence.mongo.interfaces.UbicacionMongoDAO;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.EspirituRepository;
import ar.edu.unq.epersgeist.persistence.sql.interfaces.EspirituSQLDAO;
import ar.edu.unq.epersgeist.persistence.sql.entity.espiritu.EspirituSQL;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EspirituRepositoryImpl implements EspirituRepository {
    private final EspirituSQLDAO espirituSQLDAO;
    private final EspirituMapper espirituMapper;
    private final UbicacionMongoDAO ubicacionMongoDAO;
    private final CoordenadasMapper coordenadasMapper;

    public EspirituRepositoryImpl(EspirituSQLDAO espirituSQLDAO, EspirituMapper espirituMapper, UbicacionMongoDAO ubicacionMongoDAO, CoordenadasMapper coordenadasMapper) {
        this.espirituSQLDAO = espirituSQLDAO;
        this.espirituMapper = espirituMapper;
        this.ubicacionMongoDAO = ubicacionMongoDAO;
        this.coordenadasMapper = coordenadasMapper;
    }

    @Override
    public Espiritu save(Espiritu espiritu) {
        EspirituSQL espirituSQL = espirituMapper.aEntidad(espiritu);
        espirituSQLDAO.save(espirituSQL);
        ubicacionMongoDAO.actualizarOcrearCoordenadaEntidad(espirituSQL.getUbicacion().getId().toString(), espirituSQL.getId(), espiritu.getRef(), espiritu.getCoordenadas());
        CoordenadaMongo coordenadaMongo = ubicacionMongoDAO.findCoordenadaDeEntidad(espirituSQL.getUbicacion().getId(), espirituSQL.getId());
        Coordenadas coordenadas = coordenadasMapper.aModelo(coordenadaMongo);
        espiritu.setId(espirituSQL.getId());
        espiritu.setCoordenadas(coordenadas);
        return espiritu;
    }

    @Override
    public List<? extends Espiritu> saveAll(List<? extends Espiritu> espiritus) {
        List<EspirituSQL> espirituSQLs = espiritus.stream()
                .map(espirituMapper::aEntidad)
                .toList();

        List<EspirituSQL> espirituSQLsGuardados = espirituSQLDAO.saveAll(espirituSQLs);

        for (int i = 0; i < espiritus.size(); i++) {
            Espiritu espirituDominio = espiritus.get(i);
            EspirituSQL espirituSQLGuardado = espirituSQLsGuardados.get(i);

            espirituDominio.setId(espirituSQLGuardado.getId());

            ubicacionMongoDAO.actualizarOcrearCoordenadaEntidad(
                    espirituSQLGuardado.getUbicacion().getId().toString(),
                    espirituSQLGuardado.getId(),
                    espirituDominio.getRef(),
                    espirituDominio.getCoordenadas()
            );

            CoordenadaMongo coordenadaMongo = ubicacionMongoDAO.findCoordenadaDeEntidad(
                    espirituSQLGuardado.getUbicacion().getId(),
                    espirituSQLGuardado.getId()
            );
            espirituDominio.setCoordenadas(coordenadasMapper.aModelo(coordenadaMongo));
        }
        return espiritus;
    }

    @Override
    public List<Espiritu> findByMediumId(Long mediumId) {
        return this.espirituSQLDAO.findByMediumId(mediumId)
                .stream()
                .map(espirituSQL -> {
                    Long ubicacionSQLID = espirituSQL.getUbicacion().getId();
                    UbicacionMongo ubicacionMongo = ubicacionMongoDAO.findById(ubicacionSQLID.toString()).orElseThrow(
                            () -> new EntityNotFoundException("No se encontro la ubicacion con id: " + ubicacionSQLID));
                    CoordenadaMongo coordenadasMongo = ubicacionMongoDAO.findCoordenadaDeEntidad(ubicacionSQLID, espirituSQL.getId());
                    espirituMapper.aModelo(espirituSQL, coordenadasMongo, ubicacionMongo);
                    return espirituMapper.aModelo(espirituSQL, coordenadasMongo, ubicacionMongo);
                })
                .toList();
    }

    @Override
    public Page<Demonio> findDemonios(Pageable pageable) {
        return this.espirituSQLDAO.findDemonios(pageable)
                .map(espirituSQL -> {
                    Long ubicacionSQLID = espirituSQL.getUbicacion().getId();
                    UbicacionMongo ubicacionMongo = ubicacionMongoDAO.findById(ubicacionSQLID.toString()).orElseThrow(
                            () -> new EntityNotFoundException("No se encontro la ubicacion con id: " + ubicacionSQLID));
                    CoordenadaMongo coordenadasMongo = ubicacionMongoDAO.findCoordenadaDeEntidad(ubicacionSQLID, espirituSQL.getId());
                    espirituMapper.aModelo(espirituSQL, coordenadasMongo, ubicacionMongo);
                    return (Demonio) espirituMapper.aModelo(espirituSQL, coordenadasMongo, ubicacionMongo);
                });
    }

    @Override
    public List<Angel> findAllAngelesByMediumId(Long idMediumExorcista) {
        return this.espirituSQLDAO.findAllAngelesByMediumId(idMediumExorcista)
                .stream()
                .map(espirituSQL -> {
                    Long ubicacionSQLID = espirituSQL.getUbicacion().getId();
                    UbicacionMongo ubicacionMongo = ubicacionMongoDAO.findById(ubicacionSQLID.toString()).orElseThrow(
                            () -> new EntityNotFoundException("No se encontro la ubicacion con id: " + ubicacionSQLID));
                    CoordenadaMongo coordenadasMongo = ubicacionMongoDAO.findCoordenadaDeEntidad(ubicacionSQLID, espirituSQL.getId());
                    espirituMapper.aModelo(espirituSQL, coordenadasMongo, ubicacionMongo);
                    return (Angel) espirituMapper.aModelo(espirituSQL, coordenadasMongo, ubicacionMongo);
                })
                .toList();
    }

    @Override
    public List<Espiritu> findByUbicacionId(Long ubicacionId) {
        return this.espirituSQLDAO.findByUbicacionId(ubicacionId)
                .stream()
                .map(espirituSQL -> {
                    Long ubicacionSQLID = espirituSQL.getUbicacion().getId();
                    UbicacionMongo ubicacionMongo = ubicacionMongoDAO.findById(ubicacionSQLID.toString()).orElseThrow(
                            () -> new EntityNotFoundException("No se encontro la ubicacion con id: " + ubicacionSQLID));
                    CoordenadaMongo coordenadasMongo = ubicacionMongoDAO.findCoordenadaDeEntidad(ubicacionSQLID, espirituSQL.getId());
                    return espirituMapper.aModelo(espirituSQL, coordenadasMongo, ubicacionMongo);
                })
                .toList();
    }

    @Override
    public Espiritu findById(Long espirituId) {
        EspirituSQL espirituSQL = this.espirituSQLDAO.findById(espirituId).orElseThrow(
                () -> new EntityNotFoundException("No se encontro el espiritu con id: " + espirituId));

        Long ubicacionSQLID = espirituSQL.getUbicacion().getId();
        String ubicacionMongoId = ubicacionSQLID.toString();

        UbicacionMongo ubicacionMongo = this.ubicacionMongoDAO.findById(ubicacionMongoId).orElseThrow(
                () -> new EntityNotFoundException("No se encontró la ubicación con ID: " + ubicacionSQLID));

        CoordenadaMongo coordenadasMongo = ubicacionMongoDAO.findCoordenadaDeEntidad(ubicacionSQLID, espirituSQL.getId());

        return espirituMapper.aModelo(espirituSQL, coordenadasMongo, ubicacionMongo);
    }

    @Override
    public List<Espiritu> findAll() {
        return this.espirituSQLDAO.findAll()
                .stream()
                .map(espirituSQL -> {
                    Long ubicacionSQLID = espirituSQL.getUbicacion().getId();
                    UbicacionMongo ubicacionMongo = ubicacionMongoDAO.findById(ubicacionSQLID.toString()).orElseThrow(
                            () -> new EntityNotFoundException("No se encontro la ubicacion con id: " + ubicacionSQLID));
                    CoordenadaMongo coordenadasMongo = ubicacionMongoDAO.findCoordenadaDeEntidad(ubicacionSQLID, espirituSQL.getId());
                    return espirituMapper.aModelo(espirituSQL, coordenadasMongo, ubicacionMongo);
                })
                .toList();
    }

    /**
     * Elimina un espíritu de la base de datos SQL por su ID.
     * @param espirituId El ID del espíritu a eliminar.
     */
    @Override
    public void deleteById(Long espirituId) {
        Espiritu espiritu = findById(espirituId);
        Long ubicacionId = espiritu.getUbicacion().getId();
        String tipoEntidad = espiritu.getClass().getSimpleName();
        ubicacionMongoDAO.eliminarCoordenadaEntidad(ubicacionId.toString(), espirituId, tipoEntidad);
        this.espirituSQLDAO.deleteById(espirituId);
    }
}
