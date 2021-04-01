package com.github.brdr3.swsnetwork.dal.repository;

import com.github.brdr3.swsnetwork.dal.entity.Rebel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RebelsRepository extends JpaRepository<Rebel, UUID> {
    @Query("SELECT r FROM Rebel r WHERE r.name = :name")
    Rebel findByUniqueKey(@Param("name") String name);
}
