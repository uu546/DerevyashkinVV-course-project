package project.warehouse.foundation.interfaces;

import project.warehouse.entity.Location;

import java.util.List;
import java.util.Optional;

public interface ILocationRepository {

    Optional<Location> findById(Integer id);
    List<Location> findAll();
    Location save(Location location);
}