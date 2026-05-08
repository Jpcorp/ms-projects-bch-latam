package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CanalDominioNotificacionesDTO {
    private String canalName;
    private String dominioName;
    private List<NotificacionDTO> notificaciones;

    public CanalDominioNotificacionesDTO(String canalName, String dominioName, List<NotificacionDTO> notificaciones) {
        this.canalName = canalName;
        this.dominioName = dominioName;
        this.notificaciones = notificaciones;
    }
}
