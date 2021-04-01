package com.github.brdr3.swsnetwork.service;

import com.github.brdr3.swsnetwork.dal.entity.Rebel;
import com.github.brdr3.swsnetwork.dal.entity.RebelBase;
import com.github.brdr3.swsnetwork.dal.repository.RebelBasesRepository;
import com.github.brdr3.swsnetwork.dal.repository.RebelsRepository;
import com.github.brdr3.swsnetwork.dto.RebelDTO;
import com.github.brdr3.swsnetwork.mapper.RebelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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


    public RebelDTO insertRebel(RebelDTO rebelDTO) {
        Rebel rebel = RebelMapper.toRebel(rebelDTO);
        RebelBase savedRebelBase = rebelBasesRepository.save(rebel.getRebelBase());

        rebel.setRebelBase(savedRebelBase);
        Rebel savedRebel = rebelsRepository.save(rebel);

        return RebelMapper.toRebelDTO(savedRebel);
    }

    public RebelDTO getRebel(UUID id) {
        try {
            Rebel rebel = rebelsRepository.getOne(id);
            return RebelMapper.toRebelDTO(rebel);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }
}
