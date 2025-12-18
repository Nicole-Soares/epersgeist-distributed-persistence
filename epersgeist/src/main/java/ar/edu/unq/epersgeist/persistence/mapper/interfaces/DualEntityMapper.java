package ar.edu.unq.epersgeist.persistence.mapper.interfaces;

public interface DualEntityMapper<D, E1, E2> {
    D aModelo(E1 entidad1, E2 entidad2);
    E2 aEntidadSecundaria(D modelo);
    E1 aEntidad(D modelo);
}
