package com.tradefinance.demo.dto;

import java.util.List;

public record InvoicePage(List<Invoice> items, Integer nextOffset) {
    public InvoicePage { items = List.copyOf(items); }
}
