package ar.edu.unq.epersgeist.exception;

public class EspirituNoPuedeMoverseSoloException extends ConflictException{
    public EspirituNoPuedeMoverseSoloException() {
        super("No se puede mover un espiritu por si solo.");
    }
}
