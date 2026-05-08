package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.MarcaEntity;

public record MarcaDTO(
        String id,
        String nombre
) {
    public static MarcaDTO fromEntity(MarcaEntity entity) {
        return new MarcaDTO(entity.getId(), entity.getNombre());
    }
}