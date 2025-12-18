package ar.edu.unq.epersgeist.exception;

public class DiferenteUbicacionException extends ConflictException {
    public DiferenteUbicacionException() {
        super("La ubicacion es distinta a la esperada.");
    }

    public DiferenteUbicacionException(String mensaje) {
        super(mensaje);
    }
}
