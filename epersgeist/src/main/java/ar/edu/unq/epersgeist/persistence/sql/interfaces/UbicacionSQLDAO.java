package ar.edu.unq.epersgeist.persistence.sql.interfaces;

import ar.edu.unq.epersgeist.modelo.estadistica.CantidadDeDemoniosEnUnSantuario;
import ar.edu.unq.epersgeist.modelo.estadistica.SantuarioMasCorrupto;
import ar.edu.unq.epersgeist.persistence.sql.entity.UbicacionSQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UbicacionSQLDAO extends JpaRepository<UbicacionSQL, Long> {

    /**
     * Encuentra el santuario más corrupto y el medium con más demonios en ese santuario.
     * @return SantuarioMasCorrupto que contiene el ID del santuario y el ID del medium con más demonios.
     */
    @Query(value = """
    SELECT u.id AS idSantuario, m.id AS idMediumConMasDemonios
    FROM ubicacion u
    LEFT JOIN espiritu e ON e.ubicacion_id = u.id
    LEFT JOIN demonio d ON d.id = e.id
    LEFT JOIN angel a ON a.id = e.id
    LEFT JOIN medium m ON m.id = (
        SELECT m2.id
        FROM medium m2
        JOIN espiritu e2 ON e2.medium_id = m2.id
        JOIN demonio d2 ON d2.id = e2.id
        WHERE m2.ubicacion_id = u.id
        GROUP BY m2.id
        ORDER BY COUNT(d2.id) DESC
        LIMIT 1
    )
    WHERE u.tipo = 'SANTUARIO'
    GROUP BY u.id, m.id
    ORDER BY
        SUM(CASE WHEN d.id IS NOT NULL THEN 1 ELSE 0 END) - SUM(CASE WHEN a.id IS NOT NULL THEN 1 ELSE 0 END) DESC
    LIMIT 1
    """, nativeQuery = true)
    SantuarioMasCorrupto findSantuarioMasCorruptoYMedium();

    /**
     * Obtiene la cantidad de demonios en un santuario específico.
     * @param idSantuario ID del santuario.
     * @return CantidadDeDemoniosEnUnSantuario con los detalles de demonios en el santuario.
     */
    @Query("""
    SELECT new ar.edu.unq.epersgeist.modelo.estadistica.CantidadDeDemoniosEnUnSantuario(
        u.nombre,
        (
            SELECT COUNT(e)
            FROM Espiritu e
            WHERE e.ubicacion = u AND TYPE(e) = Demonio
        ),
        (
            SELECT COUNT(e)
            FROM Espiritu e
            WHERE e.ubicacion = u AND TYPE(e) = Demonio AND e.medium IS NULL
        )
    )
    FROM Ubicacion u
    WHERE u.id = :idSantuario
    """)
    CantidadDeDemoniosEnUnSantuario cantidadDeDemoniosEnElSantuario(Long idSantuario);


    /**
     * Verifica si existe una Ubicacion con el nombre dado.
     * @param nombre El nombre a verificar.
     * @return true si ya existe, sino false.
     */
    boolean existsByNombre(String nombre);

    List<UbicacionSQL> findByFlujoEnergiaGreaterThan(Integer umbralDeEnergia);
}