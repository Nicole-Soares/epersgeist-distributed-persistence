package ar.edu.unq.epersgeist.persistence.repository.impl;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.persistence.mapper.interfaces.CoordenadasMapper;
import ar.edu.unq.epersgeist.persistence.mapper.interfaces.MediumMapper;
import ar.edu.unq.epersgeist.persistence.mongo.entity.CoordenadaMongo;
import ar.edu.unq.epersgeist.persistence.mongo.entity.UbicacionMongo;
import ar.edu.unq.epersgeist.persistence.mongo.interfaces.UbicacionMongoDAO;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.MediumRepository;
import ar.edu.unq.epersgeist.persistence.sql.interfaces.MediumSQLDAO;
import ar.edu.unq.epersgeist.persistence.sql.entity.MediumSQL;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MediumRepositoryImpl implements MediumRepository {
    private final MediumSQLDAO mediumSQLDAO;
    private final MediumMapper mediumMapper;
    private final UbicacionMongoDAO ubicacionMongoDAO;
    private final CoordenadasMapper coordenadasMapper;

    public MediumRepositoryImpl(MediumSQLDAO mediumSQLDAO, MediumMapper mediumMapper, UbicacionMongoDAO ubicacionMongoDAO, CoordenadasMapper coordenadasMapper) {
        this.mediumSQLDAO = mediumSQLDAO;
        this.mediumMapper = mediumMapper;
        this.ubicacionMongoDAO = ubicacionMongoDAO;
        this.coordenadasMapper = coordenadasMapper;
    }

    @Override
    public Medium save(Medium medium) {
        MediumSQL mediumSQL = mediumMapper.aEntidad(medium);
        mediumSQLDAO.save(mediumSQL);
        ubicacionMongoDAO.actualizarOcrearCoordenadaEntidad(mediumSQL.getUbicacion().getId().toString(), mediumSQL.getId(), medium.getRef(), medium.getCoordenadas());
        CoordenadaMongo coordenadaMongo = ubicacionMongoDAO.findCoordenadaDeEntidad(mediumSQL.getUbicacion().getId(), mediumSQL.getId());
        Coordenadas coordenadas = coordenadasMapper.aModelo(coordenadaMongo);
        medium.setId(mediumSQL.getId());
        medium.setCoordenadas(coordenadas);
        return medium;
    }

    @Override
    public Medium findById(Long mediumId) {
        //  Buscar MediumSQL
        MediumSQL mediumSQL = mediumSQLDAO.findById(mediumId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el Medium con id " + mediumId));

        //  Buscar ubicación Mongo
        Long ubicacionSQLID = mediumSQL.getUbicacion().getId();
        String ubicacionMongoId = ubicacionSQLID.toString();

        UbicacionMongo ubicacionMongo = ubicacionMongoDAO.findById(ubicacionMongoId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la UbicacionMongo con id " + ubicacionMongoId));

        CoordenadaMongo coordenadasMongo = ubicacionMongoDAO.findCoordenadaDeEntidad(ubicacionSQLID, mediumId);

        return mediumMapper.aModelo(mediumSQL, coordenadasMongo, ubicacionMongo);
    }


    @Override
    public void deleteById(Long mediumId) {
        Medium medium = findById(mediumId);
        Long ubicacionId = medium.getUbicacion().getId();
        String tipoEntidad = medium.getClass().getSimpleName();
        ubicacionMongoDAO.eliminarCoordenadaEntidad(ubicacionId.toString(), mediumId, tipoEntidad);
        this.mediumSQLDAO.deleteById(mediumId);
    }

    @Override
    public List<Medium> findAll() {
        return mediumSQLDAO.findAll()
                .stream()
                .map(mediumSQL -> {
                    Long ubicacionSQLID = mediumSQL.getUbicacion().getId();
                    String ubicacionMongoId = ubicacionSQLID.toString();
                    UbicacionMongo ubicacionMongo = ubicacionMongoDAO.findById(ubicacionMongoId).orElse(null);
                    CoordenadaMongo coordenadasMongo = ubicacionMongoDAO.findCoordenadaDeEntidad(ubicacionSQLID, mediumSQL.getId());
                    return mediumMapper.aModelo(mediumSQL, coordenadasMongo, ubicacionMongo);
                })
                .toList();
    }

    @Override
    public List<Medium> findByUbicacionIdAndEspiritusIsEmpty(Long ubicacionId) {
        return mediumSQLDAO.findByUbicacionIdAndEspiritusIsEmpty(ubicacionId)
                .stream()
                .map(mediumSQL -> {
                    Long ubicacionSQLID = mediumSQL.getUbicacion().getId();
                    String ubicacionMongoId = ubicacionSQLID.toString();
                    UbicacionMongo ubicacionMongo = ubicacionMongoDAO.findById(ubicacionMongoId).orElse(null);
                    CoordenadaMongo coordenadasMongo = ubicacionMongoDAO.findCoordenadaDeEntidad(ubicacionSQLID, mediumSQL.getId());
                    return mediumMapper.aModelo(mediumSQL, coordenadasMongo, ubicacionMongo);
                })
                .toList();
    }
}
