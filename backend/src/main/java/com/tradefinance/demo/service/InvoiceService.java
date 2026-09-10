package com.tradefinance.demo.service;

import java.util.Locale;
import java.util.Map;
import com.tradefinance.demo.config.TransactionTimeouts;
import com.tradefinance.demo.dto.InvoicePage;
import com.tradefinance.demo.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private static final Map<String, String> INVOICE_SORT_COLUMNS = Map.of(
            "invoiceId", "invoice_id",
            "invoiceDate", "invoice_date",
            "currency", "currency",
            "amount", "invoice_amount",
            "paidAmount", "paid_amount",
            "outstandingAmount", "outstanding_amount");

    private static final int MAX_INVOICE_PAGE_SIZE = 100;

    private final InvoiceRepository invoices;

    @Transactional(readOnly = true, timeout = TransactionTimeouts.DEFAULT_SECONDS)
    public InvoicePage findUnpaidInvoices(int offset, int limit, String sortBy, String sortDir) {
        if (offset < 0 || limit < 1 || limit > MAX_INVOICE_PAGE_SIZE) {
            throw new IllegalArgumentException("Offset must be nonnegative; limit must be 1–100.");
        }
        String column = INVOICE_SORT_COLUMNS.get(sortBy);
        if (column == null) {
            throw new IllegalArgumentException("Unknown sort column: " + sortBy);
        }
        boolean descending = switch (sortDir == null ? "" : sortDir.toLowerCase(Locale.ROOT)) {
            case "asc" -> false;
            case "desc" -> true;
            default -> throw new IllegalArgumentException("Sort direction must be 'asc' or 'desc'.");
        };
        var rows = invoices.findUnpaidInvoices(offset, limit + 1, column, descending);
        boolean more = rows.size() > limit;
        var page = more ? rows.subList(0, limit) : rows;
        return new InvoicePage(page, more ? offset + limit : null);
    }
}
