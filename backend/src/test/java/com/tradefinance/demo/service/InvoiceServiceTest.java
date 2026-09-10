package com.tradefinance.demo.service;

import java.util.List;
import com.tradefinance.demo.dto.Invoice;
import com.tradefinance.demo.dto.InvoicePage;
import com.tradefinance.demo.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class InvoiceServiceTest {
    private final InvoiceRepository invoices = mock(InvoiceRepository.class);
    private InvoiceService service;

    @BeforeEach
    void setUp() {
        service = new InvoiceService(invoices);
    }

    @Test
    void usesLookAheadForOffsetWithoutDroppingRows() {
        var first = invoice("1");
        var second = invoice("2");
        when(invoices.findUnpaidInvoices(0, 2, "invoice_id", false)).thenReturn(List.of(first, second));

        var page = service.findUnpaidInvoices(0, 1, "invoiceId", "asc");

        assertThat(page.items()).containsExactly(first);
        assertThat(page.nextOffset()).isEqualTo(1);

        when(invoices.findUnpaidInvoices(1, 2, "invoice_id", false)).thenReturn(List.of(second));

        var nextPage = service.findUnpaidInvoices(1, 1, "invoiceId", "asc");

        assertThat(nextPage.nextOffset()).isNull();
    }

    @Test
    void emptyPageHasNoNextOffset() {
        when(invoices.findUnpaidInvoices(0, 26, "invoice_id", false)).thenReturn(List.of());

        var page = service.findUnpaidInvoices(0, 25, "invoiceId", "asc");

        assertThat(page).isEqualTo(new InvoicePage(List.of(), null));
    }

    @Test
    void rejectsUnboundedPagination() {
        assertThatThrownBy(() -> service.findUnpaidInvoices(-1, 25, "invoiceId", "asc")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.findUnpaidInvoices(0, 0, "invoiceId", "asc")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.findUnpaidInvoices(0, 101, "invoiceId", "asc")).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(invoices);
    }

    @Test
    void resolvesSortKeyToAllowlistedColumn() {
        when(invoices.findUnpaidInvoices(0, 26, "outstanding_amount", true)).thenReturn(List.of());

        service.findUnpaidInvoices(0, 25, "outstandingAmount", "desc");

        verify(invoices).findUnpaidInvoices(0, 26, "outstanding_amount", true);
    }

    @Test
    void rejectsUnknownSortKeyOrDirection() {
        assertThatThrownBy(() -> service.findUnpaidInvoices(0, 25, "notAColumn", "asc")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.findUnpaidInvoices(0, 25, "invoiceId", "sideways")).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(invoices);
    }

    private Invoice invoice(String id) {
        return new Invoice(id, "2026-01-10", "EUR", "100", "0", "100");
    }
}
