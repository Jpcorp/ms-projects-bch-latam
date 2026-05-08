package cl.bch.cloud.ms.fglosas.mantenedor.repositories;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.StatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StatusRepository extends JpaRepository<StatusEntity, Long>,
        JpaSpecificationExecutor<StatusEntity> {

    @Query("SELECT g FROM StatusEntity g WHERE g.name = :name")
    StatusEntity findByNameStatus(@Param("name") String name);

    @Query("SELECT g FROM StatusEntity g WHERE g.name = :name")
    StatusEntity findByName(@Param("name") String name);

}
