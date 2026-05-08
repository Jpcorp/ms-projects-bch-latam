package cl.bch.cloud.ms.fglosas.mantenedor.entities;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "CANALES")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TmpChannelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "canal_seq")
    @SequenceGenerator(name = "canal_seq", sequenceName = "CANAL_SEQ", allocationSize = 1)
    @Column(name = "CANAL_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "FOLDER")
    private String folder;

    @ManyToOne()
    @JoinColumn(name = "DOMINIO_ID")
    private TmpDomainsEntity dominio;

    @Column(name = "CREATE_AT")
    private LocalDate createAt;

}
