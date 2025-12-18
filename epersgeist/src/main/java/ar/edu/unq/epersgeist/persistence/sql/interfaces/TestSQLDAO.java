package ar.edu.unq.epersgeist.persistence.sql.interfaces;

import ar.edu.unq.epersgeist.persistence.sql.entity.espiritu.EspirituSQL;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TestSQLDAO extends JpaRepository<EspirituSQL, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM angel; DELETE FROM demonio; DELETE FROM espiritu; DELETE FROM medium; DELETE FROM ubicacion;", nativeQuery = true)
    void clearAll();
}
