package cl.bch.cloud.ms.fglosas.mantenedor.repositories;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.CampoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CampoRepository extends JpaRepository<CampoEntity, String> {

    @Query("""
        SELECT c FROM CampoEntity c
        JOIN TransaccionCampoEntity tc ON c.nombre = tc.campo.nombre
        JOIN TransaccionEntity t ON tc.transaccion.id = t.id
        WHERE t.id = :idTransaccion AND t.marca.id = :idMarca
    """)
    List<CampoEntity> findCamposByTemplateIdAndMark(@Param("idTransaccion") String idTransaccion,
                                                    @Param("idMarca") String idMarca);
}
