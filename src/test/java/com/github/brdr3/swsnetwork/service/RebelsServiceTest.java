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
import com.github.brdr3.swsnetwork.data.factory.NegotiateItemsFactory;
import com.github.brdr3.swsnetwork.data.factory.RebelBaseFactory;
import com.github.brdr3.swsnetwork.data.factory.RebelFactory;
import com.github.brdr3.swsnetwork.data.factory.ReportBetrayalFactory;
import com.github.brdr3.swsnetwork.dto.NegotiateItemsDTO;
import com.github.brdr3.swsnetwork.dto.RebelBaseDTO;
import com.github.brdr3.swsnetwork.dto.RebelDTO;
import com.github.brdr3.swsnetwork.dto.ReportBetrayalDTO;
import com.github.brdr3.swsnetwork.dto.StatsDTO;
import com.github.brdr3.swsnetwork.mapper.RebelMapper;
import com.github.brdr3.swsnetwork.mapper.ReportBetrayalMapper;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(SpringExtension.class)
public class RebelsServiceTest {
    @Mock
    private RebelsRepository rebelsRepository;

    @Mock
    private RebelBasesRepository rebelBasesRepository;

    @Mock
    private BetrayalReportRepository betrayalReportRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private RebelsService service;

    private final RebelBaseFactory rebelBaseFactory = new RebelBaseFactory();
    private final RebelFactory rebelFactory = new RebelFactory();
    private final ReportBetrayalFactory reportBetrayalFactory = new ReportBetrayalFactory();
    private final NegotiateItemsFactory negotiateItemsFactory = new NegotiateItemsFactory();

    @Test
    public void whenProvidedValidRebelBaseDTOAndRebelBaseDoesNotExist_ShouldCreateIt() {
        RebelBaseDTO validRebelBaseDTO = rebelBaseFactory.createRebelBaseDTO();
        RebelBase expectedRebelBaseOutput = RebelMapper.toRebelBase(validRebelBaseDTO);

        when(rebelBasesRepository.findByUniqueKey(any(String.class))).thenReturn(null);
        when(rebelBasesRepository.findByUniqueKey(any(Float.class), any(Float.class))).thenReturn(null);
        when(rebelBasesRepository.save(any(RebelBase.class))).thenReturn(expectedRebelBaseOutput);

        try {
            RebelBaseDTO createdRebelBase = service.insertOrGetRebelBase(validRebelBaseDTO);
            assertThat(createdRebelBase).isEqualTo(validRebelBaseDTO);
        } catch (Exception e) {
            fail("It should not throw exception");
        }

    }

    @Test
    public void whenProvidedValidRebelBaseDTOAndRebelDoesExist_ShouldGetIt_ScenarioOne() {
        // When All Parameters were provided
        RebelBase rebelBase = rebelBaseFactory.createRebelBase();

        RebelBaseDTO expectedRebelBaseDTO = RebelMapper.toRebelBaseDTO(rebelBase);
        RebelBaseDTO providedRebelBaseDTO = rebelBaseFactory.cloneIgnoringId(expectedRebelBaseDTO);

        when(rebelBasesRepository.findByUniqueKey(any(String.class))).thenReturn(rebelBase);
        when(rebelBasesRepository.findByUniqueKey(any(Float.class), any(Float.class))).thenReturn(rebelBase);

        try {
            RebelBaseDTO resultedRebelBaseDTO = service.insertOrGetRebelBase(providedRebelBaseDTO);
            assertThat(expectedRebelBaseDTO).isEqualTo(resultedRebelBaseDTO);
        } catch (Exception e) {
            fail("It should not throw Exception");
        }
    }

    @Test
    public void whenProvidedValidRebelBaseDTOAndRebelDoesExist_ShouldGetIt_ScenarioTwo() {
        // When name was not provided
        RebelBase rebelBase = rebelBaseFactory.createRebelBase();

        RebelBaseDTO expectedRebelBaseDTO = RebelMapper.toRebelBaseDTO(rebelBase);
        RebelBaseDTO providedRebelBaseDTO = rebelBaseFactory.cloneIgnoringId(expectedRebelBaseDTO);
        providedRebelBaseDTO.setName(null);

        when(rebelBasesRepository.findByUniqueKey(any(Float.class), any(Float.class))).thenReturn(rebelBase);

        try {
            RebelBaseDTO resultedRebelBaseDTO = service.insertOrGetRebelBase(providedRebelBaseDTO);
            assertThat(expectedRebelBaseDTO).isEqualTo(resultedRebelBaseDTO);
        } catch (Exception e) {
            fail("It should not throw Exception");
        }
    }

    @Test
    public void whenProvidedValidRebelBaseDTOAndRebelDoesExist_ShouldGetIt_ScenarioThree() {
        // When latitude and longitude was not provided
        RebelBase rebelBase = rebelBaseFactory.createRebelBase();

        RebelBaseDTO expectedRebelBaseDTO = RebelMapper.toRebelBaseDTO(rebelBase);
        RebelBaseDTO providedRebelBaseDTO = rebelBaseFactory.cloneIgnoringId(expectedRebelBaseDTO);
        providedRebelBaseDTO.setLatitude(null);
        providedRebelBaseDTO.setLongitude(null);

        when(rebelBasesRepository.findByUniqueKey(any(String.class))).thenReturn(rebelBase);

        try {
            RebelBaseDTO resultedRebelBaseDTO = service.insertOrGetRebelBase(providedRebelBaseDTO);
            assertThat(expectedRebelBaseDTO).isEqualTo(resultedRebelBaseDTO);
        } catch (Exception e) {
            fail("It should not throw Exception");
        }
    }

    @Test
    public void whenParametersMatchToTwoDistinctRebelBases_ShouldThrowException() {
        RebelBaseDTO anyRebelBaseDTO = rebelBaseFactory.createRebelBaseDTO();

        RebelBase first = rebelBaseFactory.createRebelBase();
        RebelBase second = rebelBaseFactory.createRebelBase();

        when(rebelBasesRepository.findByUniqueKey(any(String.class))).thenReturn(first);
        when(rebelBasesRepository.findByUniqueKey(any(Float.class), any(Float.class))).thenReturn(second);

        try {
            service.insertOrGetRebelBase(anyRebelBaseDTO);
            fail("should throw exception because first != second");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Arguments maps to 2 distinct Rebel Bases");
        }

    }

    @Test
    public void whenGetRebelBaseButNotProvideAnything_ShouldThrowException() {
        RebelBaseDTO noArgumentsRebelBaseDTO = RebelBaseDTO.builder()
                .id(null)
                .latitude(null)
                .longitude(null)
                .name(null)
                .build();

        try {
            service.insertOrGetRebelBase(noArgumentsRebelBaseDTO);
            fail("RebelBase went saved or gotten");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "No argument was provided to get Rebel Base");
        }
    }

    @Test
    public void whenInsertRebelIsNotPossible_ShouldThrowException() {
        RebelBaseDTO rebelBaseDTO = RebelBaseDTO.builder()
                .id(null)
                .longitude(null)
                .latitude(10f)
                .name("Invalid Base")
                .build();

        when(rebelBasesRepository.findByUniqueKey(any(String.class))).thenReturn(null);

        try {
            service.insertOrGetRebelBase(rebelBaseDTO);
            fail("Could not get without longitude / latitude");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Missing arguments to insert new Rebel Base and could not find any base with" +
                    " given attributes");
        }
    }

    @Test
    public void whenInsertValidRebel_ShouldExecuteSuccessfully() {
        RebelDTO rebelDTO = rebelFactory.createRebelDTO();

        when(rebelBasesRepository.findByUniqueKey(any(String.class))).thenReturn(null);
        when(rebelBasesRepository.findByUniqueKey(any(Float.class), any(Float.class))).thenReturn(null);
        when(rebelBasesRepository.save(any(RebelBase.class))).thenReturn(RebelMapper.toRebelBase(rebelDTO.getRebelBase()));

        when(rebelsRepository.save(any(Rebel.class))).thenReturn(RebelMapper.toRebel(rebelDTO));

        try {
            RebelDTO resultRebel = service.insertRebel(rebelDTO);
            assertEquals(rebelDTO, resultRebel);
        } catch (Exception e) {
            fail("Should not throw exception");
        }
    }

    @Test
    public void whenInsertInvalidRebel_ShouldThrowException() {
        RebelDTO rebelDTO = rebelFactory.createRebelDTO();

        when(rebelBasesRepository.findByUniqueKey(any(String.class))).thenReturn(null);
        when(rebelBasesRepository.findByUniqueKey(any(Float.class), any(Float.class))).thenReturn(null);
        when(rebelBasesRepository.save(any(RebelBase.class))).thenReturn(RebelMapper.toRebelBase(rebelDTO.getRebelBase()));

        when(rebelsRepository.save(any(Rebel.class))).thenThrow(new DataIntegrityViolationException(""));

        try {
            service.insertRebel(rebelDTO);
            fail("Should throw Exception");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Could not insert rebel on base. Maybe rebel already exist, its gender " +
                    "doesn't exists or its inventory has invalid or duplicated items");
        }
    }

    @Test
    public void whenGetValidRebel_ShouldReturnRebel() {
        Rebel rebel = rebelFactory.createRebel();
        RebelDTO expectedRebel = RebelMapper.toRebelDTO(rebel);

        UUID id = rebel.getId();

        when(rebelsRepository.getOne(any(UUID.class))).thenReturn(rebel);

        RebelDTO rebelDTO = service.getRebel(id);

        assertEquals(expectedRebel, rebelDTO);
    }

    @Test
    public void whenRebelDoesNotExist_ShouldReturnNull() {
        UUID id = UUID.randomUUID();

        when(rebelsRepository.getOne(any(UUID.class))).thenThrow(new EntityNotFoundException());

        RebelDTO rebelDTO = service.getRebel(id);

        assertNull(rebelDTO);
    }

    @Test
    public void whenRebelNameExist_ShouldReturnRebel() {
        Rebel rebel = rebelFactory.createRebel();
        String name = rebel.getName();

        when(rebelsRepository.findByUniqueKey(any(String.class))).thenReturn(rebel);

        RebelDTO expectedRebel = RebelMapper.toRebelDTO(rebel);

        RebelDTO resultRebel = service.getRebelByName(name);
        assertEquals(expectedRebel, resultRebel);
    }

    @Test
    public void whenRebelNameDoesNotExist_ShouldReturnNull_ScenarioOne() {
        // Repository answer null

        String name = "Son Goku";
        when(rebelsRepository.findByUniqueKey(any(String.class))).thenReturn(null);

        RebelDTO resultRebel = service.getRebelByName(name);
        assertNull(resultRebel);
    }

    @Test
    public void whenRebelNameDoesNotExist_ShouldReturnNull_ScenarioTwo() {
        // Repository throws any error

        String name = "Son Goku";
        when(rebelsRepository.findByUniqueKey(any(String.class))).thenThrow(new NoSuchElementException());

        RebelDTO resultRebel = service.getRebelByName(name);
        assertNull(resultRebel);
    }

    @Test
    public void whenUpdateToValidRebelBase_ShouldReturnUpdatedRebel() {
        RebelBaseDTO rebelBaseDTO = rebelBaseFactory.createRebelBaseDTO();
        RebelDTO rebelDTO = rebelFactory.createRebelDTO();
        rebelDTO.setRebelBase(rebelBaseDTO);

        when(rebelBasesRepository.findByUniqueKey(any(String.class))).thenReturn(null);
        when(rebelBasesRepository.findByUniqueKey(any(Float.class), any(Float.class))).thenReturn(null);
        when(rebelBasesRepository.save(any(RebelBase.class))).thenReturn(RebelMapper.toRebelBase(rebelBaseDTO));

        when(rebelsRepository.getOne(rebelDTO.getId())).thenReturn(RebelMapper.toRebel(rebelDTO));
        when(rebelsRepository.save(any(Rebel.class))).thenReturn(RebelMapper.toRebel(rebelDTO));

        try {
            RebelDTO resultRebel = service.updateRebelBase(rebelDTO.getId(), rebelBaseDTO);
            assertEquals(resultRebel, rebelDTO);
        } catch (Exception e) {
            fail("It was not supposed to throw exception here");
        }
    }

    @Test
    public void whenRebelDoesNotExist_ShouldThrowException() {
        RebelBaseDTO rebelBaseDTO = rebelBaseFactory.createRebelBaseDTO();
        UUID id = UUID.randomUUID();

        when(rebelBasesRepository.findByUniqueKey(any(String.class))).thenReturn(null);
        when(rebelBasesRepository.findByUniqueKey(any(Float.class), any(Float.class))).thenReturn(null);
        when(rebelBasesRepository.save(any(RebelBase.class))).thenReturn(RebelMapper.toRebelBase(rebelBaseDTO));

        when(rebelsRepository.getOne(id)).thenReturn(null);

        try {
            RebelDTO resultRebel = service.updateRebelBase(id, rebelBaseDTO);
            fail("It was supposed to throw exception here");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Could not find rebel with id '" + id + "'");
        }
    }

    @Test
    public void whenValidReportBetrayalIsProvided_ShouldReturnReportBetrayal() {
        ReportBetrayalDTO reportBetrayalDTO = reportBetrayalFactory.createReportBetrayalDTO();

        RebelDTO reporterRebel = rebelFactory.createRebelDTO();
        reporterRebel.setId(reportBetrayalDTO.getReporter());

        RebelDTO reportedRebel = rebelFactory.createRebelDTO();
        reportedRebel.setId(reportBetrayalDTO.getReported());

        BetrayalReport betrayalReport = ReportBetrayalMapper.toBetrayalReport(
                reportBetrayalDTO, reporterRebel, reportedRebel);

        when(rebelsRepository.getOne(reportBetrayalDTO.getReporter())).thenReturn(RebelMapper.toRebel(reporterRebel));
        when(rebelsRepository.getOne(reportBetrayalDTO.getReported())).thenReturn(RebelMapper.toRebel(reportedRebel));
        Mockito.doReturn(betrayalReport).when(betrayalReportRepository).saveAndFlush(any(BetrayalReport.class));

        when(betrayalReportRepository.getBetrayalReportsToRebel(any(Rebel.class))).thenReturn(new ArrayList<>());

        try {
            ReportBetrayalDTO result = service.reportBetrayal(reportBetrayalDTO);
            assertEquals(reportBetrayalDTO, result);
        } catch (Exception e) {
            fail("It was not supposed to throw exception");
        }
    }

    @Test
    public void whenReportingRebelDoesNotExist_ShouldThrowError() {
        ReportBetrayalDTO reportBetrayalDTO = reportBetrayalFactory.createReportBetrayalDTO();
        UUID reporterId = reportBetrayalDTO.getReporter();

        when(rebelsRepository.getOne(reporterId)).thenThrow(new EntityNotFoundException());

        try {
            service.reportBetrayal(reportBetrayalDTO);
            fail("It was not supposed to reach here");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Could not get reporter rebel using parameters provided");
        }

    }

    @Test
    public void whenReportedRebelDoesNotExist_ShouldThrowError() {
        ReportBetrayalDTO reportBetrayalDTO = reportBetrayalFactory.createReportBetrayalDTO();
        UUID reporterId = reportBetrayalDTO.getReporter();
        UUID reportedId = reportBetrayalDTO.getReported();

        Rebel reporterRebel = rebelFactory.createRebel();
        reporterRebel.setId(reporterId);

        when(rebelsRepository.getOne(reporterId)).thenReturn(reporterRebel);
        when(rebelsRepository.getOne(reportedId)).thenThrow(new EntityNotFoundException());

        try {
            service.reportBetrayal(reportBetrayalDTO);
            fail("It was not supposed to reach here");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Could not get reported rebel using parameters provided");
        }
    }

    @Test
    public void whenValidNegotiateItemsIsProvided_ShouldExecuteWithoutErrors() {
        NegotiateItemsDTO negotiateItemsDTO = negotiateItemsFactory.createNegotiateItemsDTO();
        negotiateItemsDTO.setFirstRebelItems(Map.of(Item.WATER.toString(), 1));
        negotiateItemsDTO.setSecondRebelItems(Map.of(Item.FOOD.toString(), 2));

        Rebel firstRebel = rebelFactory.createRebel();
        List<ItemPossession> firstInventory = List.of(ItemPossession.builder()
                .id(UUID.randomUUID())
                .quantity(3)
                .item(Item.WATER)
                .rebel(firstRebel)
                .build());
        firstRebel.setId(negotiateItemsDTO.getFirstRebel());
        firstRebel.setBetrayal(false);
        firstRebel.setInventory(firstInventory);

        Rebel secondRebel = rebelFactory.createRebel();
        List<ItemPossession> secondInventory = List.of(ItemPossession.builder()
                .id(UUID.randomUUID())
                .quantity(6)
                .item(Item.FOOD)
                .rebel(secondRebel)
                .build());
        secondRebel.setId(negotiateItemsDTO.getSecondRebel());
        secondRebel.setBetrayal(false);
        secondRebel.setInventory(secondInventory);

        when(rebelsRepository.getOne(firstRebel.getId())).thenReturn(firstRebel);
        when(rebelsRepository.getOne(secondRebel.getId())).thenReturn(secondRebel);
        when(inventoryRepository.getRebelItems(firstRebel)).thenReturn(firstInventory);
        when(inventoryRepository.getRebelItems(secondRebel)).thenReturn(secondInventory);
        Mockito.doReturn(null).when(rebelsRepository).save(any(Rebel.class));

        try {
            service.negotiateItems(negotiateItemsDTO);
        } catch (Exception e) {
            fail("Should not reach here");
        }
    }

    @Test
    public void validateRebelTest_ScenarioOne() {
        // None is betrayer, tested in following method
        whenValidNegotiateItemsIsProvided_ShouldExecuteWithoutErrors();
    }

    @Test
    public void validateRebelTest_ScenarioTwo() {
        // First rebel is betrayer

        NegotiateItemsDTO negotiateItemsDTO = negotiateItemsFactory.createNegotiateItemsDTO();
        Rebel rebel = rebelFactory.createRebel();
        rebel.setId(negotiateItemsDTO.getFirstRebel());
        rebel.setBetrayal(true);

        when(rebelsRepository.getOne(rebel.getId())).thenReturn(rebel);

        try {
            service.negotiateItems(negotiateItemsDTO);
            fail("Should not reach here");
        } catch (Exception e) {
            assertEquals(e.getMessage(), rebel.getName() + " is betrayer and cannot negotiate its items");
        }
    }

    @Test
    public void validateRebelTest_ScenarioThree() {
        // Second rebel is betrayer

        NegotiateItemsDTO negotiateItemsDTO = negotiateItemsFactory.createNegotiateItemsDTO();
        Rebel firstRebel = rebelFactory.createRebel();
        firstRebel.setId(negotiateItemsDTO.getFirstRebel());
        firstRebel.setBetrayal(false);

        Rebel secondRebel = rebelFactory.createRebel();
        secondRebel.setId(negotiateItemsDTO.getSecondRebel());
        secondRebel.setBetrayal(true);

        when(rebelsRepository.getOne(firstRebel.getId())).thenReturn(firstRebel);
        when(rebelsRepository.getOne(secondRebel.getId())).thenReturn(secondRebel);

        try {
            service.negotiateItems(negotiateItemsDTO);
            fail("Should not reach here");
        } catch (Exception e) {
            assertEquals(e.getMessage(), secondRebel.getName() + " is betrayer and cannot negotiate its items");
        }
    }

    @Test
    public void validateRebelItemsTest_ScenarioOne() {
        // All have all items betrayer, tested in following method
        whenValidNegotiateItemsIsProvided_ShouldExecuteWithoutErrors();
    }

    @Test
    public void validateRebelItemsTest_ScenarioTwo() {
        // first rebel doesn't have all items

        NegotiateItemsDTO negotiateItemsDTO = negotiateItemsFactory.createNegotiateItemsDTO();
        negotiateItemsDTO.setFirstRebelItems(Map.of(Item.WATER.toString(), 1));
        negotiateItemsDTO.setSecondRebelItems(Map.of(Item.FOOD.toString(), 2));

        Rebel firstRebel = rebelFactory.createRebel();
        firstRebel.setId(negotiateItemsDTO.getFirstRebel());
        firstRebel.setBetrayal(false);
        firstRebel.setInventory(List.of(ItemPossession.builder()
                .id(UUID.randomUUID())
                .quantity(0)
                .item(Item.WATER)
                .rebel(firstRebel)
                .build()));

        Rebel secondRebel = rebelFactory.createRebel();
        secondRebel.setId(negotiateItemsDTO.getSecondRebel());
        secondRebel.setBetrayal(false);
        secondRebel.setInventory(List.of(ItemPossession.builder()
                .id(UUID.randomUUID())
                .quantity(6)
                .item(Item.FOOD)
                .rebel(secondRebel)
                .build()));

        when(rebelsRepository.getOne(firstRebel.getId())).thenReturn(firstRebel);
        when(rebelsRepository.getOne(secondRebel.getId())).thenReturn(secondRebel);

        try {
            service.negotiateItems(negotiateItemsDTO);
        } catch (Exception e) {
            assertEquals(e.getMessage(), firstRebel.getName() + " does not have all items");
        }
    }

    @Test
    public void validateRebelItemsTest_ScenarioThree() {
        // second rebel doesn't have all items

        NegotiateItemsDTO negotiateItemsDTO = negotiateItemsFactory.createNegotiateItemsDTO();
        negotiateItemsDTO.setFirstRebelItems(Map.of(Item.WATER.toString(), 1));
        negotiateItemsDTO.setSecondRebelItems(Map.of(Item.FOOD.toString(), 2));

        Rebel firstRebel = rebelFactory.createRebel();
        List<ItemPossession> firstInventory = List.of(ItemPossession.builder()
                .id(UUID.randomUUID())
                .quantity(3)
                .item(Item.WATER)
                .rebel(firstRebel)
                .build());
        firstRebel.setId(negotiateItemsDTO.getFirstRebel());
        firstRebel.setBetrayal(false);
        firstRebel.setInventory(firstInventory);

        Rebel secondRebel = rebelFactory.createRebel();
        List<ItemPossession> secondInventory = List.of(ItemPossession.builder()
                .id(UUID.randomUUID())
                .quantity(1)
                .item(Item.FOOD)
                .rebel(secondRebel)
                .build());
        secondRebel.setId(negotiateItemsDTO.getSecondRebel());
        secondRebel.setBetrayal(false);
        secondRebel.setInventory(secondInventory);

        when(rebelsRepository.getOne(firstRebel.getId())).thenReturn(firstRebel);
        when(rebelsRepository.getOne(secondRebel.getId())).thenReturn(secondRebel);
        when(inventoryRepository.getRebelItems(firstRebel)).thenReturn(firstInventory);
        when(inventoryRepository.getRebelItems(secondRebel)).thenReturn(secondInventory);

        try {
            service.negotiateItems(negotiateItemsDTO);
        } catch (Exception e) {
            assertEquals(e.getMessage(), secondRebel.getName() + " does not have all items");
        }
    }

    @Test
    public void validatePointsTest_ScenarioOne() {
        // items negotiated have same amount of points, tested in following method
        whenValidNegotiateItemsIsProvided_ShouldExecuteWithoutErrors();
    }

    @Test
    public void validatePointTest_ScenarioTwo() {
        // items negotiated have different points

        NegotiateItemsDTO negotiateItemsDTO = negotiateItemsFactory.createNegotiateItemsDTO();
        negotiateItemsDTO.setFirstRebelItems(Map.of(Item.WATER.toString(), 1));
        negotiateItemsDTO.setSecondRebelItems(Map.of(Item.FOOD.toString(), 1));

        Rebel firstRebel = rebelFactory.createRebel();
        List<ItemPossession> firstInventory = List.of(ItemPossession.builder()
                .id(UUID.randomUUID())
                .quantity(1)
                .item(Item.WATER)
                .rebel(firstRebel)
                .build());
        firstRebel.setId(negotiateItemsDTO.getFirstRebel());
        firstRebel.setBetrayal(false);
        firstRebel.setInventory(firstInventory);

        Rebel secondRebel = rebelFactory.createRebel();
        List<ItemPossession> secondInventory = List.of(ItemPossession.builder()
                .id(UUID.randomUUID())
                .quantity(2)
                .item(Item.FOOD)
                .rebel(secondRebel)
                .build());
        secondRebel.setId(negotiateItemsDTO.getSecondRebel());
        secondRebel.setBetrayal(false);
        secondRebel.setInventory(secondInventory);

        when(rebelsRepository.getOne(firstRebel.getId())).thenReturn(firstRebel);
        when(rebelsRepository.getOne(secondRebel.getId())).thenReturn(secondRebel);
        when(inventoryRepository.getRebelItems(firstRebel)).thenReturn(firstInventory);
        when(inventoryRepository.getRebelItems(secondRebel)).thenReturn(secondInventory);

        try {
            service.negotiateItems(negotiateItemsDTO);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Rebels don't have same amount of points in negotiation");
        }
    }

    @Test
    public void getStatsTest() {
        Rebel firstRebel = rebelFactory.createRebel();
        firstRebel.setBetrayal(true);
        firstRebel.setInventory(List.of(
                ItemPossession.builder().item(Item.GUN).quantity(1).rebel(firstRebel).id(UUID.randomUUID()).build(),
                ItemPossession.builder().item(Item.AMMO).quantity(1).rebel(firstRebel).id(UUID.randomUUID()).build(),
                ItemPossession.builder().item(Item.WATER).quantity(1).rebel(firstRebel).id(UUID.randomUUID()).build(),
                ItemPossession.builder().item(Item.FOOD).quantity(1).rebel(firstRebel).id(UUID.randomUUID()).build()));

        Rebel secondRebel = rebelFactory.createRebel();
        secondRebel.setBetrayal(false);
        secondRebel.setInventory(List.of(
                ItemPossession.builder().item(Item.GUN).quantity(2).rebel(firstRebel).id(UUID.randomUUID()).build(),
                ItemPossession.builder().item(Item.AMMO).quantity(2).rebel(firstRebel).id(UUID.randomUUID()).build(),
                ItemPossession.builder().item(Item.WATER).quantity(2).rebel(firstRebel).id(UUID.randomUUID()).build(),
                ItemPossession.builder().item(Item.FOOD).quantity(2).rebel(firstRebel).id(UUID.randomUUID()).build()));

        Rebel thirdRebel = rebelFactory.createRebel();
        thirdRebel.setBetrayal(false);
        thirdRebel.setInventory(List.of(
                ItemPossession.builder().item(Item.GUN).quantity(3).rebel(firstRebel).id(UUID.randomUUID()).build(),
                ItemPossession.builder().item(Item.AMMO).quantity(3).rebel(firstRebel).id(UUID.randomUUID()).build(),
                ItemPossession.builder().item(Item.WATER).quantity(3).rebel(firstRebel).id(UUID.randomUUID()).build(),
                ItemPossession.builder().item(Item.FOOD).quantity(3).rebel(firstRebel).id(UUID.randomUUID()).build()));


        when(rebelsRepository.findAll()).thenReturn(List.of(firstRebel, secondRebel, thirdRebel));

        long rebels = 3;
        long betrayers = 1;
        long corruptedPoints =
                Item.FOOD.getPoints() + Item.GUN.getPoints() + Item.AMMO.getPoints() + Item.WATER.getPoints();


        Map<String, Float> resourcesPerRebel = Map.of(
                Item.FOOD.toString(), (1 + 2 + 3) * 1.0f / 3,
                Item.WATER.toString(), (1 + 2 + 3) * 1.0f / 3,
                Item.AMMO.toString(), (1 + 2 + 3) * 1.0f / 3,
                Item.GUN.toString(), (1 + 2 + 3) * 1.0f / 3
        );

        StatsDTO expectedStats = StatsDTO.builder()
                .rebels(rebels)
                .betrayers(betrayers)
                .resourcesPerRebel(resourcesPerRebel)
                .corruptedPointsByBetrayers(corruptedPoints)
                .build();

        StatsDTO resultStats = service.getStats();

        assertEquals(expectedStats, resultStats);
    }
}
