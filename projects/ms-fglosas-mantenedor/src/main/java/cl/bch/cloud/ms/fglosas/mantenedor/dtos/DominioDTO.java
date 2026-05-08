package cl.bch.cloud.ms.fglosas.mantenedor.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
public class DominioDTO {
    private String nombre;
    private List<CanalDTO> canales;

    public DominioDTO(String nombre, List<CanalDTO> canales) {
        this.nombre = nombre;
        this.canales = canales;
    }
}

