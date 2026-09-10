package com.tradefinance.demo.controller;

import com.tradefinance.demo.support.ControllerIT;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AgeControllerIT extends ControllerIT {

    @Test
    void unauthenticatedRequestIsRejectedByRealSecurityChain() throws Exception {
        var request = mvc.perform(get(AgeController.PATH).param("age", "55"));

        request.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void describesAgeThroughFullStack() throws Exception {
        var request = mvc.perform(get(AgeController.PATH).param("age", "55"));

        request.andExpect(status().isOk()).andExpect(jsonPath("$.text").value("You are aged"));
    }

    @Test
    @WithMockUser
    void malformedAgeIsBadRequest() throws Exception {
        var request = mvc.perform(get(AgeController.PATH).param("age", "banana"));

        request.andExpect(status().isBadRequest());
    }
}
