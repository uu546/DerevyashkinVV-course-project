package project.warehouse.foundation.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import project.warehouse.entity.Location;
import project.warehouse.foundation.interfaces.ILocationRepository;
import java.util.Optional;

@Repository
public class LocationRepository implements ILocationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Location> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Location.class, id));
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