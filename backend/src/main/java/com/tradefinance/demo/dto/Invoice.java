package com.tradefinance.demo.dto;

public record Invoice(String invoiceId, String invoiceDate, String currency,
                       String amount, String paidAmount, String outstandingAmount) {}
