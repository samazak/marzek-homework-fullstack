package com.tradefinance.demo.repository.oracle.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/** Bridges a JDBC column type {@code R} to the String representation MyBatis maps DTOs through. */
public abstract class AbstractStringTypeHandler<R> extends BaseTypeHandler<String> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int columnIndex, String parameter, JdbcType jdbcType)
            throws SQLException {
        setRaw(ps, columnIndex, parse(parameter));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        var value = getRaw(rs, columnName);
        return value == null ? null : render(value);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        var value = getRaw(rs, columnIndex);
        return value == null ? null : render(value);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        var value = getRaw(cs, columnIndex);
        return value == null ? null : render(value);
    }

    protected abstract void setRaw(PreparedStatement ps, int columnIndex, R value) throws SQLException;

    protected abstract R getRaw(ResultSet rs, String columnName) throws SQLException;

    protected abstract R getRaw(ResultSet rs, int columnIndex) throws SQLException;

    protected abstract R getRaw(CallableStatement cs, int columnIndex) throws SQLException;

    protected abstract R parse(String value);

    protected abstract String render(R value);
}
