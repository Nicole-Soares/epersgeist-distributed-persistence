package ar.edu.unq.epersgeist.exception;

public class ManaSuperaManaMaxException extends ConflictException {

    public ManaSuperaManaMaxException() {
        super("El Medium no puede tener un maná mayor a su maná maximo");
    }
}
