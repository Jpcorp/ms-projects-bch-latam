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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "PLANTILLAS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TemplatesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plla_seq")
    @SequenceGenerator(name = "plla_seq", sequenceName = "PLLA_SEQ", allocationSize = 1)
    @Column(name = "PLANTILLA_ID")
    private Long id; //v

    @Column(name = "EXTENSION")
    private String extension;

    @Column(name = "PATH")
    @Lob
    private String name; //v

    @Column(name = "FOLDER")
    @Lob
    private String folder; //v

    @Column(name = "MARCA")
    private String marca;

    @Column(name = "TRANSACCION")
    private String transaccion;

    @ManyToOne()
    @JoinColumn(name = "TIPO_NTC_ID")
    private TmpNotificationEntity config; // v

    @ManyToOne()
    @JoinColumn(name = "ESTADO_ID")
    private StatusEntity estado; // v

    @Column(name = "ESTADO_DOCTO")
    private String estadoDocto;

    @Column(name = "COMPROBANTE")
    private int vaucher;

    @Column(name = "ATTR")
    @Lob
    @NotNull
    private String attr;

    @Column(name = "USER_CREATED")
    private String userCreated;

    @Column(name = "USER_UPDATED")
    private String userUpdated;

    @Column(name = "CREATE_AT")
    private LocalDate createAt; //v

    @Column(name = "UPDATE_AT")
    private LocalDate updateAt; //v

}
