package cl.bch.cloud.ms.fglosas.mantenedor.services;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.TypeGlossesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.TypeGlossesRepository;
import org.springframework.stereotype.Service;

@Service
public class TypeGlossesService {

    private final TypeGlossesRepository repository;

    public TypeGlossesService(TypeGlossesRepository repository) {
        this.repository = repository;
    }

    public TypeGlossesEntity findByName(String name) {
        return repository.findByName(name);
    }

}
