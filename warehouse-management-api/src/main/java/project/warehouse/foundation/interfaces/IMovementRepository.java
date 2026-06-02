package project.warehouse.foundation.interfaces;

import project.warehouse.entity.Movement;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IMovementRepository {
    Movement save(Movement movement);
    void deleteById(Integer id);

    Optional<Movement> findById(Integer id);

    List<Movement> findAll();

    List<Movement> findByProductId(Integer productId);

    List<Movement> findByFromLocationId(Integer locationId);

    List<Movement> findByToLocationId(Integer locationId);

    List<Movement> findByMovementDateBetween(LocalDate startDate, LocalDate endDate);
}