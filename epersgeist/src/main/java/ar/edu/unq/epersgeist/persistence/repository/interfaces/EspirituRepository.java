package ar.edu.unq.epersgeist.persistence.repository.interfaces;

import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EspirituRepository {
    Espiritu findById(Long espirituId);
    List<Espiritu> findAll();
    void deleteById(Long espirituId);
    Espiritu save(Espiritu espiritu);
    List<Espiritu> findByMediumId(Long mediumId);
    Page<Demonio> findDemonios(Pageable pageable);
    List<Angel> findAllAngelesByMediumId(Long idMediumExorcista);
    List<Espiritu> findByUbicacionId(Long ubicacionId);
    List<? extends Espiritu> saveAll(List<? extends Espiritu> espiritus);
}
