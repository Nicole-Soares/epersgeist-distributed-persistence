package ar.edu.unq.epersgeist.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DistanciaMaximaParaInvocacion.class)
    public ResponseEntity<ErrorResponseDTO> handleDistanciaMaximaParaInvocacion(DistanciaMaximaParaInvocacion ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(DominacionInvalidaException.class)
    public ResponseEntity<ErrorResponseDTO> handleDomincacionInvalida(DominacionInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(NormalizacionFallidaException.class)
    public ResponseEntity<ErrorResponseDTO> handleNormalizacionFallida(NormalizacionFallidaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(CostoFueraDeRango.class)
    public ResponseEntity<ErrorResponseDTO> handleCostoFueraDeRangoException(CostoFueraDeRango ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(RelacionCircularInvalida.class)
    public ResponseEntity<ErrorResponseDTO> handleRelacionCircularInvalidaException(RelacionCircularInvalida ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DataErrorValidation>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var errors = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(errors.stream().map(DataErrorValidation::new).toList());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleParametrosDePaginacionInvalidos(ConstraintViolationException ex) {
        var errors = ex.getConstraintViolations().stream().map(v -> v.getPropertyPath() + " " + v.getMessage()).toList();
        String msg = "Parametros invalidos: " + String.join("; ", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(msg));
    }

    @ExceptionHandler(NombreRepetidoException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntidadConNombreRepetido(NombreRepetidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("El cuerpo de la solicitud JSON está mal formado."));
    }

    @ExceptionHandler(FlujoFueraDeRangoException.class)
    public ResponseEntity<ErrorResponseDTO> handleFlujoFueraDeRangoException(FlujoFueraDeRangoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(EspirituConectadoException.class)
    public ResponseEntity<ErrorResponseDTO> handleEspirituConectadoException(EspirituConectadoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(DiferenteUbicacionException.class)
    public ResponseEntity<ErrorResponseDTO> handleDiferenteUbicacionException(DiferenteUbicacionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(ExorcistaSinAtacantesException.class)
    public ResponseEntity<ErrorResponseDTO> handleExorcistaSinAtacantesException(ExorcistaSinAtacantesException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(ManaSuperaManaMaxException.class)
    public ResponseEntity<ErrorResponseDTO> handleManaSuperaManaMaxException(ManaSuperaManaMaxException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(ManaNegativoException.class)
    public ResponseEntity<ErrorResponseDTO> handleManaNegativoException(ManaNegativoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(MaximoNivelConexionException.class)
    public ResponseEntity<ErrorResponseDTO> handleMaximoNivelConexionException(MaximoNivelConexionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(NoExistenSantuarioCorrompidoException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoExistenSantuarioCorrompidoException(NoExistenSantuarioCorrompidoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(UbicacionesNoConectadasException.class)
    public ResponseEntity<ErrorResponseDTO> handleUbicacionesNoConectadasException(UbicacionesNoConectadasException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(UbicacionLejanaException.class)
    public ResponseEntity<ErrorResponseDTO> handleUbicacionLejanaException(UbicacionLejanaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage()));
    }

    public record ErrorResponseDTO(String message) {}

    public record DataErrorValidation(String campo, String mensaje) {
        public DataErrorValidation(FieldError error){
            this(error.getField(), "El campo " + error.getField() + " esta mal construido.");
        }
    }

}