package ar.edu.unq.epersgeist.service.interfaces;

import ar.edu.unq.epersgeist.controller.dto.espiritu.ActualizarEspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.espiritu.CrearEspirituDTO;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EspirituService {
    Espiritu create(Espiritu espiritu);
    Espiritu create(CrearEspirituDTO espirituDTO);
    Espiritu findById(Long espirituId);
    List<Espiritu> recuperarTodos();
    Espiritu update(Espiritu espiritu);
    Espiritu actualizarEspiritu(Long espirituId, ActualizarEspirituDTO dto);
    void delete(Long espirituId);
    Medium conectar(Long espirituId, Long mediumId);
    List<Espiritu> espiritusDelMedium(Long mediumId);
    Page<Demonio> espiritusDemoniacos(Pageable pageable);
    void dominar(Long espirituDominanteId, Long espirituADominarId);

    Espiritu findEspirituConComunicacionActivaByMediumId(Long mediumId);
}
