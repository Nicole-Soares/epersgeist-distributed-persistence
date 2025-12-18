package ar.edu.unq.epersgeist.persistence.sql.interfaces;

import ar.edu.unq.epersgeist.persistence.sql.entity.MediumSQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediumSQLDAO extends JpaRepository<MediumSQL, Long> {
    List<MediumSQL> findByUbicacionIdAndEspiritusIsEmpty(Long ubicacionId);
}
