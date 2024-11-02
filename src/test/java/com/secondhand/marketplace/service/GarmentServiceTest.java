package com.secondhand.marketplace.service;

import com.secondhand.marketplace.entity.Garment;
import com.secondhand.marketplace.entity.User;
import com.secondhand.marketplace.exceptions.GarmentNotFoundException;
import com.secondhand.marketplace.exceptions.UnauthorizedActionException;
import com.secondhand.marketplace.repository.GarmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GarmentServiceTest {

    @InjectMocks
    private GarmentService garmentService;

    @Mock
    private GarmentRepository garmentRepository;

    private Garment garment;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        garment = new Garment();
        garment.setId(1L);
        garment.setType("Shirt");
        garment.setPublisher(user);
    }

    @Test
    public void testGetAllGarments_WithType() {
        when(garmentRepository.findByType("Shirt")).thenReturn(Collections.singletonList(garment));

        List<Garment> garments = garmentService.getAllGarments("Shirt");

        assertEquals(1, garments.size());
        assertEquals("Shirt", garments.get(0).getType());
        verify(garmentRepository, times(1)).findByType("Shirt");
    }

    @Test
    public void testGetAllGarments_WithoutType() {
        when(garmentRepository.findAll()).thenReturn(Collections.singletonList(garment));

        List<Garment> garments = garmentService.getAllGarments(null);

        assertEquals(1, garments.size());
        verify(garmentRepository, times(1)).findAll();
    }

    @Test
    public void testGetGarmentById_Success() throws GarmentNotFoundException {
        when(garmentRepository.findById(1L)).thenReturn(Optional.of(garment));

        Garment foundGarment = garmentService.getGarmentById(1L);

        assertEquals(garment.getId(), foundGarment.getId());
        verify(garmentRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetGarmentById_NotFound() {
        when(garmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GarmentNotFoundException.class, () -> garmentService.getGarmentById(1L));
        verify(garmentRepository, times(1)).findById(1L);
    }

    @Test
    public void testPublishGarment() {
        when(garmentRepository.save(garment)).thenReturn(garment);

        Garment savedGarment = garmentService.publishGarment(garment);

        assertEquals(garment.getId(), savedGarment.getId());
        verify(garmentRepository, times(1)).save(garment);
    }

    @Test
    public void testUpdateGarment_Success() throws GarmentNotFoundException {
        Garment updatedGarment = new Garment();
        updatedGarment.setType("Updated Shirt");
        updatedGarment.setDescription("Updated description");
        updatedGarment.setSize("M");
        updatedGarment.setPrice(30.0);
        updatedGarment.setPublisher(user);

        when(garmentRepository.findById(1L)).thenReturn(Optional.of(garment));
        when(garmentRepository.save(garment)).thenReturn(garment);

        Garment result = garmentService.updateGarment(1L, updatedGarment, user);

        assertEquals("Updated Shirt", result.getType());
        verify(garmentRepository, times(1)).findById(1L);
        verify(garmentRepository, times(1)).save(garment);
    }

    @Test
    public void testUpdateGarment_Unauthorized() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otherUser");

        Garment updatedGarment = new Garment();
        updatedGarment.setPublisher(otherUser);

        when(garmentRepository.findById(1L)).thenReturn(Optional.of(garment));

        assertThrows(UnauthorizedActionException.class, () -> garmentService.updateGarment(1L, updatedGarment, otherUser));
        verify(garmentRepository, times(1)).findById(1L);
    }

    @Test
    public void testUnpublishGarment_Success() throws GarmentNotFoundException {
        when(garmentRepository.findById(1L)).thenReturn(Optional.of(garment));

        garmentService.unpublishGarment(1L, user);

        verify(garmentRepository, times(1)).findById(1L);
        verify(garmentRepository, times(1)).delete(garment);
    }

    @Test
    public void testUnpublishGarment_Unauthorized() {
        User otherUser = new User();
        otherUser.setId(2L);

        when(garmentRepository.findById(1L)).thenReturn(Optional.of(garment));

        assertThrows(UnauthorizedActionException.class, () -> garmentService.unpublishGarment(1L, otherUser));
        verify(garmentRepository, times(1)).findById(1L);
    }

    @Test
    public void testUnpublishGarment_NotFound() {
        when(garmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GarmentNotFoundException.class, () -> garmentService.unpublishGarment(1L, user));
        verify(garmentRepository, times(1)).findById(1L);
    }
}
