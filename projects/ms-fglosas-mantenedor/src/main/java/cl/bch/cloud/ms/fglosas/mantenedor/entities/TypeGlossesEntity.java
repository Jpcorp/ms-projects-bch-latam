package cl.bch.cloud.ms.fglosas.mantenedor.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "TIPO_GLOSAS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TypeGlossesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_glosa_seq")
    @SequenceGenerator(name = "tipo_glosa_seq", sequenceName = "TIPO_GLOSA_SEQ", allocationSize = 1)
    @Column(name = "TIPO_GLOSA_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

}
