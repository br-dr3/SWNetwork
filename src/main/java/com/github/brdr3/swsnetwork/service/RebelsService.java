package com.github.brdr3.swsnetwork.service;

import com.github.brdr3.swsnetwork.dal.entity.Rebel;
import com.github.brdr3.swsnetwork.dal.entity.RebelBase;
import com.github.brdr3.swsnetwork.dal.repository.RebelBasesRepository;
import com.github.brdr3.swsnetwork.dal.repository.RebelsRepository;
import com.github.brdr3.swsnetwork.dto.RebelBaseDTO;
import com.github.brdr3.swsnetwork.dto.RebelDTO;
import com.github.brdr3.swsnetwork.mapper.RebelMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
public class RebelsService {
    private final RebelsRepository rebelsRepository;
    private final RebelBasesRepository rebelBasesRepository;
    private final Gson gson = new Gson();

    @Autowired
    public RebelsService(RebelsRepository rr, RebelBasesRepository rbr) {
        this.rebelsRepository = rr;
        this.rebelBasesRepository = rbr;
    }

    public RebelBaseDTO insertOrGetRebelBase(RebelBaseDTO rebelBaseDTO) throws Exception {
        try {
            RebelBase savedRebelBase = rebelBasesRepository.save(RebelMapper.toRebelBase(rebelBaseDTO));
            return RebelMapper.toRebelBaseDTO(savedRebelBase);
        } catch (Exception e) {
            RebelBase rebelBaseByLatitudeAndLongitude = rebelBasesRepository.findByUniqueKey(rebelBaseDTO.getLatitude(),
                    rebelBaseDTO.getLongitude());
            RebelBase rebelBaseByName = rebelBasesRepository.findByUniqueKey(rebelBaseDTO.getName());

            if (rebelBaseByLatitudeAndLongitude == null && rebelBaseByName == null) {
                throw new Exception("Missing arguments to insert new Rebel Base and could not find any base with this attributes");
            }

            boolean rebelBasesAreNotSame = rebelBaseByName != null
                    && rebelBaseByLatitudeAndLongitude != null
                    && !rebelBaseByName.equals(rebelBaseByLatitudeAndLongitude);

            boolean latitudeAndLongitudeMatchNamedRebelBase = rebelBaseByName != null
                    && rebelBaseByName.getLatitude() == rebelBaseDTO.getLatitude()
                    && rebelBaseByName.getLongitude() == rebelBaseDTO.getLongitude();

            if (rebelBasesAreNotSame || !latitudeAndLongitudeMatchNamedRebelBase) {
                throw new Exception("Arguments maps to 2 distinct Rebel Bases");
            }

            return rebelBaseByLatitudeAndLongitude == null ?
                    RebelMapper.toRebelBaseDTO(rebelBaseByName) :
                    RebelMapper.toRebelBaseDTO(rebelBaseByLatitudeAndLongitude);
        }
    }

    public RebelDTO insertRebel(RebelDTO rebelDTO) throws Exception {
        RebelBaseDTO savedRebelBaseDTO = insertOrGetRebelBase(rebelDTO.getRebelBase());
        rebelDTO.setRebelBase(savedRebelBaseDTO);

        try {
            Rebel savedRebel = rebelsRepository.save(RebelMapper.toRebel(rebelDTO));
            return RebelMapper.toRebelDTO(savedRebel);
        } catch (DataIntegrityViolationException e) {
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
