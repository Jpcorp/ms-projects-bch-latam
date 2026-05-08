package cl.bch.cloud.ms.fglosas.mantenedor.repositories;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.TmpNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TmpNotificationRepository extends JpaRepository<TmpNotificationEntity, Long>,
        JpaSpecificationExecutor<TmpNotificationEntity> {

    @Query("SELECT CONCAT(d.folder, c.folder, n.path) " +
            "FROM TmpNotificationEntity n " +
            "JOIN n.canal c " +
            "JOIN c.dominio d " +
            "WHERE n.id = :tipoNtcId")
    String getFolderNotifyId(@Param("tipoNtcId") Long tipoNtcId);

    List<TmpNotificationEntity> findByCanalId(Long canalId);

    @Query("SELECT u FROM TmpNotificationEntity u " +
            "JOIN FETCH u.canal c JOIN FETCH c.dominio")
    List<TmpNotificationEntity> findAllWithChannelDomain();
}
