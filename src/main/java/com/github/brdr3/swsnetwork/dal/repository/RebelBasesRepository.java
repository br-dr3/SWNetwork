package com.github.brdr3.swsnetwork.dal.repository;

import com.github.brdr3.swsnetwork.dal.entity.RebelBase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RebelBasesRepository extends JpaRepository<RebelBase, UUID> {
}
