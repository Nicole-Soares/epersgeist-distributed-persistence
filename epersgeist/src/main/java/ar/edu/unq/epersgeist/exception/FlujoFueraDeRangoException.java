package ar.edu.unq.epersgeist.exception;

public class FlujoFueraDeRangoException extends ConflictException {
    public FlujoFueraDeRangoException() {super("El flujo debe ser un valor numérico positivo menor o igual a 100");}
}
