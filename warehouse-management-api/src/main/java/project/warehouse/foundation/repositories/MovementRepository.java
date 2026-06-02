package project.warehouse.foundation.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import project.warehouse.entity.Movement;
import project.warehouse.foundation.interfaces.IMovementRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Movement> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Movement.class, id));
    }

    @Override
    public List<Movement> findAll() {
        String jpql = "SELECT m FROM Movement m ORDER BY m.movementDate DESC";
        return entityManager.createQuery(jpql, Movement.class).getResultList();
    }

    @Override
    public List<Movement> findByProductId(Integer productId) {
        String jpql = "SELECT m FROM Movement m WHERE m.product.id = :productId ORDER BY m.movementDate DESC";
        TypedQuery<Movement> query = entityManager.createQuery(jpql, Movement.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }

    @Override
    public List<Movement> findByFromLocationId(Integer locationId) {
        String jpql = "SELECT m FROM Movement m WHERE m.fromLocation.id = :locationId ORDER BY m.movementDate DESC";
        TypedQuery<Movement> query = entityManager.createQuery(jpql, Movement.class);
        query.setParameter("locationId", locationId);
        return query.getResultList();
    }

    @Override
    public List<Movement> findByToLocationId(Integer locationId) {
        String jpql = "SELECT m FROM Movement m WHERE m.toLocation.id = :locationId ORDER BY m.movementDate DESC";
        TypedQuery<Movement> query = entityManager.createQuery(jpql, Movement.class);
        query.setParameter("locationId", locationId);
        return query.getResultList();
    }

    @Override
    public List<Movement> findByMovementDateBetween(LocalDate startDate, LocalDate endDate) {
        String jpql = "SELECT m FROM Movement m WHERE m.movementDate BETWEEN :startDate AND :endDate ORDER BY m.movementDate DESC";
        TypedQuery<Movement> query = entityManager.createQuery(jpql, Movement.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
}