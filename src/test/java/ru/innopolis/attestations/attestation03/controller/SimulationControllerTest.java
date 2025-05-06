package ru.innopolis.attestations.attestation03.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.innopolis.attestations.attestation03.dto.SimulationRequest;
import ru.innopolis.attestations.attestation03.repository.CryptoCandleRepository;
import ru.innopolis.attestations.attestation03.service.simulation.PortfolioSimulator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Import({SimulationController.class})
@WebMvcTest(controllers = SimulationController.class)
public class SimulationControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PortfolioSimulator simulator;

    @MockBean
    private CryptoCandleRepository candleRepository;

    @Test
    void testBadRequestOnInvalidWeights() throws Exception {
        SimulationRequest request = new SimulationRequest();
        request.setStart(LocalDate.of(2025, 1, 1));
        request.setEnd(LocalDate.of(2025, 2, 1));
        request.setInitialUsd(BigDecimal.valueOf(1000));
        request.setWeights(Map.of(
                "ETHUSDT", BigDecimal.valueOf(0.3),
                "BTCUSDT", BigDecimal.valueOf(0.3)
        ));

        mockMvc.perform(post("/api/simulation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}