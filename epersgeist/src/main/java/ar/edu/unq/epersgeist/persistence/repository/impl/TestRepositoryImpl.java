package ar.edu.unq.epersgeist.persistence.repository.impl;

import ar.edu.unq.epersgeist.persistence.mongo.interfaces.UbicacionMongoDAO;
import ar.edu.unq.epersgeist.persistence.neo.UbicacionNeo4JDAO;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.TestRepository;
import ar.edu.unq.epersgeist.persistence.sql.interfaces.TestSQLDAO;
import org.springframework.stereotype.Component;

@Component
public class TestRepositoryImpl implements TestRepository {

    private final TestSQLDAO testSQLDAO;
    private final UbicacionNeo4JDAO ubicacionNeo4JDAO;
    private final UbicacionMongoDAO ubicacionMongoDAO;

    public TestRepositoryImpl(TestSQLDAO testSQLDAO, UbicacionNeo4JDAO ubicacionNeo4JDAO, UbicacionMongoDAO ubicacionMongoDAO) {
        this.testSQLDAO = testSQLDAO;
        this.ubicacionNeo4JDAO = ubicacionNeo4JDAO;
        this.ubicacionMongoDAO = ubicacionMongoDAO;
    }

    @Override
    public void clearAll() {
        this.testSQLDAO.clearAll();
        this.ubicacionNeo4JDAO.deleteAll();
        this.ubicacionMongoDAO.deleteAll();
    }
}