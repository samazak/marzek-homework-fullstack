package com.tradefinance.demo.repository.oracle;

import java.util.List;
import com.tradefinance.demo.dto.Invoice;
import com.tradefinance.demo.repository.InvoiceRepository;
import com.tradefinance.demo.repository.oracle.typehandler.IsoLocalDateTypeHandler;
import com.tradefinance.demo.repository.oracle.typehandler.PlainBigDecimalTypeHandler;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface InvoiceMapper extends InvoiceRepository {
    /**
     * sortColumn is interpolated with {@code ${}} (not bindable as a query param); callers must
     * resolve it against a fixed allow-list first, see InvoiceRepository.findUnpaidInvoices javadoc.
     */
    @Override
    @Select("""
            <script>
            SELECT invoice_id, invoice_date, currency, invoice_amount, paid_amount, outstanding_amount
            FROM unpaid_invoice
            ORDER BY ${sortColumn}
            <choose>
                <when test="descending">DESC</when>
                <otherwise>ASC</otherwise>
            </choose>, invoice_id
            OFFSET #{offset} ROWS FETCH NEXT #{limit} ROWS ONLY
            </script>
            """)
    @ConstructorArgs({
            @Arg(column = "invoice_id", javaType = String.class, typeHandler = PlainBigDecimalTypeHandler.class),
            @Arg(column = "invoice_date", javaType = String.class, typeHandler = IsoLocalDateTypeHandler.class),
            @Arg(column = "currency", javaType = String.class),
            @Arg(column = "invoice_amount", javaType = String.class, typeHandler = PlainBigDecimalTypeHandler.class),
            @Arg(column = "paid_amount", javaType = String.class, typeHandler = PlainBigDecimalTypeHandler.class),
            @Arg(column = "outstanding_amount", javaType = String.class, typeHandler = PlainBigDecimalTypeHandler.class)
    })
    List<Invoice> findUnpaidInvoices(@Param("offset") int offset, @Param("limit") int limit,
            @Param("sortColumn") String sortColumn, @Param("descending") boolean descending);
}
