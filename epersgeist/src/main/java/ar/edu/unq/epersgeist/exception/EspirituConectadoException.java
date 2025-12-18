package ar.edu.unq.epersgeist.exception;

public class EspirituConectadoException extends ConflictException{
    
    public EspirituConectadoException() {
        super("El espíritu ya está conectado.");
    }

    public EspirituConectadoException(String mensaje) {
        super(mensaje);
    }
}
