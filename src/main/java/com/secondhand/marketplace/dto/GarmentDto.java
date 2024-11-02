package com.secondhand.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GarmentDto {
    private Long id;
    private String type;
    private String description;
    private String size;
    private double price;
    private Long publisherId;
}

