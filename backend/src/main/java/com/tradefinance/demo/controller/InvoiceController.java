package com.tradefinance.demo.controller;

import com.tradefinance.demo.dto.InvoicePage;
import com.tradefinance.demo.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InvoiceController {
    public static final String PATH = "/api/invoices/unpaid";

    private final InvoiceService service;

    @GetMapping(PATH)
    public InvoicePage listUnpaidInvoices(@RequestParam(defaultValue = "0") int offset,
                                          @RequestParam(defaultValue = "25") int limit,
                                          @RequestParam(defaultValue = "invoiceId") String sortBy,
                                          @RequestParam(defaultValue = "asc") String sortDir) {
        return service.findUnpaidInvoices(offset, limit, sortBy, sortDir);
    }
}
