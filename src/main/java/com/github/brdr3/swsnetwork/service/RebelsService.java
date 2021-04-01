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

    public RebelBaseDTO insertOrGetRebelBase(RebelBaseDTO rebelBaseDTO) {
        try {
            RebelBase savedRebelBase = rebelBasesRepository.save(RebelMapper.toRebelBase(rebelBaseDTO));
            return RebelMapper.toRebelBaseDTO(savedRebelBase);
        } catch (DataIntegrityViolationException e) {
            RebelBase rebelBase = rebelBasesRepository.findByUniqueKey(rebelBaseDTO.getLatitude(), rebelBaseDTO.getLongitude());
            if(rebelBase == null) {
                rebelBase = rebelBasesRepository.findByUniqueKey(rebelBaseDTO.getName());
            }

            return RebelMapper.toRebelBaseDTO(rebelBase);
        }
    }

    public RebelDTO insertRebel(RebelDTO rebelDTO) throws Exception {
        RebelBaseDTO savedRebelBaseDTO = insertOrGetRebelBase(rebelDTO.getRebelBase());
        rebelDTO.setRebelBase(savedRebelBaseDTO);

        try {
            Rebel savedRebel = rebelsRepository.save(RebelMapper.toRebel(rebelDTO));
            return RebelMapper.toRebelDTO(savedRebel);
        } catch(DataIntegrityViolationException e) {
            throw new Exception("Could not insert rebel on base. Maybe rebel already exist or its inventory has duplicated items");
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
}
