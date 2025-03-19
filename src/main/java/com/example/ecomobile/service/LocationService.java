package com.example.ecomobile.service;

import com.example.ecomobile.dto.LocationRequest;
import com.example.ecomobile.entity.Location;
import com.example.ecomobile.entity.User;
import com.example.ecomobile.exeption.ResourceNotFoundException;
import com.example.ecomobile.repo.LocationRepository;
import com.example.ecomobile.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    /**
     * Foydalanuvchi uchun yangi manzil qo'shish
     */
    public Location addLocation(LocationRequest request, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi"));

        Location location = Location.builder()
                .region(request.getRegion())
                .district(request.getDistrict())
                .street(request.getStreet())
                .home(request.getHome())
                .user(user)
                .build();

        return locationRepository.save(location);
    }

    /**
     * Foydalanuvchining barcha manzillarini olish
     */
    public List<Location> getLocationsByUserId(Integer userId) {
        return locationRepository.findByUserId(userId);
    }

    /**
     * Foydalanuvchining manzilini o'chirish
     */
    public void deleteLocation(Integer locationId, Integer userId) {
        Location location = locationRepository.findByIdAndUserId(locationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Manzil topilmadi"));
        locationRepository.delete(location);
    }

    /**
     * Foydalanuvchining manzilini yangilash
     */
    public Location updateLocation(Integer locationId, Integer userId, LocationRequest request) {
        Location location = locationRepository.findByIdAndUserId(locationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Manzil topilmadi"));

        location.setRegion(request.getRegion());
        location.setDistrict(request.getDistrict());
        location.setStreet(request.getStreet());
        location.setHome(request.getHome());

        return locationRepository.save(location);
    }
}