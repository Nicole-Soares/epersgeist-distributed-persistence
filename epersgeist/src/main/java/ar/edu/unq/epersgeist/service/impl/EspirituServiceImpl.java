package ar.edu.unq.epersgeist.service.impl;


import ar.edu.unq.epersgeist.controller.dto.espiritu.ActualizarEspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.espiritu.CrearEspirituDTO;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.CoordenadaRepository;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.EspirituRepository;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.MediumRepository;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.UbicacionRepository;
import ar.edu.unq.epersgeist.service.interfaces.EspirituService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EspirituServiceImpl implements EspirituService {

    private final EspirituRepository espirituRepository;
    private final UbicacionRepository ubicacionRepository;
    private final MediumRepository mediumRepository;

    public EspirituServiceImpl(EspirituRepository espirituRepository, UbicacionRepository ubicacionRepository, MediumRepository mediumRepository, CoordenadaRepository coordenadaRepository) {
        this.espirituRepository = espirituRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.mediumRepository = mediumRepository;
    }

    @Override
    public Espiritu create(Espiritu espiritu) {
        return this.espirituRepository.save(espiritu);
    }

    @Override
    public Espiritu create(CrearEspirituDTO espirituDTO) {
        Ubicacion ubicacionEspiritu = ubicacionRepository.findByIdSinConexiones(espirituDTO.ubicacionId());
        Espiritu espiritu = espirituDTO.aModelo(ubicacionEspiritu);
        return this.espirituRepository.save(espiritu);
    }

    @Override
    public Espiritu findById(Long espirituId) {
        return this.espirituRepository.findById(espirituId);
    }

    @Override
    public List<Espiritu> recuperarTodos() {
        return this.espirituRepository.findAll();
    }

    @Override
    public Espiritu update(Espiritu espiritu) {
        return this.espirituRepository.save(espiritu);
    }

    @Override
    public Espiritu actualizarEspiritu(Long id, ActualizarEspirituDTO dto) {
        Espiritu espiritu = this.espirituRepository.findById(id);
        dto.sobrescribir(espiritu);
        return this.espirituRepository.save(espiritu);
    }

    @Override
    public void delete(Long espirituId) {
        this.espirituRepository.deleteById(espirituId);
    }

    @Override
    public Medium conectar(Long espirituId, Long mediumId) {
        Espiritu espiritu = this.espirituRepository.findById(espirituId);
        Medium medium = this.mediumRepository.findById(mediumId);
        medium.conectarseAEspiritu(espiritu);
        return this.mediumRepository.save(medium);
    }

    @Override
    public List<Espiritu> espiritusDelMedium(Long mediumId) {
       return this.espirituRepository.findByMediumId(mediumId);
    }

    @Override
    public Page<Demonio> espiritusDemoniacos(Pageable pageable) {
        return this.espirituRepository.findDemonios(pageable);
    }

    @Override
    public void dominar(Long espirituDominanteId, Long espirituADominarId){
        Espiritu espirituDominante = this.espirituRepository.findById(espirituDominanteId);
        Espiritu espirituADominar = this.espirituRepository.findById(espirituADominarId);
        espirituDominante.dominar(espirituADominar);
        this.espirituRepository.save(espirituADominar);
    }

    @Override
    public Espiritu findEspirituConComunicacionActivaByMediumId(Long mediumId) {

        Long espirituId = ubicacionRepository.findEspirituIdConComunicacionActivaConMediumById(mediumId);

        return espirituRepository.findById(espirituId);
    }
}