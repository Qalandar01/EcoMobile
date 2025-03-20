package com.example.ecomobile.controller;

import com.example.ecomobile.dto.LocationRequest;
import com.example.ecomobile.entity.Location;
import com.example.ecomobile.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<Location> addLocation(
            @RequestBody LocationRequest request,
            @RequestHeader("userId") Integer userId
    ) {
        return ResponseEntity.ok(locationService.addLocation(request, userId));
    }

    @GetMapping
    public ResponseEntity<List<Location>> getLocations(@RequestHeader("userId") Integer userId) {
        System.out.println(locationService.getLocationsByUserId(userId));
        return ResponseEntity.ok(locationService.getLocationsByUserId(userId));
    }

    @PutMapping("/{locationId}")
    public ResponseEntity<Location> updateLocation(
            @PathVariable Integer locationId,
            @RequestBody LocationRequest request,
            @RequestHeader("userId") Integer userId
    ) {
        return ResponseEntity.ok(locationService.updateLocation(locationId, userId, request));
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable Integer locationId,
            @RequestHeader("userId") Integer userId
    ) {
        locationService.deleteLocation(locationId, userId);
        return ResponseEntity.noContent().build();
    }
}
