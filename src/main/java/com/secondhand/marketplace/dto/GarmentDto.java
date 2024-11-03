package com.secondhand.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GarmentDto {
    @JsonView(View.Detailed.class)
    private Long id;
    @JsonView(View.Summary.class)
    private String type;
    @JsonView(View.Summary.class)
    private String description;
    @JsonView(View.Summary.class)
    private String size;
    @JsonView(View.Summary.class)
    private double price;
    @JsonView(View.Detailed.class)
    private Long publisherId;
}

