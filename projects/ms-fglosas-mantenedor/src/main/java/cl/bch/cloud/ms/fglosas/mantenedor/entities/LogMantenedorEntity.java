package cl.bch.cloud.ms.fglosas.mantenedor.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "LOGS_MANTENEDOR")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LogMantenedorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "logmant_seq")
    @SequenceGenerator(name = "logmant_seq", sequenceName = "LOGMANT_SEQ", allocationSize = 1)
    @Column(name = "MANTENEDOR_ID")
    private Long id;

    @Column(name = "IDENTITY")
    private String identity;

    @Column(name = "IDENTITY_ID")
    private Long identityId;

    @Column(name = "ACTION")
    private String action;
    @Lob
    @Column(name = "VALUE_OLD")
    private String valueOld;

    @Lob
    @Column(name = "VALUE_NEW")
    private String valueNew;

    @Column(name = "USER_RESPONSABILITY")
    private String userResposability;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
}
