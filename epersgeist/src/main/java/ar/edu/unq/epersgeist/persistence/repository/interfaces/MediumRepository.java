package ar.edu.unq.epersgeist.persistence.repository.interfaces;
import ar.edu.unq.epersgeist.modelo.Medium;
import java.util.List;

public interface MediumRepository {
    Medium save(Medium medium);
    Medium findById(Long mediumId);
    void deleteById(Long mediumId);
    List<Medium> findAll();
    List<Medium> findByUbicacionIdAndEspiritusIsEmpty(Long ubicacionId);
}
