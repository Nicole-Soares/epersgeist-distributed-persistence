package ar.edu.unq.epersgeist.exception;

public class DistanciaMaximaParaInvocacion extends ConflictException {
    public DistanciaMaximaParaInvocacion() {
        super("La distancia entre el espiritu a invocar y el medium es mayor a 50km.");
    }
}
