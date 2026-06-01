package project.warehouse.control.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.warehouse.control.dto.LocationDto;
import project.warehouse.entity.Location;
import project.warehouse.foundation.interfaces.ILocationRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final ILocationRepository locationRepository;

    public LocationController(ILocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @GetMapping
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        List<Location> locations = locationRepository.findAll();

        List<LocationDto> dtos = locations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDto> getLocationById(@PathVariable Integer id) {
        return locationRepository.findById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private LocationDto toDto(Location location) {
        return new LocationDto(
                location.getId(),
                location.getName(),
                location.getFullName(),
                location.getWarehouse().getId(),
                location.getWarehouse().getName(),
                location.getType().getId(),
                location.getType().getTitle(),
                location.getTemperature() != null ? location.getTemperature().getId() : null,
                location.getTemperature() != null ? location.getTemperature().getTitle() : null
        );
    }
}