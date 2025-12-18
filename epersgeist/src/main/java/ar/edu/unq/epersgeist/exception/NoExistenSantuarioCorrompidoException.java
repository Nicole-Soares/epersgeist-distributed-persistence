package ar.edu.unq.epersgeist.exception;

public class NoExistenSantuarioCorrompidoException extends ConflictException {
    public NoExistenSantuarioCorrompidoException() {
        super("No existen santuarios corrompidos.");
    }
}
