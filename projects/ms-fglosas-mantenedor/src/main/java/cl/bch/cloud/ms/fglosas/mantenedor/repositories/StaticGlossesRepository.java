package cl.bch.cloud.ms.fglosas.mantenedor.repositories;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.StaticGlossesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StaticGlossesRepository
        extends JpaRepository<StaticGlossesEntity, Long>,
        JpaSpecificationExecutor<StaticGlossesEntity>  {

    @Query("SELECT g FROM StaticGlossesEntity g WHERE TRIM(LOWER(g.name)) = :name and g.estadoId.id = :approved")
    List<StaticGlossesEntity> findByName(@Param("name") String name, long approved);

    @Query("SELECT COUNT(g) > 0 FROM StaticGlossesEntity g WHERE g.name = :name and g.estadoId.id= :toBeApproved")
    boolean existsByName(@Param("name") String name, long toBeApproved);

    @Query("SELECT g FROM StaticGlossesEntity g WHERE g.name = :name and g.estadoId.id = :approved")
    StaticGlossesEntity findByNameCopied(@Param("name") String name, long approved);

}
