package com.secondhand.marketplace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "garment")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Garment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String description;
    private String size;
    private double price;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Foreign key column in garments table
    private User publisher;
}
