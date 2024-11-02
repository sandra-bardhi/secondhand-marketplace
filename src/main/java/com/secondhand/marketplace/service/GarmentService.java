package com.secondhand.marketplace.service;

import com.secondhand.marketplace.entity.Garment;
import com.secondhand.marketplace.entity.User;
import com.secondhand.marketplace.exceptions.GarmentNotFoundException;
import com.secondhand.marketplace.exceptions.UnauthorizedActionException;
import com.secondhand.marketplace.repository.GarmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GarmentService {

    private final GarmentRepository garmentRepository;

    // Constructor-based dependency injection
    public GarmentService(GarmentRepository garmentRepository) {
        this.garmentRepository = garmentRepository;
    }

    public List<Garment> getAllGarments(String type) {
        if (type != null && !type.isEmpty()) {
            log.info("Fetching garments by type: {}", type);
            return garmentRepository.findByType(type);
        }
        log.info("Fetching all garments");
        return garmentRepository.findAll();
    }

    public Garment getGarmentById(Long id) throws GarmentNotFoundException {
        return garmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Garment not found with ID: {}", id);
                    return new GarmentNotFoundException("Garment not found with ID: " + id);
                });
    }

    public Garment publishGarment(Garment garment) {
        log.info("Publishing garment: {}", garment);
        return garmentRepository.save(garment);
    }

    public Garment updateGarment(Long id, Garment updatedGarment, User currentUser) throws GarmentNotFoundException {
        Garment existingGarment = garmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Garment not found with ID: {}", id);
                    return new GarmentNotFoundException("Garment not found with ID: " + id);
                });

        if (!existingGarment.getPublisher().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized update attempt by user {} for garment ID: {}", currentUser.getId(), id);
            throw new UnauthorizedActionException("Unauthorized action: You do not own this garment.");
        }

        existingGarment.setType(updatedGarment.getType());
        existingGarment.setDescription(updatedGarment.getDescription());
        existingGarment.setSize(updatedGarment.getSize());
        existingGarment.setPrice(updatedGarment.getPrice());

        log.info("Updating garment ID: {}", existingGarment.getId());
        return garmentRepository.save(existingGarment);
    }

    public void unpublishGarment(Long id, User currentUser) throws GarmentNotFoundException {
        Garment garment = garmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Garment not found with ID: {}", id);
                    return new GarmentNotFoundException("Garment not found with ID: " + id);
                });

        if (!garment.getPublisher().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized delete attempt by user {} for garment ID: {}", currentUser.getId(), id);
            throw new UnauthorizedActionException("Unauthorized action: You do not own this garment.");
        }

        log.info("Unpublishing garment ID: {}", garment.getId());
        garmentRepository.delete(garment);
    }
}
