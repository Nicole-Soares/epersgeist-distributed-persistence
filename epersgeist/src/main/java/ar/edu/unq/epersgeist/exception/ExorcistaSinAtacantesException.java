package ar.edu.unq.epersgeist.exception;

public class ExorcistaSinAtacantesException extends ConflictException{

    public ExorcistaSinAtacantesException() {
        super("El exorcista no tiene ángeles asignados.");
    }
}
