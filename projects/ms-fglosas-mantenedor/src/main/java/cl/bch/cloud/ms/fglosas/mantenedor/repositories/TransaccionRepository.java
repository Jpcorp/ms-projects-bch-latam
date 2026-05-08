package cl.bch.cloud.ms.fglosas.mantenedor.repositories;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.TransaccionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransaccionRepository extends JpaRepository<TransaccionEntity, Long>,
        JpaSpecificationExecutor<TransaccionEntity> {

    List<TransaccionEntity> findByMarca_Id(String idMarca);

    @Query("SELECT n FROM TransaccionEntity n WHERE n.id = :descripcion and n.marca.id = :marca")
    TransaccionEntity findByName(String descripcion, String marca);

    @Query("SELECT n FROM TransaccionEntity n WHERE n.id = :code and n.marca.id = :marca")
    TransaccionEntity findByTrxIdMark(Long code, String marca);

    @Query("SELECT n FROM TransaccionEntity n WHERE n.descripcion = :descripcion and n.marca.id = :marca")
    TransaccionEntity findByDescMark(String descripcion, String marca);
}
