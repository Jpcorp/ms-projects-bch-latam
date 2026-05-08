package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String nombre;
    private String path;
}
