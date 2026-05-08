package cl.bch.cloud.ms.fglosas.mantenedor.repositories;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.AprobacionesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AprobacionesRepository
        extends JpaRepository<AprobacionesEntity, Long>,
        JpaSpecificationExecutor<AprobacionesEntity> {

    @Query("SELECT g FROM AprobacionesEntity g WHERE g.identity = :identity and g.identityId = :identityId " +
            "AND (g.comentario IS NULL OR g.comentario = '') AND (g.checker IS NULL OR g.checker = '') ")
    AprobacionesEntity findIdentityByIdStatus(
            @Param("identity") String identity,
            @Param("identityId") long identityId);
}
