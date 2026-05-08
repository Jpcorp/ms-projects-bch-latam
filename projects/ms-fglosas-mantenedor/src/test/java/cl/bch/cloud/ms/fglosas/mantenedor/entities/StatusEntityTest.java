package cl.bch.cloud.ms.fglosas.mantenedor.entities;

import cl.bch.cloud.ms.fglosas.mantenedor.utils.StaticGlossesUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StatusEntityTest {

    static Long id;
    static String name;
    static LocalDateTime createAt;

    StatusEntity entity;

    @BeforeEach
    void setUp() {
        id = 1L;
        name = StaticGlossesUtils.POR_SER_APPROVADO;
        createAt = LocalDateTime.now();

        entity = new StatusEntity(id, name, createAt);
    }

    @Test
    public void shouldGetId() throws Exception {
        assertThat(entity.getId()).isEqualTo(id);
    }

    @Test
    public void shouldGetName() throws Exception {
        assertThat(entity.getName()).isEqualTo(name);
    }

    @Test
    public void shouldGetCreateAt() throws Exception {
        assertThat(entity.getCreateAt()).isEqualTo(createAt);
    }

    @Test
    public void shouldSetId() throws Exception {
        entity.setId(id + 1);
        assertThat(entity.getId()).isEqualTo(id + 1);
    }

    @Test
    public void shouldSetName() throws Exception {
        entity.setName( "new" + entity.getName());
        assertThat(entity.getName()).isEqualTo("new" + name);
    }

    @Test
    public void shouldSetCreateAt() throws Exception {
        entity.setCreateAt(entity.getCreateAt().plusDays(1) );
        assertThat(entity.getCreateAt()).isEqualTo(createAt.plusDays(1));
    }

}