package com.tradefinance.demo.repository.oracle.typehandler;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.MappedTypes;

/** Renders a NUMBER column as {@link BigDecimal#toPlainString()}, never scientific notation. */
@MappedTypes(String.class)
public class PlainBigDecimalTypeHandler extends AbstractStringTypeHandler<BigDecimal> {
    @Override
    protected void setRaw(PreparedStatement ps, int columnIndex, BigDecimal value) throws SQLException {
        ps.setBigDecimal(columnIndex, value);
    }

    @Override
    protected BigDecimal getRaw(ResultSet rs, String columnName) throws SQLException {
        return rs.getBigDecimal(columnName);
    }

    @Override
    protected BigDecimal getRaw(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getBigDecimal(columnIndex);
    }

    @Override
    protected BigDecimal getRaw(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getBigDecimal(columnIndex);
    }

    @Override
    protected BigDecimal parse(String value) {
        return new BigDecimal(value);
    }

    @Override
    protected String render(BigDecimal value) {
        return value.toPlainString();
    }
}
