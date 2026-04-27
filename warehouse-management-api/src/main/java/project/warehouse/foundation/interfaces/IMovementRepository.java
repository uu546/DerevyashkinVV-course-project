package project.warehouse.foundation.interfaces;

import project.warehouse.entity.Movement;

public interface IMovementRepository {
    Movement save(Movement movement);
    void deleteById(Integer id);
}