package com.tradefinance.demo.repository.oracle.typehandler;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import org.apache.ibatis.type.MappedTypes;

/** Renders a DATE column as {@code LocalDate.toString()} (ISO-8601, no time part). */
@MappedTypes(String.class)
public class IsoLocalDateTypeHandler extends AbstractStringTypeHandler<LocalDate> {
    @Override
    protected void setRaw(PreparedStatement ps, int columnIndex, LocalDate value) throws SQLException {
        ps.setDate(columnIndex, Date.valueOf(value));
    }

    @Override
    protected LocalDate getRaw(ResultSet rs, String columnName) throws SQLException {
        var value = rs.getDate(columnName);
        return value == null ? null : value.toLocalDate();
    }

    @Override
    protected LocalDate getRaw(ResultSet rs, int columnIndex) throws SQLException {
        var value = rs.getDate(columnIndex);
        return value == null ? null : value.toLocalDate();
    }

    @Override
    protected LocalDate getRaw(CallableStatement cs, int columnIndex) throws SQLException {
        var value = cs.getDate(columnIndex);
        return value == null ? null : value.toLocalDate();
    }

    @Override
    protected LocalDate parse(String value) {
        return LocalDate.parse(value);
    }

    @Override
    protected String render(LocalDate value) {
        return value.toString();
    }
}
