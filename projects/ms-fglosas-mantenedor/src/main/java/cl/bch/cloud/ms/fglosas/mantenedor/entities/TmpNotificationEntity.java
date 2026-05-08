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
@Table(name = "TIPO_NOTIFICACIONES")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TmpNotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_ntc_seq")
    @SequenceGenerator(name = "tipo_ntc_seq", sequenceName = "TIPO_NTC_SEQ", allocationSize = 1)
    @Column(name = "TIPO_NTC_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PATH")
    private String path;

    @ManyToOne()
    @JoinColumn(name = "CANAL_ID")
    private TmpChannelEntity canal;

    @Column(name = "CREATE_AT")
    private LocalDate createAt;

}
