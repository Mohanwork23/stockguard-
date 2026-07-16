package com.mohan.stockguard.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatusController.class)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void statusEndpointReturnsRunningState() throws Exception {
        mockMvc.perform(get("/api/status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.app").value("stockguard"))
            .andExpect(jsonPath("$.state").value("RUNNING"));
    }
}
