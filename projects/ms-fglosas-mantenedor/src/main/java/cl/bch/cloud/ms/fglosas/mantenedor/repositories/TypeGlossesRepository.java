package cl.bch.cloud.ms.fglosas.mantenedor.repositories;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.TypeGlossesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TypeGlossesRepository extends JpaRepository<TypeGlossesEntity, Long>,
        JpaSpecificationExecutor<TypeGlossesEntity> {

    @Query("SELECT g FROM TypeGlossesEntity g WHERE g.name = :name")
    TypeGlossesEntity findByName(@Param("name") String name);
}