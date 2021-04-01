package com.github.brdr3.swsnetwork.dal.repository;

import com.github.brdr3.swsnetwork.dal.entity.Rebel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RebelsRepository extends JpaRepository<Rebel, UUID> {}
