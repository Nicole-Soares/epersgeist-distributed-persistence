package ar.edu.unq.epersgeist.service.impl;

import ar.edu.unq.epersgeist.persistence.repository.interfaces.TestRepository;
import ar.edu.unq.epersgeist.service.interfaces.TestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;

    public TestServiceImpl(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clearAll() {
        testRepository.clearAll();
    }
}
