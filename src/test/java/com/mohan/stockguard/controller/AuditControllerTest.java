package com.mohan.stockguard.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuditController.class)
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void auditEndpointReturnsTrackingStatus() throws Exception {
        mockMvc.perform(get("/api/audit"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.module").value("orders"))
            .andExpect(jsonPath("$.status").value("tracked"));
    }
}
