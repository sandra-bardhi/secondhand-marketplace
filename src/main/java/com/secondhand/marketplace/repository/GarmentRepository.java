package com.secondhand.marketplace.repository;


import com.secondhand.marketplace.entity.Garment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GarmentRepository extends JpaRepository<Garment, Long> {
    List<Garment> findByType(String type);
}
