package com.github.brdr3.swsnetwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.brdr3.swsnetwork.data.factory.StatsFactory;
import com.github.brdr3.swsnetwork.dto.StatsDTO;
import com.github.brdr3.swsnetwork.service.RebelsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatsController.class)
@ExtendWith(SpringExtension.class)
public class StatsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    public RebelsService service;

    private final ObjectMapper mapper = new ObjectMapper();
    private final StatsFactory statsFactory = new StatsFactory();

    @Test
    public void whenAskedForStats_ReturnStatsAndOk() throws Exception {
        StatsDTO statsDTO = statsFactory.createStatsDTO();
        given(service.getStats()).willReturn(statsDTO);

        MvcResult result = this.mockMvc
                .perform(get("/stats/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), mapper.writeValueAsString(statsDTO));
    }
}
