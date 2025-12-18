package ar.edu.unq.epersgeist.exception;

public class UbicacionesNoConectadasException extends ConflictException {
    public UbicacionesNoConectadasException(Long idOrigen, Long idDestino) {
        super("Las ubicaciones con ID " + idOrigen + " y " + idDestino + " no están conectadas.");
    }

    public UbicacionesNoConectadasException() {
        super("Las ubicaciones no están conectadas.");
    }
}