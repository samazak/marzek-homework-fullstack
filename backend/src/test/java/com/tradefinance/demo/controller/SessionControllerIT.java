package com.tradefinance.demo.controller;

import com.tradefinance.demo.support.ControllerIT;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessionControllerIT extends ControllerIT {

    @Test
    void sessionReportsUnauthenticated() throws Exception {
        var request = mvc.perform(get(SessionController.SESSION_PATH));

        request.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void sessionReportsAuthenticated() throws Exception {
        var request = mvc.perform(get(SessionController.SESSION_PATH));

        request.andExpect(status().isOk());
    }
}
