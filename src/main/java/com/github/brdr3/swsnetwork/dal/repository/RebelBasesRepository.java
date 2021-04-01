package com.github.brdr3.swsnetwork.dal.repository;

import com.github.brdr3.swsnetwork.dal.entity.RebelBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RebelBasesRepository extends JpaRepository<RebelBase, UUID> {
    @Query("SELECT rb FROM RebelBase rb WHERE rb.latitude = :latitude AND rb.longitude = :longitude")
    RebelBase findByUniqueKey(@Param("latitude") float latitude, @Param("longitude") float longitude);
}
