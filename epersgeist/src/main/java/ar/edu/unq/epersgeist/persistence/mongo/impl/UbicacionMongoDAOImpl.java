package ar.edu.unq.epersgeist.persistence.mongo.impl;

import ar.edu.unq.epersgeist.exception.NormalizacionFallidaException;
import ar.edu.unq.epersgeist.modelo.estadistica.ReportePromedio;
import ar.edu.unq.epersgeist.modelo.sensor.DatoSensorNormalizado;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.persistence.mongo.entity.CoordenadaMongo;
import ar.edu.unq.epersgeist.persistence.mongo.entity.EntidadEnCoordenadaMongo;
import ar.edu.unq.epersgeist.persistence.mongo.entity.UbicacionMongo;
import ar.edu.unq.epersgeist.persistence.mongo.interfaces.UbicacionMongoRepositoryCustom;
import jakarta.persistence.EntityNotFoundException;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UbicacionMongoDAOImpl implements UbicacionMongoRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public UbicacionMongoDAOImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void eliminarCoordenadaEntidad(String ubicacionMongoId, Long entidadSQLId, String tipoEntidad) {
        // Busca el id del documento
        Query query = new Query(Criteria.where("id").is(ubicacionMongoId));
        // hace el update para definir el pull (borrar)
        Update update = new Update();

        // elimina el subdocumento que coincida con el filtro
        // el '$[]' aplica la operacion a todas las coordenadas
        update.pull("coordenadas.$[].entidadesEnCoors", new Document("entidadSQLId", entidadSQLId).append("entidadRef", tipoEntidad));

        // ejecuta la actualizacion en la BD
        mongoTemplate.updateMulti(query, update, UbicacionMongo.class);
    }

    @Override
    public CoordenadaMongo findCoordenadaDeEntidad(Long ubicacionId, Long entidadId) {
        String id = ubicacionId.toString();
        Criteria matchEntidad = Criteria.where("entidadesEnCoors").elemMatch(Criteria.where("entidadSQLId").is(entidadId));

        Query query = new Query(Criteria.where("_id").is(id).and("coordenadas").elemMatch(matchEntidad));
        query.fields().elemMatch("coordenadas", matchEntidad).include("_id");

        UbicacionMongo doc = mongoTemplate.findOne(query, UbicacionMongo.class);
        if (doc == null || doc.getCoordenadas() == null || doc.getCoordenadas().isEmpty())
            throw new EntityNotFoundException("No se encontraron las coordenadas de la entidad con id :" + entidadId);
        return doc.getCoordenadas().getFirst();
    }

    @Override
    public void actualizarOcrearCoordenadaEntidad(String ubicacionMongoId, Long entidadSQLId, String tipoEntidad, Coordenadas coordenadasEntidad) {
        double lon = coordenadasEntidad.getLongitud();
        double lat = coordenadasEntidad.getLatitud();
        GeoJsonPoint nuevoPunto = new GeoJsonPoint(lon, lat);
        EntidadEnCoordenadaMongo entidadMongo = new EntidadEnCoordenadaMongo(entidadSQLId, tipoEntidad);

        // Elimina referencia anterior de la entidad en TODAS las coordenadas
        Query pullQuery = new Query(Criteria.where("_id").is(ubicacionMongoId));
        Update pullUpdate = new Update().pull("coordenadas.$[].entidadesEnCoors", new Document("entidadSQLId", entidadSQLId).append("entidadRef", tipoEntidad));
        // Usamos updateMulti para asegurar la limpieza en tod0 el array 'coordenadas'
        mongoTemplate.updateMulti(pullQuery, pullUpdate, UbicacionMongo.class);

        // Busca si NO existe una coordenada con ese punto
        Query ensureCoordQuery = new Query(Criteria.where("_id").is(ubicacionMongoId).and("coordenadas.punto").ne(nuevoPunto));

        // Si no existe, la crea con una lista vacía de entidades
        Update ensureCoordUpdate = new Update().push("coordenadas", new CoordenadaMongo(lon, lat, new ArrayList<>()));

        mongoTemplate.updateFirst(ensureCoordQuery, ensureCoordUpdate, UbicacionMongo.class);

        // Buscamos la Ubicacion y la Coordenada específica que coincida con el punto
        Query updateEntityQuery = new Query(Criteria.where("_id").is(ubicacionMongoId).and("coordenadas").elemMatch(Criteria.where("punto").is(nuevoPunto)));

        //$addToSet inserta la entidad en el array 'entidadesEnCoors' del subdocumento que hizo match ($)
        Update updateEntity = new Update().addToSet("coordenadas.$.entidadesEnCoors", entidadMongo);
        mongoTemplate.updateFirst(updateEntityQuery, updateEntity, UbicacionMongo.class);
    }

    @Override
    public void normalizacionDeDocumento() {
        final String ORIGEN = "epersgeist_unnormalized";
        final String DESTINO = "epersgeist_normalized";

        long conteoOrigen = mongoTemplate.getCollection(ORIGEN).estimatedDocumentCount();

        //corroboro si hay datos en el documento epersgeist_unnormalized, si no hay lanzo excepcion ya que no ejecutaron el script
        if (conteoOrigen == 0) {
            throw new NormalizacionFallidaException("No hay datos a normalizar. Debe agregar los datos no normalizados primero.");
        }

        // Conversion de PROXIMIDAD a METROS
        Document selectorProximidad = new Document("$switch", new Document("branches", List.of(
                new Document("case", new Document("$eq", List.of("$unidad", "millas"))).append("then", new Document("$multiply", List.of("$valor", 1609.344))),
                new Document("case", new Document("$eq", List.of("$unidad", "kilometros"))).append("then", new Document("$multiply", List.of("$valor", 1000))),
                new Document("case", new Document("$eq", List.of("$unidad", "pies"))).append("then", new Document("$multiply", List.of("$valor", 0.3048))),
                new Document("case", new Document("$eq", List.of("$unidad", "metros"))).append("then", "$valor")
        )).append("default", "$valor"));

        // Conversion de PRESIÓN a HECTOPASCALES (hPa)
        Document selectorPresion = new Document("$switch", new Document("branches", List.of(
                new Document("case", new Document("$eq", List.of("$unidad", "atm"))).append("then", new Document("$multiply", List.of("$valor", 1013.25))),
                new Document("case", new Document("$eq", List.of("$unidad", "mmHg"))).append("then", new Document("$multiply", List.of("$valor", 1.33322))),
                new Document("case", new Document("$eq", List.of("$unidad", "hPa"))).append("then", "$valor")
        )).append("default", "$valor"));

        // Conversion de TEMPERATURA a CELSIUS (C)
        Document selectorTemperatura = new Document("$switch", new Document("branches", List.of(
                new Document("case", new Document("$eq", List.of("$unidad", "F"))).append("then", new Document("$multiply", List.of(new Document("$subtract", List.of("$valor", 32)), 0.55555555555))), // 5/9
                new Document("case", new Document("$eq", List.of("$unidad", "K"))).append("then", new Document("$subtract", List.of("$valor", 273.15))),
                new Document("case", new Document("$eq", List.of("$unidad", "C"))).append("then", "$valor")
        )).append("default", "$valor"));

        // Conversion de SONIDO a DECIBELES (dB)
        Document selectorSonido = new Document("$switch", new Document("branches", List.of(
                new Document("case", new Document("$eq", List.of("$unidad", "sones"))).append("then", new Document("$add", List.of(
                        26.2,
                        new Document("$multiply", List.of(33.2, new Document("$log10", "$valor")))
                ))),
                new Document("case", new Document("$eq", List.of("$unidad", "dB"))).append("then", "$valor")
        )).append("default", "$valor"));

        Document selectorValorNormalizado = new Document("$switch", new Document("branches", List.of(
                new Document("case", new Document("$eq", List.of("$tipo", "proximidad"))).append("then", selectorProximidad),
                new Document("case", new Document("$eq", List.of("$tipo", "presion"))).append("then", selectorPresion),
                new Document("case", new Document("$eq", List.of("$tipo", "temperatura"))).append("then", selectorTemperatura),
                new Document("case", new Document("$eq", List.of("$tipo", "sonido"))).append("then", selectorSonido)
        )).append("default", "$valor"));

        Document selectorUnidadNormalizada = new Document("$switch", new Document("branches", List.of(
                new Document("case", new Document("$eq", List.of("$tipo", "proximidad"))).append("then", "metros"),
                new Document("case", new Document("$eq", List.of("$tipo", "presion"))).append("then", "hPa"),
                new Document("case", new Document("$eq", List.of("$tipo", "temperatura"))).append("then", "C"),
                new Document("case", new Document("$eq", List.of("$tipo", "sonido"))).append("then", "dB")
        )).append("default", "$unidad"));


        // Agrego campos
        AddFieldsOperation operacionAgregarCampos = AddFieldsOperation.addField("valor_normalizado")
                .withValue(selectorValorNormalizado)
                .addField("unidad_normalizada")
                .withValue(selectorUnidadNormalizada)
                .build();

        // Renombro campos y proyecto el resultado
        ProjectionOperation operacionProyeccion = Aggregation.project()
                .andInclude("fecha", "tipo")
                .and("$sensor_id").as("id_sensor")
                .and("$valor").as("valor_original")
                .and("$unidad").as("unidad_original")
                .and("$valor_normalizado").as("valor")
                .and("$unidad_normalizada").as("unidad");

        OutOperation operacionVolcado = Aggregation.out(DESTINO);

        // Construyo la agregación final
        Aggregation agregacion = Aggregation.newAggregation(operacionAgregarCampos, operacionProyeccion, operacionVolcado);

        mongoTemplate.aggregate(agregacion, ORIGEN, Document.class);

        long conteoDestino = mongoTemplate.getCollection(DESTINO).estimatedDocumentCount();

        // Validación: Si el conteo de origen no coincide con el conteo de destino, algo falló
        if (conteoDestino != conteoOrigen) {
            throw new NormalizacionFallidaException("Fallo la normalización.");
        }
    }

    @Override
    public List<ReportePromedio> obtenerPromedioPorTipoSensor() {
        final String COLECCION_NORMALIZADA = "epersgeist_normalized";

        long conteoDocumentos = mongoTemplate.count(new Query(), COLECCION_NORMALIZADA);

        // corroboro si hay datos en el documento epersgeist_unnormalized, si no hay lanzo excepcion ya que no ejecutaron el script
        if (conteoDocumentos == 0) {
            throw new NormalizacionFallidaException(
                    "No existen datos normalizados para calcular promedios."
            );
        }
        GroupOperation operacionAgrupacion = Aggregation.group("tipo")
                .avg("valor").as("promedio")
                .first("unidad").as("unidad");

        SortOperation operacionOrdenamiento = Aggregation.sort(Sort.Direction.DESC, "promedio");

        Aggregation agregacion = Aggregation.newAggregation(operacionAgrupacion, operacionOrdenamiento);

        return mongoTemplate.aggregate(agregacion, "epersgeist_normalized", ReportePromedio.class)
                .getMappedResults();
    }

    @Override
    public double distanciaEntre(GeoJsonPoint pointMedium, GeoJsonPoint pointEspiritu) {
        Document p1 = new Document("type", "Point")
                .append("coordinates", List.of(pointMedium.getX(), pointMedium.getY())); // [lon, lat]

        Document p2 = new Document("type", "Point")
                .append("coordinates", List.of(pointEspiritu.getX(), pointEspiritu.getY()));

        // Pipeline de agregación: inyecta los puntos y calcula la distancia (Haversine) en KM
        List<Document> pipeline = List.of(
                // 1) Inyectamos los puntos "en memoria"
                new Document("$documents", List.of(
                        new Document("p1", p1).append("p2", p2)
                )),
                // 2) Calculamos la distancia
                new Document("$project", new Document("_id", 0).append("distanceKm",
                        new Document("$let", new Document("vars", new Document()
                                .append("lat1", new Document("$degreesToRadians",
                                        new Document("$arrayElemAt", List.of("$p1.coordinates", 1))))
                                .append("lon1", new Document("$degreesToRadians",
                                        new Document("$arrayElemAt", List.of("$p1.coordinates", 0))))
                                .append("lat2", new Document("$degreesToRadians",
                                        new Document("$arrayElemAt", List.of("$p2.coordinates", 1))))
                                .append("lon2", new Document("$degreesToRadians",
                                        new Document("$arrayElemAt", List.of("$p2.coordinates", 0))))
                        ).append("in",
                                new Document("$multiply", List.of(
                                        6371,
                                        new Document("$acos",
                                                new Document("$add", List.of(
                                                        new Document("$multiply", List.of(
                                                                new Document("$sin", "$$lat1"),
                                                                new Document("$sin", "$$lat2")
                                                        )),
                                                        new Document("$multiply", List.of(
                                                                new Document("$cos", "$$lat1"),
                                                                new Document("$cos", "$$lat2"),
                                                                new Document("$cos", new Document("$subtract", List.of("$$lon2", "$$lon1")))
                                                        ))
                                                ))
                                        )
                                ))
                        ))
                ))
        );

        // Ejecutamos el aggregate "a mano" (no necesitamos una colección real, usamos aggregate: 1)
        Document command = new Document("aggregate", 1)
                .append("pipeline", pipeline)
                .append("cursor", new Document());

        Document result = mongoTemplate.getDb().runCommand(command);

        List<Document> firstBatch = ((Document) result.get("cursor"))
                .getList("firstBatch", Document.class);

        if (firstBatch.isEmpty()) {
            return 0.0;
        }

        Number distance = (Number) firstBatch.getFirst().get("distanceKm");
        return distance.doubleValue();
    }

    @Override
    public List<DatoSensorNormalizado> findAllNormalizados() {
        return mongoTemplate.findAll(DatoSensorNormalizado.class, "epersgeist_normalized");
    }
}
