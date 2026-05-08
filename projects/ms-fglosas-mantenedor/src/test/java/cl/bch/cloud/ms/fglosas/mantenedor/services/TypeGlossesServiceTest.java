package cl.bch.cloud.ms.fglosas.mantenedor.services;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.TypeGlossesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.TypeGlossesRepository;
import cl.bch.cloud.ms.fglosas.mantenedor.utils.StaticGlossesUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypeGlossesServiceTest {
    private TypeGlossesRepository repository;
    private TypeGlossesService service;

    @BeforeEach
    void setUp() {
        repository = mock(TypeGlossesRepository.class);
        service = new TypeGlossesService(repository);
    }

    @Test
    void testFindByName_success() {
        String name = StaticGlossesUtils.IDENTITY_GLOSSES;
        TypeGlossesEntity entity = new TypeGlossesEntity();
        entity.setName(name);

        when(repository.findByName(name)).thenReturn(entity);

        TypeGlossesEntity result = service.findByName(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
    }

    @Test
    void testFindByName_notFound() {
        String name = "UNKNOWN";
        when(repository.findByName(name)).thenReturn(null);

        TypeGlossesEntity result = service.findByName(name);

        assertEquals(null, result);
    }

}