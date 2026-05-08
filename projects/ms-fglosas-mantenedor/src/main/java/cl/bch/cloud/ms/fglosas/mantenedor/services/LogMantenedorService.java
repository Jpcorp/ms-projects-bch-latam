package cl.bch.cloud.ms.fglosas.mantenedor.services;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.LoggerMaintainerDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.LogMantenedorEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.LogMantenedorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LogMantenedorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogMantenedorService.class);

    private final LogMantenedorRepository repository;

    public LogMantenedorService(LogMantenedorRepository repository) {
        this.repository = repository;
    }

    public Optional<LoggerMaintainerDTO> getLoggerById(long id) {
        try {
            return repository.findById(id)
                    .map(LoggerMaintainerDTO::fromEntity)
                    .or(() -> Optional.of( new LoggerMaintainerDTO(
                            null, "", null, "", "",
                            "", null, null
                    )));
        } catch (Exception e) {
            LOGGER.error("Error searching logger Maintainer by id: {}", id, e);
            return Optional.of(new LoggerMaintainerDTO(null,
                    "", null, "",
                    "", "", null, null
            ));
        }
    }

    public List<LoggerMaintainerDTO> getAllLoggers() {
        return repository.findAll()
                .stream()
                .sorted(Comparator.comparing(LogMantenedorEntity::getCreatedAt).reversed())
                .map(LoggerMaintainerDTO::fromEntity)
                .toList();

    }

    public Optional<LoggerMaintainerDTO> create(LoggerMaintainerDTO entity) {
        LogMantenedorEntity created = repository.save(entity.toEntity());
        return Optional.of(LoggerMaintainerDTO.fromEntity(created));
    }

    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<List<LoggerMaintainerDTO>> searchWithDate(LocalDate from, LocalDate until) {
        //valido que las fechas desde hoy hacias 30 atras
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);

        if (from.isBefore(thirtyDaysAgo) || until.isAfter(today) || from.isAfter(until)) {
            return Optional.empty();
        }
        //obtengo los registros
        List<LoggerMaintainerDTO> entities = getAllLoggers();
        // Filtrar los registros dentro del rango de fechas
        List<LoggerMaintainerDTO> filteredRecords = entities.stream()
                .filter(record -> {
                    LocalDate recordDate = record.createdAt().atZone(ZoneId.systemDefault()).toLocalDate();
                    return (recordDate.isEqual(from) || recordDate.isAfter(from)) &&
                            (recordDate.isEqual(until) || recordDate.isBefore(until));
                }).collect(Collectors.toList());
        //devuelvo el arreglo
        return Optional.of(filteredRecords);
    }
}
