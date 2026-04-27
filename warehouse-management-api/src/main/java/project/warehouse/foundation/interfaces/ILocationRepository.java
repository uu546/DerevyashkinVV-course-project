package project.warehouse.foundation.interfaces;

import project.warehouse.entity.Location;
import java.util.Optional;

public interface ILocationRepository {

    Optional<Location> findById(Integer id);
    Location save(Location location);
}