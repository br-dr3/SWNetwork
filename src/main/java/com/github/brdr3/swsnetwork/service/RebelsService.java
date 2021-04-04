package com.github.brdr3.swsnetwork.service;

import com.github.brdr3.swsnetwork.dal.entity.BetrayalReport;
import com.github.brdr3.swsnetwork.dal.entity.Rebel;
import com.github.brdr3.swsnetwork.dal.entity.RebelBase;
import com.github.brdr3.swsnetwork.dal.repository.BetrayalReportRepository;
import com.github.brdr3.swsnetwork.dal.repository.RebelBasesRepository;
import com.github.brdr3.swsnetwork.dal.repository.RebelsRepository;
import com.github.brdr3.swsnetwork.dto.RebelBaseDTO;
import com.github.brdr3.swsnetwork.dto.RebelDTO;
import com.github.brdr3.swsnetwork.dto.ReportBetrayalDTO;
import com.github.brdr3.swsnetwork.mapper.RebelMapper;
import com.github.brdr3.swsnetwork.mapper.ReportBetrayalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@PropertySource("classpath:application.properties")
@Service
public class RebelsService {
    private final RebelsRepository rebelsRepository;
    private final RebelBasesRepository rebelBasesRepository;
    private final BetrayalReportRepository betrayalReportRepository;

    @Value("${REPORTS_TO_CONSIDER_BETRAYAL}")
    private int REPORTS_TO_CONSIDER_BETRAYAL;

    @Autowired
    public RebelsService(RebelsRepository rr, RebelBasesRepository rbr, BetrayalReportRepository brr) {
        this.rebelsRepository = rr;
        this.rebelBasesRepository = rbr;
        this.betrayalReportRepository = brr;
    }

    public RebelBaseDTO insertOrGetRebelBase(RebelBaseDTO rebelBaseDTO) throws Exception {
        RebelBase rebelBase = null;

        if (rebelBaseDTO.getName() != null
                && rebelBaseDTO.getLatitude() != null
                && rebelBaseDTO.getLongitude() != null) {
            return this.insertOrGetRebelBase(
                    rebelBaseDTO.getName(), rebelBaseDTO.getLatitude(), rebelBaseDTO.getLongitude());
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
            rebelBase = rebelBasesRepository.findByUniqueKey(
                    rebelBaseDTO.getLatitude(), rebelBaseDTO.getLongitude());
        }

        if (rebelBase == null) {
            throw new Exception(
                    "Missing arguments to insert new Rebel Base and could not find any base with given "
                            + "attributes");
        }

        return RebelMapper.toRebelBaseDTO(rebelBase);
    }

    public RebelDTO insertRebel(RebelDTO rebelDTO) throws Exception {
        RebelBaseDTO savedRebelBaseDTO = insertOrGetRebelBase(rebelDTO.getRebelBase());
        rebelDTO.setRebelBase(savedRebelBaseDTO);

        try {
            Rebel savedRebel = rebelsRepository.save(RebelMapper.toRebel(rebelDTO));
            return RebelMapper.toRebelDTO(savedRebel);
        } catch (Exception e) {
            throw new Exception(
                    "Could not insert rebel on base. Maybe rebel already exist, its gender doesn't exists"
                            + " or its inventory has invalid or duplicated items");
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
            return rebel != null ? RebelMapper.toRebelDTO(rebel): null;
        } catch (Exception e) {
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

    public ReportBetrayalDTO reportBetrayal(ReportBetrayalDTO reportBetrayalDTO) throws Exception {
        RebelDTO reporter = this.getRebel(reportBetrayalDTO.getReporter());

        if (reporter == null) {
            throw new Exception("Could not get reporter rebel using parameters provided");
        }

        RebelDTO reported = this.getRebel(reportBetrayalDTO.getReported());

        if (reported == null) {
            throw new Exception("Could not get reported rebel using parameters provided");
        }

        BetrayalReport savedBetrayalReport =
                betrayalReportRepository.saveAndFlush(
                        ReportBetrayalMapper.toBetrayalReport(reportBetrayalDTO, reporter, reported));
        return this.verifyRebel(savedBetrayalReport);
    }

    private ReportBetrayalDTO verifyRebel(BetrayalReport betrayalReport) {
        List<BetrayalReport> reportsToRebel =
                betrayalReportRepository.getBetrayalReportsToRebel(betrayalReport.getReported());

        if (reportsToRebel.size() >= REPORTS_TO_CONSIDER_BETRAYAL
                && !betrayalReport.getReported().isBetrayal()) {
            Rebel reported = betrayalReport.getReported();
            reported.setBetrayal(true);
            rebelsRepository.saveAndFlush(reported);

            return ReportBetrayalMapper.toReportBetrayalDTO(
                    betrayalReportRepository.getOne(betrayalReport.getId()));
        }

        return ReportBetrayalMapper.toReportBetrayalDTO(betrayalReport);
    }

    private RebelBaseDTO insertOrGetRebelBase(String name, Float latitude, Float longitude)
            throws Exception {

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

        RebelBase savedRebelBase =
                rebelBasesRepository.save(new RebelBase(null, name, latitude, longitude));
        return RebelMapper.toRebelBaseDTO(savedRebelBase);
    }
}
