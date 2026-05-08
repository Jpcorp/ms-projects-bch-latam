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

import java.time.LocalDate;

@Entity
@Table(name = "DOMINIOS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TmpDomainsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dominios_seq")
    @SequenceGenerator(name = "dominios_seq", sequenceName = "DOMINIOS_SEQ", allocationSize = 1)
    @Column(name = "DOMINIO_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "FOLDER", unique = true, nullable = false)
    private String folder;

    @Column(name = "CREATE_AT")
    private LocalDate createAt;

}
