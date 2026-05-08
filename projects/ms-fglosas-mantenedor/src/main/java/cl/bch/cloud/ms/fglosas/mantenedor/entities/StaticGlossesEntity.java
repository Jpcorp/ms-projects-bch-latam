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
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Entity
@Table(name = "GLOSAS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StaticGlossesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "glosa_seq")
    @SequenceGenerator(name = "glosa_seq", sequenceName = "GLOSA_SEQ", allocationSize = 1)
    @Column(name = "GLOSA_ID")
    private Long id;

    @Column(name = "NAME")
    @NotNull
    private String name;

    @Column(name = "VALUE")
    @Lob
    @NotNull
    private String value;

    @Column(name = "USER_CREATED")
    private String userCreated; //campo

    @Column(name = "USER_UPDATED")
    private String userUpdated;

    @ManyToOne()
    @JoinColumn(name = "ESTADO_ID")
    private StatusEntity estadoId;

    @ManyToOne()
    @JoinColumn(name = "TIPO_GLOSA_ID")
    private TypeGlossesEntity tipoGlosa;

    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    @Column(name = "UPDATE_AT")
    private LocalDateTime updateAt;

    public String getNameToLowerCase() {
        return StringUtils.lowerCase(name);
    }
}

