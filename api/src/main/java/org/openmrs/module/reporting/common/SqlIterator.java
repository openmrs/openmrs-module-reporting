package org.openmrs.module.reporting.common;

import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SqlIterator implements Iterator<DataSetRow> {
    private ResultSet resultSet;
    private List<DataSetColumn> columns;
    private Statement statement;

    public SqlIterator(ResultSetMetaData metadata, ResultSet resultSet, Statement statement) throws SQLException {
        this.resultSet = resultSet;
        this.statement = statement;
        this.createDataSetColumns(metadata);
    }

    public boolean hasNext() {
        //TODO
        return true;
    }

    public DataSetRow next() throws IllegalArgumentException {
        try {
            if (resultSet.next()) {
                return createDataSetRow();
            } else {
                closeStatement();
                return null;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to execute query", e);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private DataSetRow createDataSetRow() throws SQLException {
        DataSetRow dataSetRow = new DataSetRow();
        for (int i = 0; i < columns.size(); i++) {
            dataSetRow.addColumnValue(columns.get(i), resultSet.getObject(i + 1));
        }
        return dataSetRow;
    }

    private void closeStatement() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (Exception e) {
        }
    }

    public List<DataSetColumn> getColumns() {
        return columns;
    }

    private void createDataSetColumns(ResultSetMetaData metadata) throws SQLException {
        columns = new LinkedList<DataSetColumn>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String columnName = metadata.getColumnLabel(i);
            columns.add(new DataSetColumn(columnName, columnName, Object.class));
        }
    }
}
