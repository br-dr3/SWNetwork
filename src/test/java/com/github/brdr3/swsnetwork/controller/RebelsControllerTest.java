package com.github.brdr3.swsnetwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.brdr3.swsnetwork.data.factory.NegotiateItemsFactory;
import com.github.brdr3.swsnetwork.data.factory.RebelBaseFactory;
import com.github.brdr3.swsnetwork.data.factory.RebelFactory;
import com.github.brdr3.swsnetwork.data.factory.ReportBetrayalFactory;
import com.github.brdr3.swsnetwork.dto.NegotiateItemsDTO;
import com.github.brdr3.swsnetwork.dto.RebelBaseDTO;
import com.github.brdr3.swsnetwork.dto.RebelDTO;
import com.github.brdr3.swsnetwork.dto.ReportBetrayalDTO;
import com.github.brdr3.swsnetwork.service.RebelsService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RebelsController.class)
@ExtendWith(SpringExtension.class)
public class RebelsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    public RebelsService service;

    private final ObjectMapper mapper = new ObjectMapper();
    private final RebelFactory rebelFactory = new RebelFactory();
    private final RebelBaseFactory rebelBaseFactory = new RebelBaseFactory();
    private final ReportBetrayalFactory reportBetrayalFactory = new ReportBetrayalFactory();
    private final NegotiateItemsFactory negotiateItemsFactory = new NegotiateItemsFactory();

    @Test
    public void whenInsertValidRebel_ShouldReturnOkWithRebelJson() throws Exception {
        RebelDTO savedRebelDTO = rebelFactory.createRebelDTO();
        RebelDTO rebelDTO = rebelFactory.cloneIgnoringId(savedRebelDTO);

        given(service.insertRebel(any(RebelDTO.class))).willReturn(savedRebelDTO);

        MvcResult result = this.mockMvc
                .perform(post("/rebel/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(rebelDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), mapper.writeValueAsString(savedRebelDTO));
    }

    @Test
    public void whenInsertInvalidRebel_ShouldReturnNotAcceptableWithErrorMessage() throws Exception {
        RebelDTO invalidRebel = rebelFactory.createRebelDTO();

        given(service.insertRebel(any(RebelDTO.class))).willThrow(new Exception("any-error"));

        MvcResult result = this.mockMvc
                .perform(post("/rebel/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidRebel)))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andReturn();
        assertEquals(result.getResponse().getContentAsString(), "any-error");
    }

    @Test
    public void whenAskForValidRebelById_ShouldReturnOkWithAskedRebel() throws Exception {
        RebelDTO rebel = rebelFactory.createRebelDTO();
        UUID id = rebel.getId();

        given(service.getRebel(any(UUID.class))).willReturn(rebel);

        MvcResult result = this.mockMvc.perform(get("/rebel/id/{id}", id.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), mapper.writeValueAsString(rebel));
    }

    @Test
    public void whenAskForValidRebelByName_ShouldReturnOkWithAskedRebel() throws Exception {
        RebelDTO rebel = rebelFactory.createRebelDTO();
        String name = rebel.getName();

        given(service.getRebelByName(any(String.class))).willReturn(rebel);

        MvcResult result = this.mockMvc.perform(get("/rebel/name/{name}", name))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), mapper.writeValueAsString(rebel));
    }

    @Test
    public void whenAskForInvalidRebelById_ShouldReturnNotFoundWithErrorMessage() throws Exception {
        UUID randomId = UUID.randomUUID();

        given(service.getRebel(any(UUID.class))).willReturn(null);

        MvcResult result = this.mockMvc.perform(get("/rebel/id/{id}", randomId.toString()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "Could not find any rebel with id '" +
                randomId.toString() + "'");
    }

    @Test
    public void whenAskForInvalidRebelByName_ShouldReturnNotFoundWithErrorMessage() throws Exception {
        String fakeName = "Son Goku";

        given(service.getRebelByName(any(String.class))).willReturn(null);

        MvcResult result = this.mockMvc.perform(get("/rebel/name/{name}", fakeName))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "Could not find any rebel with name '" + fakeName +
                "'");
    }

    @Test
    public void whenUpdateValidRebelBase_ShouldReturnOkWithRebelUpdated() throws Exception {
        RebelDTO rebelDTO = rebelFactory.createRebelDTO();
        UUID id = rebelDTO.getId();
        RebelBaseDTO oldRebelBase = rebelDTO.getRebelBase();

        RebelBaseDTO newRebelBase = rebelBaseFactory.createRebelBaseDTO();
        rebelDTO.setRebelBase(newRebelBase);

        given(service.updateRebelBase(any(UUID.class), any(RebelBaseDTO.class))).willReturn(rebelDTO);

        MvcResult result = this.mockMvc
                .perform(put("/rebel/id/{id}/rebelBase/update", id.toString())
                        .content(mapper.writeValueAsString(newRebelBase))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), mapper.writeValueAsString(rebelDTO));
        assertThat(result.getResponse().getContentAsString(),
                CoreMatchers.not(containsString(oldRebelBase.getId().toString())));

    }

    @Test
    public void whenUpdateInvalidRebelBase_ShouldReturnNotAcceptableWithErrorMessage() throws Exception {
        RebelDTO rebelDTO = rebelFactory.createRebelDTO();
        UUID id = rebelDTO.getId();
        RebelBaseDTO invalidRebelBase = rebelBaseFactory.createRebelBaseDTO();

        given(service.updateRebelBase(any(UUID.class), any(RebelBaseDTO.class))).willThrow(new Exception("any-error"));

        MvcResult result = this.mockMvc
                .perform(put("/rebel/id/{id}/rebelBase/update", id.toString())
                        .content(mapper.writeValueAsString(invalidRebelBase))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "any-error");
    }

    @Test
    public void whenValidReportBetrayalIsProvided_ShouldReturnOkWithReportBetrayal() throws Exception {
        ReportBetrayalDTO savedReportBetrayal = reportBetrayalFactory.createReportBetrayalDTO();
        ReportBetrayalDTO reportBetrayal = reportBetrayalFactory.cloneIgnoringId(savedReportBetrayal);

        given(service.reportBetrayal(any(ReportBetrayalDTO.class))).willReturn(savedReportBetrayal);

        MvcResult result = this.mockMvc
                .perform(post("/rebel/reportBetrayal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reportBetrayal)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), mapper.writeValueAsString(savedReportBetrayal));
    }

    @Test
    public void whenInvalidReportBetrayalIsProvided_ShouldReturnNotAcceptableWithErrorMessage() throws Exception {
        ReportBetrayalDTO invalidReportBetrayal = reportBetrayalFactory.createReportBetrayalDTO();

        given(service.reportBetrayal(any(ReportBetrayalDTO.class))).willThrow(new Exception("any-error"));

        MvcResult result = this.mockMvc
                .perform(post("/rebel/reportBetrayal")
                        .content(mapper.writeValueAsString(invalidReportBetrayal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "any-error");
    }

    @Test
    public void whenValidNegotiateItemsDTOIsProvided_ShouldReturnOkAndStringOk() throws Exception {
        NegotiateItemsDTO negotiateItemsDTO = negotiateItemsFactory.createNegotiateItemsDTO();

        willDoNothing().given(this.service).negotiateItems(any(NegotiateItemsDTO.class));

        MvcResult result = this.mockMvc
                .perform(post("/rebel/negotiateItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(negotiateItemsDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "Ok");
    }

    @Test
    public void whenInvalidNegotiateItemsDTOIsProvided_ShouldReturnNotAcceptableAndMessageError() throws Exception {
        NegotiateItemsDTO negotiateItemsDTO = negotiateItemsFactory.createNegotiateItemsDTO();

        willThrow(new Exception("any-error")).given(this.service).negotiateItems(any(NegotiateItemsDTO.class));

        MvcResult result = this.mockMvc
                .perform(post("/rebel/negotiateItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(negotiateItemsDTO)))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "any-error");
    }
}
