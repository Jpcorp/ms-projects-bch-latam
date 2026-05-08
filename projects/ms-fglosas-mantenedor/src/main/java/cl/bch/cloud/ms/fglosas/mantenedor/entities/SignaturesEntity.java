package cl.bch.cloud.ms.fglosas.mantenedor.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "FIRMAS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SignaturesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "firma_seq")
    @SequenceGenerator(name = "firma_seq", sequenceName = "FIRMA_SEQ", allocationSize = 1)
    @Column(name = "FIRMA_ID")
    private Long id;

    @Column(name = "RUT", unique = true, length = 15, nullable = false)
    private String rut;

    @Column(name = "NAME", length = 40)
    private String name;

    @Column(name = "USER_CREATED")
    private String userCreated;

    @Lob
    @Column(name = "IMAGE")
    private String imagen;

    @ManyToOne()
    @JoinColumn(name = "ESTADO_ID")
    private StatusEntity estadoId;

    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

}
