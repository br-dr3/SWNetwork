package com.github.brdr3.swsnetwork.service;

import com.github.brdr3.swsnetwork.dal.entity.Rebel;
import com.github.brdr3.swsnetwork.dal.entity.RebelBase;
import com.github.brdr3.swsnetwork.dal.repository.RebelBasesRepository;
import com.github.brdr3.swsnetwork.dal.repository.RebelsRepository;
import com.github.brdr3.swsnetwork.dto.RebelBaseDTO;
import com.github.brdr3.swsnetwork.dto.RebelDTO;
import com.github.brdr3.swsnetwork.mapper.RebelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Objects;
import java.util.UUID;

@Service
public class RebelsService {
    private final RebelsRepository rebelsRepository;
    private final RebelBasesRepository rebelBasesRepository;

    @Autowired
    public RebelsService(RebelsRepository rr, RebelBasesRepository rbr) {
        this.rebelsRepository = rr;
        this.rebelBasesRepository = rbr;
    }

    private RebelBaseDTO insertOrGetRebelBase(String name, Float latitude, Float longitude) throws Exception {

        if (name == null || latitude == null || longitude == null) {
            throw new Exception("All parameters should be not null");
        }

        RebelBase rebelBaseByName = rebelBasesRepository.findByUniqueKey(name);
        RebelBase rebelBaseByLatAndLong = rebelBasesRepository.findByUniqueKey(latitude, longitude);

        if (!Objects.deepEquals(rebelBaseByName, rebelBaseByLatAndLong)) {
            throw new Exception("Arguments maps to 2 distinct Rebel Bases");
        }

        if (rebelBaseByName != null) {
            return RebelMapper.toRebelBaseDTO(rebelBaseByName);
        }

        RebelBase savedRebelBase = rebelBasesRepository.save(new RebelBase(null, name, latitude, longitude));
        return RebelMapper.toRebelBaseDTO(savedRebelBase);
    }

    public RebelBaseDTO insertOrGetRebelBase(RebelBaseDTO rebelBaseDTO) throws Exception {
        RebelBase rebelBase = null;

        if (rebelBaseDTO.getName() != null
                && rebelBaseDTO.getLatitude() != null
                && rebelBaseDTO.getLongitude() != null) {
            return this.insertOrGetRebelBase(rebelBaseDTO.getName(), rebelBaseDTO.getLatitude(),
                    rebelBaseDTO.getLongitude());
        }

        if (rebelBaseDTO.getName() == null
                && rebelBaseDTO.getLatitude() == null
                && rebelBaseDTO.getLongitude() == null) {
            throw new Exception("No argument was provided to get Rebel Base");
        }

        if (rebelBaseDTO.getName() != null) {
            rebelBase = rebelBasesRepository.findByUniqueKey(rebelBaseDTO.getName());
        }

        if (rebelBaseDTO.getLatitude() != null && rebelBaseDTO.getLongitude() != null) {
            rebelBase = rebelBasesRepository.findByUniqueKey(rebelBaseDTO.getLatitude(), rebelBaseDTO.getLongitude());
        }

        if (rebelBase == null) {
            throw new Exception("Missing arguments to insert new Rebel Base and could not find any base with given " +
                    "attributes");
        }

        return RebelMapper.toRebelBaseDTO(rebelBase);
    }

    public RebelDTO insertRebel(RebelDTO rebelDTO) throws Exception {
        RebelBaseDTO savedRebelBaseDTO = insertOrGetRebelBase(rebelDTO.getRebelBase());
        rebelDTO.setRebelBase(savedRebelBaseDTO);

        try {
            Rebel savedRebel = rebelsRepository.save(RebelMapper.toRebel(rebelDTO));
            return RebelMapper.toRebelDTO(savedRebel);
        } catch (DataIntegrityViolationException e) {
            throw new Exception("Could not insert rebel on base. Maybe rebel already exist or its inventory has " +
                    "duplicated items");
        }

    }

    public RebelDTO getRebel(UUID id) {
        try {
            Rebel rebel = rebelsRepository.getOne(id);
            return RebelMapper.toRebelDTO(rebel);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    public RebelDTO getRebelByName(String name) {
        try {
            Rebel rebel = rebelsRepository.findByUniqueKey(name);
            return RebelMapper.toRebelDTO(rebel);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    public RebelDTO updateRebelBase(UUID id, RebelBaseDTO rebelBaseDTO) throws Exception {
        RebelBaseDTO savedRebelBaseDTO = this.insertOrGetRebelBase(rebelBaseDTO);

        try {
            RebelDTO rebelDTO = this.getRebel(id);
            rebelDTO.setRebelBase(savedRebelBaseDTO);
            Rebel updatedRebel = this.rebelsRepository.save(RebelMapper.toRebel(rebelDTO));

            return RebelMapper.toRebelDTO(updatedRebel);
        } catch (Exception e) {
            throw new Exception("Could not find rebel with id '" + id + "'");
        }
    }
}
