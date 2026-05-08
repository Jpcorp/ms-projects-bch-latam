package cl.bch.cloud.ms.fglosas.mantenedor.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "APROBACIONES")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AprobacionesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aprobacion_seq")
    @SequenceGenerator(name = "aprobacion_seq", sequenceName = "APROBACION_SEQ", allocationSize = 1)
    @Column(name = "APROBACIONES_ID")
    private Long id;

    @Column(name = "IDENTITY")
    private String identity;

    @Column(name = "IDENTITY_ID")
    private Long identityId;

    @Column(name = "CHECKER")
    private String checker;

    @Column(name = "COMENTARIO")
    private String comentario;

    @Column(name = "FECHA_APROBACION")
    private LocalDateTime fechaAprobacion;

    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;
}
