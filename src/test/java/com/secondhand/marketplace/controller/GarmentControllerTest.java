package com.secondhand.marketplace.controller;

import com.secondhand.marketplace.dto.GarmentDto;
import com.secondhand.marketplace.entity.Garment;
import com.secondhand.marketplace.entity.User;
import com.secondhand.marketplace.exceptions.GarmentNotFoundException;
import com.secondhand.marketplace.service.GarmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GarmentControllerTest {

    @InjectMocks
    private GarmentController garmentController;

    @Mock
    private GarmentService garmentService;

    @Mock
    private ModelMapper modelMapper;



    @Test
    public void testGetAllClothes_NoType_ReturnsGarments() {
        Garment garment = new Garment();
        GarmentDto garmentDto = new GarmentDto();

        when(garmentService.getAllGarments(null)).thenReturn(Collections.singletonList(garment));
        when(modelMapper.map(garment, GarmentDto.class)).thenReturn(garmentDto);

        ResponseEntity<List<GarmentDto>> response = garmentController.getAllClothes(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(garmentService, times(1)).getAllGarments(null);
    }

    @Test
    public void testGetClothingItem_ValidId_ReturnsGarment() throws GarmentNotFoundException {
        Long garmentId = 1L;
        Garment garment = new Garment();
        GarmentDto garmentDto = new GarmentDto();

        when(garmentService.getGarmentById(garmentId)).thenReturn(garment);
        when(modelMapper.map(garment, GarmentDto.class)).thenReturn(garmentDto);

        ResponseEntity<GarmentDto> response = garmentController.getClothingItem(garmentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(garmentDto, response.getBody());
        verify(garmentService, times(1)).getGarmentById(garmentId);
    }

    @Test
    public void testPublishGarment_AuthenticatedUser_Success() {
        User currentUser = new User();
        currentUser.setUsername("testUser");

        GarmentDto garmentDto = new GarmentDto();
        Garment garment = new Garment();
        Garment savedGarment = new Garment();
        GarmentDto savedGarmentDto = new GarmentDto();

        when(modelMapper.map(garmentDto, Garment.class)).thenReturn(garment);
        when(garmentService.publishGarment(garment)).thenReturn(savedGarment);
        when(modelMapper.map(savedGarment, GarmentDto.class)).thenReturn(savedGarmentDto);

        ResponseEntity<GarmentDto> response = garmentController.publishGarment(garmentDto, currentUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedGarmentDto, response.getBody());
        verify(garmentService, times(1)).publishGarment(garment);
    }

    @Test
    public void testPublishGarment_NoAuthenticatedUser_Unauthorized() {
        GarmentDto garmentDto = new GarmentDto();

        ResponseEntity<GarmentDto> response = garmentController.publishGarment(garmentDto, null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testUpdateGarment_AuthenticatedUser_Success() throws GarmentNotFoundException {
        Long garmentId = 1L;
        User currentUser = new User();
        currentUser.setUsername("testUser");

        GarmentDto garmentDto = new GarmentDto();
        Garment garment = new Garment();
        Garment updatedGarment = new Garment();
        GarmentDto updatedGarmentDto = new GarmentDto();

        when(modelMapper.map(garmentDto, Garment.class)).thenReturn(garment);
        when(garmentService.updateGarment(garmentId, garment, currentUser)).thenReturn(updatedGarment);
        when(modelMapper.map(updatedGarment, GarmentDto.class)).thenReturn(updatedGarmentDto);

        ResponseEntity<GarmentDto> response = garmentController.updateGarment(garmentId, garmentDto, currentUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedGarmentDto, response.getBody());
        verify(garmentService, times(1)).updateGarment(garmentId, garment, currentUser);
    }

    @Test
    public void testUnpublishGarment_AuthenticatedUser_Success() throws GarmentNotFoundException {
        Long garmentId = 1L;
        User currentUser = new User();
        currentUser.setUsername("testUser");

        doNothing().when(garmentService).unpublishGarment(garmentId, currentUser);

        ResponseEntity<String> response = garmentController.unpublishGarment(garmentId, currentUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Garment with id 1 was deleted", response.getBody());
        verify(garmentService, times(1)).unpublishGarment(garmentId, currentUser);
    }

    @Test
    public void testUnpublishGarment_NoAuthenticatedUser_Unauthorized() throws GarmentNotFoundException {
        Long garmentId = 1L;

        ResponseEntity<String> response = garmentController.unpublishGarment(garmentId, null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
