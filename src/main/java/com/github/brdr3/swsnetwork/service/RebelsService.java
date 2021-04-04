package com.github.brdr3.swsnetwork.service;

import com.github.brdr3.swsnetwork.dal.entity.BetrayalReport;
import com.github.brdr3.swsnetwork.dal.entity.Item;
import com.github.brdr3.swsnetwork.dal.entity.ItemPossession;
import com.github.brdr3.swsnetwork.dal.entity.Rebel;
import com.github.brdr3.swsnetwork.dal.entity.RebelBase;
import com.github.brdr3.swsnetwork.dal.repository.BetrayalReportRepository;
import com.github.brdr3.swsnetwork.dal.repository.InventoryRepository;
import com.github.brdr3.swsnetwork.dal.repository.RebelBasesRepository;
import com.github.brdr3.swsnetwork.dal.repository.RebelsRepository;
import com.github.brdr3.swsnetwork.dto.NegotiateItemsDTO;
import com.github.brdr3.swsnetwork.dto.RebelBaseDTO;
import com.github.brdr3.swsnetwork.dto.RebelDTO;
import com.github.brdr3.swsnetwork.dto.ReportBetrayalDTO;
import com.github.brdr3.swsnetwork.dto.StatsDTO;
import com.github.brdr3.swsnetwork.mapper.RebelMapper;
import com.github.brdr3.swsnetwork.mapper.ReportBetrayalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PropertySource("classpath:application.properties")
@Service
public class RebelsService {
    private final RebelsRepository rebelsRepository;
    private final RebelBasesRepository rebelBasesRepository;
    private final BetrayalReportRepository betrayalReportRepository;
    private final InventoryRepository inventoryRepository;

    @Value("${REPORTS_TO_CONSIDER_BETRAYAL}")
    private int REPORTS_TO_CONSIDER_BETRAYAL;

    @Autowired
    public RebelsService(RebelsRepository rr, RebelBasesRepository rbr, BetrayalReportRepository brr,
                         InventoryRepository ir) {
        this.rebelsRepository = rr;
        this.rebelBasesRepository = rbr;
        this.betrayalReportRepository = brr;
        this.inventoryRepository = ir;
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
            return rebel != null ? RebelMapper.toRebelDTO(rebel) : null;
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

    public void negotiateItems(NegotiateItemsDTO negotiation) throws Exception {
        Rebel firstRebel = rebelsRepository.getOne(negotiation.getFirstRebel());
        validateRebel(firstRebel);

        Rebel secondRebel = rebelsRepository.getOne(negotiation.getSecondRebel());
        validateRebel(secondRebel);

        validateRebelItems(firstRebel, negotiation.getFirstRebelItems());
        validateRebelItems(secondRebel, negotiation.getSecondRebelItems());

        validatePoints(
                negotiation.getFirstRebelItems(),
                negotiation.getSecondRebelItems()
        );


        Map<Item, Integer> resultantFirstRebelItems =
                calculateResultantExchangeItems(negotiation.getFirstRebelItems(),
                        negotiation.getSecondRebelItems());

        Map<Item, Integer> resultantSecondRebelItems =
                calculateResultantExchangeItems(negotiation.getSecondRebelItems(),
                        negotiation.getFirstRebelItems());

        applyNegotiation(firstRebel, resultantFirstRebelItems,
                secondRebel, resultantSecondRebelItems
        );
    }

    public StatsDTO getStats() {
        List<Rebel> allRebels = rebelsRepository.findAll();

        long rebels = allRebels.size();
        long betrayers = allRebels.stream().filter(Rebel::isBetrayal).count();
        long corruptedPoints = allRebels.stream()
                .filter(Rebel::isBetrayal)
                .mapToInt(r -> r.getInventory()
                        .stream()
                        .mapToInt(i -> (i.getItem().getPoints() * i.getQuantity()))
                        .reduce(0, Integer::sum)
                )
                .reduce(0, Integer::sum);


        Map<Item, Long> summarizedItems = allRebels.stream()
                .map(r -> r.getInventory().stream()
                                .map(i -> Map.entry(i.getItem(), (long) i.getQuantity()))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                )
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingLong(Map.Entry::getValue)));

        Map<String, Float> resourcesPerRebel = EnumSet.allOf(Item.class)
                .stream()
                .map(e -> Map.entry(
                        e.toString(),
                        summarizedItems.containsKey(e) ? summarizedItems.get(e) * 1.0f / rebels : 0f))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return StatsDTO.builder()
                .rebels(rebels)
                .betrayers(betrayers)
                .corruptedPointsByBetrayers(corruptedPoints)
                .resourcesPerRebel(resourcesPerRebel)
                .build();
    }

    private Map<Item, Integer> calculateResultantExchangeItems(Map<String, Integer> givenRebelItems,
                                                               Map<String, Integer> receivedRebelItems) {

        Map<Item, Integer> enumGivenRebelItems = givenRebelItems.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        i -> Item.toItem(i.getKey()), i -> -i.getValue()
                ));

        Map<Item, Integer> enumReceivedRebelItems = toMapOfItem(receivedRebelItems);

        return Stream.of(enumGivenRebelItems, enumReceivedRebelItems)
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.summingInt(Map.Entry::getValue))
                ).entrySet()
                .stream()
                .filter(e -> e.getValue() != 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void validateRebel(Rebel rebel) throws Exception {
        if (rebel.isBetrayal()) {
            throw new Exception(rebel.getName() + " is betrayer and cannot negotiate its items");
        }
    }

    private Map<Item, Integer> calculateInventoryAfterGiveItems(Rebel rebel, Map<String, Integer> items) {
        Map<Item, Integer> enumItems = toMapOfItem(items);

        List<ItemPossession> rebelItemsPossession = inventoryRepository.getRebelItems(rebel);
        List<Item> rebelItems = rebelItemsPossession.stream()
                .map(ItemPossession::getItem)
                .collect(Collectors.toList());

        Map<Item, Integer> rebelFinalPossessions = rebelItemsPossession.stream()
                .collect(Collectors.toMap(
                        ItemPossession::getItem,
                        i -> enumItems.containsKey(i.getItem()) ?
                                i.getQuantity() - enumItems.get(i.getItem()) : i.getQuantity()
                ));

        Map<Item, Integer> negativeItems = enumItems.entrySet()
                .stream()
                .filter(i -> !rebelItems.contains(i.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        i -> -i.getValue()
                ));

        return Stream.concat(rebelFinalPossessions.entrySet().stream(), negativeItems.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void validateRebelItems(Rebel rebel, Map<String, Integer> rebelItems) throws Exception {
        Map<Item, Integer> rebelInventory = this.calculateInventoryAfterGiveItems(
                rebel, rebelItems
        );

        boolean rebelHaveAllItems = rebelInventory.values()
                .stream()
                .map(v -> v >= 0)
                .reduce(true, Boolean::logicalAnd);

        if (!rebelHaveAllItems) {
            throw new Exception(rebel.getName() + " does not have all items");
        }
    }

    private void validatePoints(Map<String, Integer> firstRebelItems,
                                Map<String, Integer> secondRebelItems) throws Exception {

        Map<Item, Integer> enumFirstRebelItems = toMapOfItem(firstRebelItems);
        Map<Item, Integer> enumSecondRebelItems = toMapOfItem(secondRebelItems);

        if (sumItemsPoints(enumFirstRebelItems) != sumItemsPoints(enumSecondRebelItems)) {
            throw new Exception("Rebels don't have same amount of points in negotiation");
        }
    }

    private Map<Item, Integer> toMapOfItem(Map<String, Integer> itemNames) {
        return itemNames.entrySet().stream().collect(Collectors.toMap(
                i -> Item.toItem(i.getKey()),
                Map.Entry::getValue
        ));
    }

    private int sumItemsPoints(Map<Item, Integer> items) {
        return items.entrySet()
                .stream()
                .map(i -> i.getKey().getPoints() * i.getValue())
                .reduce(0, Integer::sum);
    }

    private void applyNegotiation(Rebel firstRebel,
                                  Map<Item, Integer> firstRebelInventoryUpdates,
                                  Rebel secondRebel,
                                  Map<Item, Integer> secondRebelInventoryUpdates) {
        applyNegotiationToRebel(firstRebel, firstRebelInventoryUpdates);
        applyNegotiationToRebel(secondRebel, secondRebelInventoryUpdates);
    }

    private void applyNegotiationToRebel(Rebel rebel, Map<Item, Integer> inventoryUpdates) {
        List<ItemPossession> notUpdatedInventory = rebel.getInventory()
                .stream()
                .filter(ip -> !inventoryUpdates.containsKey(ip.getItem()))
                .collect(Collectors.toList());

        List<ItemPossession> updateInventory = rebel.getInventory()
                .stream()
                .filter(ip -> inventoryUpdates.containsKey(ip.getItem()))
                .map(ip -> ItemPossession.builder()
                        .id(ip.getId())
                        .item(ip.getItem())
                        .quantity(ip.getQuantity() + inventoryUpdates.get(ip.getItem()))
                        .rebel(ip.getRebel())
                        .build())
                .collect(Collectors.toList());

        List<ItemPossession> newItems = inventoryUpdates.entrySet()
                .stream()
                .filter(iu -> !rebel.getInventory()
                        .stream()
                        .map(ItemPossession::getItem)
                        .collect(Collectors.toList())
                        .contains(iu.getKey()))
                .map(iu -> ItemPossession.builder()
                        .quantity(iu.getValue())
                        .item(iu.getKey())
                        .rebel(rebel)
                        .build())
                .collect(Collectors.toList());

        List<ItemPossession> finalInventory =
                Stream.of(notUpdatedInventory, updateInventory, newItems)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

        rebel.setInventory(finalInventory);
        rebelsRepository.save(rebel);
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
