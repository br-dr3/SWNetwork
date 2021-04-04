package com.github.brdr3.swsnetwork.dal.repository;

import com.github.brdr3.swsnetwork.dal.entity.ItemPossession;
import com.github.brdr3.swsnetwork.dal.entity.Rebel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<ItemPossession, UUID> {
    @Query("SELECT ip FROM ItemPossession ip WHERE rebel = :rebel")
    List<ItemPossession> getRebelItems(Rebel rebel);
}
