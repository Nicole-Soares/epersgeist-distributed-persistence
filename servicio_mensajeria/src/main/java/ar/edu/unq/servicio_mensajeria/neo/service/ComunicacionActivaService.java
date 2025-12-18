package ar.edu.unq.servicio_mensajeria.neo.service;

import ar.edu.unq.commons.neo.entity.ComunicacionActiva;
import ar.edu.unq.commons.neo.dao.ComunicacionActivaDAO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ComunicacionActivaService {

    private final ComunicacionActivaDAO repo;

    public ComunicacionActivaService(ComunicacionActivaDAO repo) {
        this.repo = repo;
    }

    public Optional<ComunicacionActiva> obtenerComunicacionActivaSiExiste(Long mediumId, Long ubicacionId) {
        return repo.findByMediumIdAndUbicacionId(mediumId, ubicacionId);
    }

    public void registrarOActualizarComunicacionActiva(Long mediumId, Long ubicacionId, Long espirituId, int nivelPista) {
        repo.actualizarComunicacionActiva(mediumId, ubicacionId, espirituId, nivelPista);
    }

    public void eliminarComunicacionActiva(Long mediumId, Long ubicacionId) {
        repo.deleteByMediumIdAndUbicacionId(mediumId, ubicacionId);
    }
}
