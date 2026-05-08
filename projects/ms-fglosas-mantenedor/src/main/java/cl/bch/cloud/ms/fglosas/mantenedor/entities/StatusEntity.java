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
@Table(name = "ESTADOS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "status_seq")
    @SequenceGenerator(name = "status_seq", sequenceName = "STATUS_SEQ", allocationSize = 1)
    @Column(name = "ESTADO_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

}
