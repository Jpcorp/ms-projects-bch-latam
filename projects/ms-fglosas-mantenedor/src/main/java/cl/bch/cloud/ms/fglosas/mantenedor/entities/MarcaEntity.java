package cl.bch.cloud.ms.fglosas.mantenedor.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "marca")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarcaEntity {

    @Id
    @Column(name = "id_marca", length = 10)
    private String id; // BCH o BED

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "marca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransaccionEntity> transacciones;
}