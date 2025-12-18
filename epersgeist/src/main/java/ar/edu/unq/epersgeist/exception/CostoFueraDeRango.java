package ar.edu.unq.epersgeist.exception;

public class CostoFueraDeRango extends ConflictException {
    public CostoFueraDeRango() {
        super("El costo se encuentra fuera del rango [0..100].");
    }
}
