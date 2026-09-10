package com.tradefinance.demo.controller;

import com.tradefinance.demo.support.ControllerIT;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PiControllerIT extends ControllerIT {

    @Test
    void unauthenticatedRequestIsRejectedByRealSecurityChain() throws Exception {
        var request = mvc.perform(post(PiController.PATH).with(csrf())
            .contentType("application/json").content("{\"precision\":\"0.01\"}"));

        request.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void calculatesPiThroughFullStack() throws Exception {
        var request = mvc.perform(post(PiController.PATH).with(csrf())
            .contentType("application/json").content("{\"precision\":\"0.000001\"}"));

        request.andExpect(status().isOk())
            .andExpect(jsonPath("$.value").value(startsWith("3.14159")))
            .andExpect(jsonPath("$.steps").isNotEmpty());
    }
}
