package project.warehouse.foundation.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import project.warehouse.entity.Location;
import project.warehouse.foundation.interfaces.ILocationRepository;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.TypedQuery;

@Repository
public class LocationRepository implements ILocationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Location> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(entityManager.find(Location.class, id));
    }

    @Override
    public List<Location> findAll() {
        String jpql = "SELECT l FROM Location l";
        TypedQuery<Location> query = entityManager.createQuery(jpql, Location.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public Location save(Location location) {
        if (location.getId() == null) {
            entityManager.persist(location);
            return location;
        } else {
            return entityManager.merge(location);
        }
    }
}