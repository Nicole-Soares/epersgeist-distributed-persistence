package ar.edu.unq.epersgeist.exception;

public class UbicacionSolapadaException extends ConflictException {
    public UbicacionSolapadaException(String nombreEntidad) {
        super("La ubicacion " + nombreEntidad + " solapa con otra ubicacion existente.");
    }
}
