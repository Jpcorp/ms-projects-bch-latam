package cl.bch.cloud.ms.fglosas.mantenedor.repositories;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.SignaturesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SignaturesRepository extends JpaRepository<SignaturesEntity, Long> {

    @Query("SELECT g FROM SignaturesEntity g WHERE g.estadoId.id = :id")
    List<SignaturesEntity> findAllWithStatus(@Param("id") long id);

}
