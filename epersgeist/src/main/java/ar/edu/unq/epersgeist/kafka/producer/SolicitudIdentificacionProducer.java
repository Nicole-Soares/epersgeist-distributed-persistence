package ar.edu.unq.epersgeist.kafka.producer;

import ar.edu.unq.commons.dto.identificacionDTOs.SolicitudIdentificacionDTO;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.service.interfaces.EspirituService;
import ar.edu.unq.epersgeist.service.interfaces.MediumService;
import com.google.gson.Gson;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SolicitudIdentificacionProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Gson gson = new Gson();
    private final MediumService mediumService;
    private final EspirituService espirituService;

    public SolicitudIdentificacionProducer(KafkaTemplate<String, String> kafkaTemplate, MediumService mediumService, EspirituService espirituService) {
        this.kafkaTemplate = kafkaTemplate;
        this.mediumService = mediumService;
        this.espirituService = espirituService;
    }

    public void enviarSolicitud(Long mediumId, SolicitudIdentificacionDTO.MediumConjetura conjetura) {

        Medium medium = mediumService.findById(mediumId);
        Espiritu espiritu = espirituService.findEspirituConComunicacionActivaByMediumId(mediumId);

        SolicitudIdentificacionDTO.Real real = new SolicitudIdentificacionDTO.Real(
                espiritu.getId(),
                espiritu.getNombre(),
                espiritu.getClass().getSimpleName(),
                espiritu.getHostilidadNormalizada(),
                0
        );

        SolicitudIdentificacionDTO solicitud = new SolicitudIdentificacionDTO(
                mediumId,
                medium.getCorduraNormalizada(),
                real,
                conjetura
        );

        String json = gson.toJson(solicitud);
        kafkaTemplate.send("solicitud_identificacion", json);
        System.out.println("[epersgeist] Envié solicitud_identificacion JSON: " + json);
    }
}