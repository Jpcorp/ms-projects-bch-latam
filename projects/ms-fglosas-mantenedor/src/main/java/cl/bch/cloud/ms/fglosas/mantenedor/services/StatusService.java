package cl.bch.cloud.ms.fglosas.mantenedor.services;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.StatusEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.StatusRepository;
import org.springframework.stereotype.Service;

@Service
public class StatusService {

    private final StatusRepository statusRepository;

    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public StatusEntity getStatus(String name) {
        return statusRepository.findByNameStatus(name);
    }

    public StatusEntity findByName(String name) {
        return statusRepository.findByName(name);
    }

}
