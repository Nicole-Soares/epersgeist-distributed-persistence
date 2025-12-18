package ar.edu.unq.servicio_mensajeria.service.comunicacion;

import ar.edu.unq.commons.dto.MensajeMediumDTO;
import ar.edu.unq.commons.dto.RespuestaEspirituDTO;
import ar.edu.unq.servicio_mensajeria.mongo.entity.HistorialSesion;
import ar.edu.unq.servicio_mensajeria.mongo.repository.HistorialSesionRepository;
import ar.edu.unq.commons.neo.entity.ComunicacionActiva;
import ar.edu.unq.servicio_mensajeria.neo.service.ComunicacionActivaService;
import ar.edu.unq.servicio_mensajeria.producer.RespuestaEspirituProducer;
import ar.edu.unq.servicio_mensajeria.producer.SinEspiritusProducer;
import ar.edu.unq.servicio_mensajeria.service.respuesta.ServicioDeRespuestas;
import ar.edu.unq.servicio_mensajeria.sql.entity.EspirituCandidato;
import ar.edu.unq.servicio_mensajeria.sql.repository.EspirituCandidatoRepository;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ComunicacionServiceImpl implements ComunicacionService {

    private final RespuestaEspirituProducer respuestaEspirituProducer;
    private final HistorialSesionRepository historialSesionRepository;
    private final ComunicacionActivaService comunicacionActivaService;
    private final EspirituCandidatoRepository espirituCandidatoRepository;
    private final ServicioDeRespuestas servicioDeRespuestas;
    private final SinEspiritusProducer sinEspiritusProducer;

    private final Gson gson = new Gson();

    public ComunicacionServiceImpl(
            RespuestaEspirituProducer respuestaEspirituProducer,
            HistorialSesionRepository historialSesionRepository,
            ComunicacionActivaService comunicacionActivaService,
            EspirituCandidatoRepository espirituCandidatoRepository,
            ServicioDeRespuestas servicioDeRespuestas,
            SinEspiritusProducer sinEspiritusProducer) {

        this.respuestaEspirituProducer = respuestaEspirituProducer;
        this.historialSesionRepository = historialSesionRepository;
        this.comunicacionActivaService = comunicacionActivaService;
        this.espirituCandidatoRepository = espirituCandidatoRepository;
        this.servicioDeRespuestas = servicioDeRespuestas;
        this.sinEspiritusProducer = sinEspiritusProducer;
    }

    @Override
    public MensajeMediumDTO getMensajeMediumDTO(String mensajeJson) {
        return gson.fromJson(mensajeJson, MensajeMediumDTO.class);
    }

    @Override
    public Optional<RespuestaEspirituDTO> procesarMensajeMedium(MensajeMediumDTO dto) {

        Long mediumId = dto.mediumId();
        Long ubicacionId = dto.ubicacionId();

        //Verifico si ya existe una comunicacion activa
        Optional<ComunicacionActiva> comunicacionOpcional =
                comunicacionActivaService.obtenerComunicacionActivaSiExiste(mediumId, ubicacionId);

        EspirituCandidato espirituCandidato;

        if (existeComunicacionActiva(comunicacionOpcional)) {

            Long espirituIdActual = comunicacionOpcional.get().getEspirituId();

            //verifico que no haya sido borrado de la fuente de verdad
            espirituCandidato = espirituCandidatoRepository
                    .findByIdAndDeletedAtFalse(espirituIdActual)
                    .orElse(null);

            if (noExisteEspirituCandidato(espirituCandidato)) {
                //si no existe elimino la comunicacion que quedo y busco otro candidato
                comunicacionActivaService.eliminarComunicacionActiva(mediumId, ubicacionId);

                espirituCandidato = buscarNuevoCandidato(ubicacionId);

                //si no hay mas candidatos envio mensaje al topic por ausencia de espiritus en ubicacion
                if (espirituCandidato == null) {
                    ausenciaDeEspiritus(dto);
                    return Optional.empty();
                }
                // registro la nueva comunicacion
                comunicacionActivaService.registrarOActualizarComunicacionActiva(
                        mediumId,
                        ubicacionId,
                        espirituCandidato.getId(),
                        comunicacionOpcional.get().getNivelPista()
                );

            }

        } else {
            // si nunca existio una comunicacion activa -> busco el candidato en la fuente de verdad
            espirituCandidato = buscarNuevoCandidato(ubicacionId);

            if (noExisteEspirituCandidato(espirituCandidato)) {
                ausenciaDeEspiritus(dto);
                return Optional.empty();
            }

            comunicacionActivaService.registrarOActualizarComunicacionActiva(
                    mediumId,
                    ubicacionId,
                    espirituCandidato.getId(),
                    0
            );
        }

        ComunicacionActiva comunicacion = comunicacionOpcional.get();

        String texto = servicioDeRespuestas.construirTextoConEspiritu(
                espirituCandidato,
                comunicacion
        );

        comunicacionActivaService.registrarOActualizarComunicacionActiva(
                mediumId,
                ubicacionId,
                espirituCandidato.getId(),
                comunicacionOpcional.get().getNivelPista()
        );

        RespuestaEspirituDTO respuesta = new RespuestaEspirituDTO(
                espirituCandidato.getId(),
                mediumId,
                texto,
                espirituCandidato.getHostilidad()
        );

        guardarHistorial(dto, espirituCandidato.getId(), texto);
        return Optional.of(respuesta);
    }

    // --- Helpers ---

    private EspirituCandidato buscarNuevoCandidato(Long ubicacionId) {
        return espirituCandidatoRepository
                .buscarCandidatoAleatorio(ubicacionId)
                .orElse(null);
    }

    private void guardarHistorial(MensajeMediumDTO dto, Long espirituId, String respuesta) {
        HistorialSesion h = new HistorialSesion(
                dto.mediumId(),
                espirituId,
                dto.ubicacionId(),
                dto.mensaje(),
                respuesta
        );
        historialSesionRepository.save(h);
    }

    private void ausenciaDeEspiritus(MensajeMediumDTO dto) {
        sinEspiritusProducer.enviarEventoSinEspiritus(dto.ubicacionId());
    }

    private static boolean noExisteEspirituCandidato(EspirituCandidato espirituCandidato) {
        return espirituCandidato == null;
    }

    private static boolean existeComunicacionActiva(Optional<ComunicacionActiva> comunicacionOpt) {
        return comunicacionOpt.isPresent();
    }
}
