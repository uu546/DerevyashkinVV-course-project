package project.warehouse.foundation.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import project.warehouse.entity.Movement;
import project.warehouse.foundation.interfaces.IMovementRepository;

@Repository
public class MovementRepository implements IMovementRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Movement save(Movement movement) {
        if (movement.getId() == null) {
            entityManager.persist(movement);
            return movement;
        } else {
            return entityManager.merge(movement);
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        Movement movement = entityManager.find(Movement.class, id);
        if (movement != null) {
            entityManager.remove(movement);
        }
    }
}