package ar.edu.unq.epersgeist.exception;

public class ConflictException extends RuntimeException{
    public ConflictException(String mensaje){
        super(mensaje);
    }
}
