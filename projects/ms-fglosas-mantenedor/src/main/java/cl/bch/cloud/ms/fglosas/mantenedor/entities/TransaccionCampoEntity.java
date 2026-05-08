package cl.bch.cloud.ms.fglosas.mantenedor.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaccion_campo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionCampoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaccion_campo_seq")
    @SequenceGenerator(name = "transaccion_campo_seq", sequenceName = "SEQ_TRANSACCION_CAMPO", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_transaccion", nullable = false)
    private TransaccionEntity transaccion;

    @ManyToOne
    @JoinColumn(name = "nombre_campo", nullable = false)
    private CampoEntity campo;
}
