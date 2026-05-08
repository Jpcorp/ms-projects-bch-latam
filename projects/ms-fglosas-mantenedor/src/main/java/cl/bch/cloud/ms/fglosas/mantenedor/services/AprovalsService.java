package cl.bch.cloud.ms.fglosas.mantenedor.services;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.AprovalsDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.AprobacionesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.AprobacionesRepository;
import cl.bch.cloud.ms.fglosas.mantenedor.utils.StaticGlossesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AprovalsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AprovalsService.class);

    private final AprobacionesRepository repository;
    public AprovalsService(AprobacionesRepository aprobacionesRepository) {
        this.repository = aprobacionesRepository;
    }

    public Optional<AprovalsDTO> getApprovalsById(long id) {
        try {
            return repository.findById(id)
                    .map(AprovalsDTO::fromEntity)
                    .or(() -> Optional.of( new AprovalsDTO(
                            null, "", null, "", "",
                            null, null
                    )));
        } catch (Exception e) {
            LOGGER.error("Error retrieving approvals by ID: {}", id, e);
            return Optional.of(new AprovalsDTO(null,
                    "", null, "",
                    "", null, null
            ));
        }
    }

    public List<AprovalsDTO> getAllAprovals() {
        return repository.findAll()
                .stream()
                .map(AprovalsDTO::fromEntity)
                .toList();
    }

    public Optional<AprovalsDTO> create(AprobacionesEntity entity) {
        if (entity.getFechaAprobacion() == null) {
            entity.setFechaAprobacion(LocalDateTime.now());
        }

        if (entity.getCreateAt() == null) {
            entity.setCreateAt(LocalDateTime.now());
        }
        AprobacionesEntity created = repository.save(entity);
        return Optional.of(AprovalsDTO.fromEntity(created));
    }

    public Optional<AprovalsDTO> update(Long id, AprobacionesEntity updated) {
        Optional<AprobacionesEntity> registry = repository.findById(id);
        if (registry.isEmpty()) {
            return Optional.empty();
        }

        StaticGlossesUtils.updateAproval(registry.get(), updated);
        repository.save(registry.get());
        return Optional.of(AprovalsDTO.fromEntity(updated));
    }

    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public AprobacionesEntity findByIdentityWithStatus(
            String identityGlosses, Long id) {
        return repository.findIdentityByIdStatus(identityGlosses, id);
    }
}
