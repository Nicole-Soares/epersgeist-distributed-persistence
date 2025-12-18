package ar.edu.unq.epersgeist.persistence.sql.interfaces;

import ar.edu.unq.epersgeist.persistence.sql.entity.espiritu.AngelSQL;
import ar.edu.unq.epersgeist.persistence.sql.entity.espiritu.DemonioSQL;
import ar.edu.unq.epersgeist.persistence.sql.entity.espiritu.EspirituSQL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspirituSQLDAO extends JpaRepository<EspirituSQL, Long> {
    List<EspirituSQL> findByMediumId(Long mediumId);

    List<EspirituSQL> findByUbicacionId(Long ubicacionId);

    @Query(
            value = "select d from Demonio d",
            countQuery = "select count(d) from Demonio d"
    )
    Page<DemonioSQL> findDemonios(Pageable pageable);

    @Query("SELECT a FROM Angel a WHERE a.medium.id = :idMedium")
    List<AngelSQL> findAllAngelesByMediumId(Long idMedium);
}