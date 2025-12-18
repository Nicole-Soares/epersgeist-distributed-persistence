package ar.edu.unq.epersgeist.exception;

public class UbicacionLejanaException extends ConflictException{

    public UbicacionLejanaException() {
        super("No es posible llegar a esa ubicacion desde la ubicacion actual.");
    }

    public UbicacionLejanaException(String mensaje) {
        super(mensaje);
    }
}
