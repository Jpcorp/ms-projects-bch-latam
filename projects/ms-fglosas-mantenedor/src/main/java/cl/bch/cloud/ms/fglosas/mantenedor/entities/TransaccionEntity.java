package cl.bch.cloud.ms.fglosas.mantenedor.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "transaccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaccion_seq")
    @SequenceGenerator(name = "transaccion_seq", sequenceName = "SEQ_TRANSACCION", allocationSize = 1)
    @Column(name = "id_transaccion")
    private Long id;

    @Column(name = "descripcion", length = 100, nullable = false)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_marca", nullable = false)
    private MarcaEntity marca;

    @OneToMany(mappedBy = "transaccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransaccionCampoEntity> camposValidos;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;
}

