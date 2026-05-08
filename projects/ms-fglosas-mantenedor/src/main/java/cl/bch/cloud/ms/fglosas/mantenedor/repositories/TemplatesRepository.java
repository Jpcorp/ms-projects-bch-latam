package cl.bch.cloud.ms.fglosas.mantenedor.repositories;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.TemplatesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface  TemplatesRepository extends JpaRepository<TemplatesEntity, Long>,
        JpaSpecificationExecutor<TemplatesEntity> {

    @Query("SELECT COUNT(g) > 0 FROM TemplatesEntity g WHERE g.name = :name and g.estado.id= :estadoId")
    boolean getByNameStatus(String name, Long estadoId);

    @Query("SELECT COUNT(g) > 0 FROM TemplatesEntity g WHERE g.name = :name and g.marca = :marca")
    boolean getNameMark(String name, String marca);

    @Query("SELECT COUNT(g) > 0 FROM TemplatesEntity g WHERE g.name = :name " +
            " and g.estado.id= :estadoId and g.folder = :folder")
    boolean getByNameStatus(String name, Long estadoId, String folder);

    @Query("SELECT g FROM TemplatesEntity g WHERE g.name LIKE :name AND g.estado.id = :estadoId " +
            "AND g.folder LIKE :folder")
    TemplatesEntity getEntityByNameStatusFolder(String name, Long estadoId, String folder);

    @Query("SELECT p FROM TemplatesEntity p JOIN FETCH p.config n JOIN FETCH n.canal c" +
            " WHERE p.id = :id ")
    TemplatesEntity getTemplatesByIdAndUser(Long id);

    TemplatesEntity findTopByTransaccionAndMarcaOrderByIdAsc(String trxName, String mark);
}
