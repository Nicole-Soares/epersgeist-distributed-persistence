package ar.edu.unq.epersgeist.exception;

public class DominioCircularException extends ConflictException {
    public DominioCircularException() {
        super("No se puede dominar a un espíritu que te domina");
    }
}
