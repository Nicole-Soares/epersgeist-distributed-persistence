package ar.edu.unq.epersgeist.exception;

public class RelacionCircularInvalida extends ConflictException {
    public RelacionCircularInvalida() {
        super("No se puede conectar una ubicacion consigo misma.");
    }
}
