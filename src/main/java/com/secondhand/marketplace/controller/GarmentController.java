package com.secondhand.marketplace.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.secondhand.marketplace.dto.GarmentDto;
import com.secondhand.marketplace.dto.View;
import com.secondhand.marketplace.entity.Garment;
import com.secondhand.marketplace.entity.User;
import com.secondhand.marketplace.exceptions.GarmentNotFoundException;
import com.secondhand.marketplace.exceptions.UnauthorizedActionException;
import com.secondhand.marketplace.service.GarmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clothes")
@Slf4j
public class GarmentController {

    private final GarmentService garmentService;
    private final ModelMapper modelMapper;

    public GarmentController(GarmentService garmentService, ModelMapper modelMapper) {
        this.garmentService = garmentService;
        this.modelMapper = modelMapper;
    }
    @Operation(summary = "Get all clothes or filter by type")
    @GetMapping
    @JsonView(View.Detailed.class)
    public ResponseEntity<List<GarmentDto>> getAllClothes(@RequestParam(required = false) String type) {
        List<Garment> garments = (type != null) ? garmentService.getAllGarments(type) : garmentService.getAllGarments(null);
        List<GarmentDto> garmentDtos = garments.stream()
                .map(garment -> modelMapper.map(garment, GarmentDto.class))
                .collect(Collectors.toList());

        log.info("Fetched {} garments", garmentDtos.size());
        return ResponseEntity.ok(garmentDtos);
    }
    @Operation(summary = "Get a clothing item by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clothing item retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Clothing item not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GarmentDto> getClothingItem(@PathVariable Long id) throws GarmentNotFoundException {
        Garment garment = garmentService.getGarmentById(id);
        GarmentDto garmentDto = modelMapper.map(garment, GarmentDto.class);

        log.info("Fetched garment with id {}: {}", id, garmentDto);
        return ResponseEntity.ok(garmentDto);
    }
    @Operation(summary = "Publish a new garment")
    @PostMapping("/add")
    @JsonView(View.Summary.class)
    public ResponseEntity<GarmentDto> publishGarment(@RequestBody GarmentDto garmentDTO, @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            log.warn("Unauthorized access attempt to publish garment");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Garment garment = modelMapper.map(garmentDTO, Garment.class);
        garment.setPublisher(currentUser);

        Garment savedGarment = garmentService.publishGarment(garment);
        GarmentDto savedGarmentDto = modelMapper.map(savedGarment, GarmentDto.class);

        log.info("Garment published successfully by user {}: {}", currentUser.getUsername(), savedGarmentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGarmentDto);
    }
    @Operation(summary = "Update a garment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Garment updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Garment not found")
    })
    @PutMapping("/{id}")
    @JsonView(View.Summary.class)
    public ResponseEntity<GarmentDto> updateGarment(@PathVariable Long id, @RequestBody GarmentDto garmentDTO, @AuthenticationPrincipal User currentUser) throws GarmentNotFoundException {
        if (currentUser == null) {
            log.warn("Unauthorized access attempt to update garment with id {}", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Garment garment = modelMapper.map(garmentDTO, Garment.class);
        garment.setPublisher(currentUser);

        Garment updatedGarment = garmentService.updateGarment(id, garment, currentUser);
        GarmentDto updatedGarmentDto = modelMapper.map(updatedGarment, GarmentDto.class);

        log.info("Garment with id {} updated successfully by user {}", id, currentUser.getUsername());
        return ResponseEntity.ok(updatedGarmentDto);
    }
    @Operation(summary = "Unpublish (delete) a garment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Garment unpublished successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Garment not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> unpublishGarment(@PathVariable Long id, @AuthenticationPrincipal User currentUser) throws GarmentNotFoundException {
        if (currentUser == null) {
            log.warn("Unauthorized access attempt to delete garment with id {}", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        garmentService.unpublishGarment(id, currentUser);
        log.info("Garment with id {} unpublished by user {}", id, currentUser.getUsername());
        return ResponseEntity.ok(String.format("Garment with id %s was deleted", id));
    }

    @ExceptionHandler(GarmentNotFoundException.class)
    public ResponseEntity<String> handleGarmentNotFoundException(GarmentNotFoundException ex) {
        log.error("Garment not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<String> handleUnauthorizedActionException(UnauthorizedActionException ex) {
        log.error("Unauthorized action: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
    }
}
