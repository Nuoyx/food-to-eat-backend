package com.foodtoeat.mapper.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Reads SQL arrays assembled from the normalized attribute tables. */
public class StringArrayListTypeHandler extends BaseTypeHandler<List<String>> {
    @Override
    public void setNonNullParameter(PreparedStatement statement, int index,
                                    List<String> values, JdbcType jdbcType) throws SQLException {
        statement.setArray(index, statement.getConnection()
                .createArrayOf("text", values.toArray(new String[0])));
    }

    @Override
    public List<String> getNullableResult(ResultSet result, String column) throws SQLException {
        return readArray(result.getArray(column));
    }

    @Override
    public List<String> getNullableResult(ResultSet result, int column) throws SQLException {
        return readArray(result.getArray(column));
    }

    @Override
    public List<String> getNullableResult(CallableStatement result, int column) throws SQLException {
        return readArray(result.getArray(column));
    }

    private List<String> readArray(Array array) throws SQLException {
        List<String> values = new ArrayList<>();
        if (array == null) {
            return values;
        }
        try {
            for (Object value : (Object[]) array.getArray()) {
                values.add((String) value);
            }
            return values;
        } finally {
            array.free();
        }
    }
}
