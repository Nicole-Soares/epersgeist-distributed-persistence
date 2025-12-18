package ar.edu.unq.epersgeist.exception;

public class NombreRepetidoException extends ConflictException {
    public NombreRepetidoException(String nombreEntidad) {
        super("El nombre " + nombreEntidad + " ya está en uso.");
    }
}
