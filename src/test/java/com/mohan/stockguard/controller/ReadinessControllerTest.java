package com.mohan.stockguard.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReadinessController.class)
class ReadinessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void readinessEndpointReturnsReadyStatus() throws Exception {
        mockMvc.perform(get("/api/ready"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("READY"));
    }
}
