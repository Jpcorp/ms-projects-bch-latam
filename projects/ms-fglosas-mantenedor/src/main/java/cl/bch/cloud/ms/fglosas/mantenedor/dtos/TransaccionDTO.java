package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.TransaccionEntity;

public record TransaccionDTO(
        Long id,
        String descripcion,
        String marcaId,
        String nombre
) {
    public static TransaccionDTO fromEntity(TransaccionEntity entity) {
        return new TransaccionDTO(
                entity.getId(),
                entity.getDescripcion(),
                entity.getMarca().getId(),
                entity.getNombre()
        );
    }
}