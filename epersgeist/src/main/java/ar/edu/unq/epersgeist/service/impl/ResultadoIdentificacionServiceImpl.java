package ar.edu.unq.epersgeist.service.impl;

import ar.edu.unq.commons.dto.identificacionDTOs.ResultadoIdentificacionDTO;
import ar.edu.unq.commons.neo.dao.ComunicacionActivaDAO;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.EspirituRepository;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.MediumRepository;
import ar.edu.unq.epersgeist.service.interfaces.ResultadoIdentificacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResultadoIdentificacionServiceImpl implements ResultadoIdentificacionService {

    private final MediumRepository mediumRepository;
    private final EspirituRepository espirituRepository;
    private final ComunicacionActivaDAO comunicacionActivaDAO;

    public ResultadoIdentificacionServiceImpl(MediumRepository mediumRepository, EspirituRepository espirituRepository, ComunicacionActivaDAO comunicacionActivaDAO) {
        this.mediumRepository = mediumRepository;
        this.espirituRepository = espirituRepository;
        this.comunicacionActivaDAO = comunicacionActivaDAO;
    }

    public void procesarResultado(ResultadoIdentificacionDTO dto){

        if (dto.exito()) {
            espirituRepository.deleteById(dto.espirituId());
            comunicacionActivaDAO.deleteEspirituById(dto.espirituId());
            Medium medium = mediumRepository.findById(dto.mediumId());
            medium.setCordura(100.0);
            mediumRepository.save(medium);
            System.out.println("Espíritu: " + obtenerMensajeAleatorioExitoso());
            System.out.println("Espíritu eliminado por identificación exitosa.");
        } else if(dto.muere())  {
            mediumRepository.deleteById(dto.mediumId());
            System.out.println("Espíritu: " + obtenerMensajeAleatorioFallido());
            System.out.println("Medium eliminado por identificación fallida.");
        } else {
            Medium medium = mediumRepository.findById(dto.mediumId());
            medium.reducirCordura(50.0);
            mediumRepository.save(medium);
            System.out.println("Espíritu: " + obtenerMensajeAleatorioFallido());
            System.out.println("Medium ha perdido 50 puntos de cordura.");
        }
    }

    private String obtenerMensajeAleatorioExitoso() {
        String[] mensajes = {
                "¡Por fin soy libre!",
                "Gracias por ayudarme.",
                "Mi alma descansa en paz.",
                "¡Has roto mis cadenas!",
                "El ciclo se ha completado.",
                "Ahora puedo descansar.",
                "Tu ayuda me liberó de este mundo.",
                "¡La luz me espera!",
                "Mi tormento ha terminado.",
                "¡Adiós, y gracias!"
        };
        int idx = (int) (Math.random() * mensajes.length);
        return mensajes[idx];
    }

    private String obtenerMensajeAleatorioFallido() {
        String[] mensajes = {
                "¡Maldito seas!",
                "Nunca me atraparán.",
                "Mi venganza será terrible.",
                "¡Has cometido un error fatal!",
                "El ciclo continúa...",
                "¡Soy más fuerte que tú!",
                "Tu fin ha llegado...",
                "¡No has visto lo peor de mí!",
                "¡Mi alma es indomable!",
                "¡Te arrepentirás de esto!"
        };
        int idx = (int) (Math.random() * mensajes.length);
        return mensajes[idx];
    }
}
