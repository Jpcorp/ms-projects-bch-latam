package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CanalDTO {
    private String nombre;
    private long id;
    private List<NotificacionDTO> notificaciones;

    public CanalDTO(String nombre, List<NotificacionDTO> notificaciones, Long id) {
        this.nombre = nombre;
        this.notificaciones = notificaciones;
        this.id = id;
    }

}
