package ar.edu.unq.epersgeist.persistence.neo;

import ar.edu.unq.epersgeist.persistence.neo.entity.UbicacionNeo4J;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UbicacionNeo4JDAO extends org.springframework.data.repository.Repository<UbicacionNeo4J, Long> {

    /**
     * Crea una Ubicacion en Neo4j.
     * @param id el ID de la ubicación a crear.
     * @return La UbicacionNeo4J guardada.
     */
    @Query("""
        MERGE (u:Ubicacion {id: $id})
        RETURN u
        """)
    UbicacionNeo4J save(@Param("id") Long id);

    /**
     * Busca una Ubicacion por su ID, incluyendo sus conexiones (si hay).
     * @param id el ID de la ubicación a buscar.
     * @return La UbicacionNeo4J con sus conexiones.
     */
    @Query("""
            MATCH (u:Ubicacion {id: $id})
            OPTIONAL MATCH (u)-[r:CONECTA_A]->(destino:Ubicacion)
            RETURN u, collect(r) AS conexiones, collect(destino) AS destinos
            """)
    UbicacionNeo4J findById(@Param("id") Long id);


    /**
     * Busca todas las Ubicaciones en Neo4j con sus conexiones (si las hay).
     *
     * @return Una lista de todas las UbicacionesNeo4J encontradas.
     */
    @Query("""
            MATCH (u:Ubicacion)
            OPTIONAL MATCH (u)-[c:CONECTA_A]->(destino)
            RETURN u, collect(c), collect(destino)
            """)
    List<UbicacionNeo4J> findAll();

    /**
     * Elimina una Ubicacion por su ID.
     * @param ubicacionId el ID de la ubicación a eliminar.
     */
    @Query("""
            MATCH (u:Ubicacion {id: $ubicacionId})
            DETACH DELETE u
            """)
    void deleteById(@Param("ubicacionId") Long ubicacionId);

    /**
     * Elimina todas las Ubicaciones del grafo.
     */
    @Query ("""
            MATCH (u:Ubicacion)
            DETACH DELETE u
            """)
    void deleteAll();

    /**
     * Conecta dos ubicaciones dadas con un costo dado. Si la ubicacion no existe no la crea.
     * @param idOrigen el ID de la ubicación origen.
     * @param idDestino el ID de la ubicacion destino a conectar.
     * @param costo el costo de realizar la conexion.
     */
    @Query("""
            MATCH (o:Ubicacion {id: $idOrigen})
            MATCH (d:Ubicacion {id: $idDestino})
            MERGE (o)-[r:CONECTA_A]->(d)
            SET r.costo = $costo
            """)
    void conectar(@Param("idOrigen")Long idOrigen,
                  @Param("idDestino")Long idDestino,
                  @Param("costo") Long costo);

    /**
     * Elimina la proyección del grafo si existe.
     */
    @Query("""
        CALL gds.graph.exists('ubicaciones-grafo') YIELD exists
        WITH exists
        WHERE exists
        CALL gds.graph.drop('ubicaciones-grafo') YIELD graphName
        RETURN graphName
        """)
    void eliminarProyeccionGrafoSiExiste();


    /**
     * Proyecta el grafo de ubicaciones en GDS.
     */
    @Query("""
        CALL gds.graph.project(
            'ubicaciones-grafo',
            'Ubicacion',
            {
                CONECTA_A: {
                    type: 'CONECTA_A',
                    properties: 'costo'
                }
            }
        )
        YIELD graphName
        RETURN graphName
        """)
    void proyectarGrafo();


    /**
     * Retorna el camino más rentable entre dos ubicaciones.
     * @param idOrigen El ID de la ubicación de origen.
     * @param idDestino El ID de la ubicación de destino.
     * @return Una lista de ubicaciones que representan el camino más rentable. Estas ubicaciones incluyen unicamente las conexiones hacia la siguiente ubicación en el camino.
     */
    @Query("""
            MATCH (origen:Ubicacion {id: $idOrigen}),
                  (destino:Ubicacion {id: $idDestino})
            CALL gds.shortestPath.dijkstra.stream(
                'ubicaciones-grafo',
                {
                        nodeLabels: ['Ubicacion'],
                        relationshipTypes: ['CONECTA_A'],
                        sourceNode: origen,
                        targetNodes: destino,
                        relationshipWeightProperty: 'costo'
                })
            YIELD path
            WITH nodes(path) AS camino
            UNWIND range(0, size(camino)-1) AS idx
            WITH camino[idx] AS u, camino[idx+1] AS siguiente
            OPTIONAL MATCH (u)-[r:CONECTA_A]->(siguiente)
            RETURN u, collect(r) AS conexiones, collect(siguiente) AS destinos
            """)
    List<UbicacionNeo4J> caminoMasRentable(@Param("idOrigen") Long idOrigen,
                                           @Param("idDestino") Long idDestino);

    /**
     * Encuentra el camino más corto entre dos ubicaciones dadas.
     * @param idOrigen  el ID de la ubicación de origen.
     * @param idDestino el ID de la ubicación de destino.
     * @return Una lista de UbicacionNeo4J que representa el camino más corto desde el origen hasta el destino.
     */
    @Query ("""
            MATCH p = SHORTEST 1
               ( (o:Ubicacion {id: $idOrigen})-[:CONECTA_A*]->(d:Ubicacion {id: $idDestino}) )
            RETURN nodes(p) AS ubicaciones
            """)
    List<UbicacionNeo4J> caminoMasCorto(@Param("idOrigen") Long idOrigen,
                                        @Param("idDestino") Long idDestino);
            
    @Query("""
            OPTIONAL MATCH (o:Ubicacion {id: $idOrigen})
            OPTIONAL MATCH (d:Ubicacion {id: $idDestino})
            RETURN EXISTS((o)-[:CONECTA_A]->(d))
            """)
    boolean estadoConexion(@Param("idOrigen") Long idOrigen,
                           @Param("idDestino") Long idDestino);
}