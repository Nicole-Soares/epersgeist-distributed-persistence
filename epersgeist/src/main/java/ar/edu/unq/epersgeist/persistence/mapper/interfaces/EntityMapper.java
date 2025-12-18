package ar.edu.unq.epersgeist.persistence.mapper.interfaces;

public interface EntityMapper<D, E> {
    E aEntidad(D modelo);
    D aModelo(E entidad);
}
