package com.mohan.stockguard.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InfoController.class)
class InfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void infoEndpointReturnsServiceMetadata() throws Exception {
        mockMvc.perform(get("/api/info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.service").value("stockguard"))
            .andExpect(jsonPath("$.version").value("1.0"));
    }
}
