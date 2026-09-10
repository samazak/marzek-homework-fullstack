package com.tradefinance.demo.repository;

import java.util.List;
import com.tradefinance.demo.dto.Invoice;

public interface InvoiceRepository {
    /**
     * {@code sortColumn} must already be resolved against a fixed allow-list by the caller;
     * implementations may interpolate it directly into SQL.
     */
    List<Invoice> findUnpaidInvoices(int offset, int limit, String sortColumn, boolean descending);
}
