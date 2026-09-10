package com.tradefinance.demo.controller;

import com.tradefinance.demo.support.ControllerIT;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InvoiceControllerIT extends ControllerIT {

    @Test
    void unauthenticatedRequestIsRejectedByRealSecurityChain() throws Exception {
        var request = mvc.perform(get(InvoiceController.PATH));

        request.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void returnsUnpaidInvoicesThroughFullStack() throws Exception {
        var request = mvc.perform(get(InvoiceController.PATH));

        request.andExpect(status().isOk()).andExpect(jsonPath("$.items[0].invoiceId").value("1"));
    }

    @Test
    @WithMockUser
    void invalidSortColumnIsBadRequest() throws Exception {
        var request = mvc.perform(get(InvoiceController.PATH).param("sortBy", "bogus"));

        request.andExpect(status().isBadRequest());
    }
}
