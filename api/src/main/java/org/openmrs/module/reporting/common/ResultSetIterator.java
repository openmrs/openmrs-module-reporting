package org.openmrs.module.reporting.common;

import org.openmrs.api.APIException;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class ResultSetIterator implements Iterator<DataSetRow> {

    private static final Logger log = LoggerFactory.getLogger(ResultSetIterator.class);

    private ResultSet resultSet;
    private List<DataSetColumn> columns;
    private Connection connection;
    private Statement statement;
    private boolean isNextUsed = true;
    private boolean hasNext = true;

    public ResultSetIterator(ResultSet resultSet) throws SQLException {
        this.resultSet = resultSet;
        this.statement = resultSet.getStatement();
        this.connection = resultSet.getStatement().getConnection();
        this.createDataSetColumns(resultSet.getMetaData());
    }

    @Override
    public boolean hasNext() {
        if (isNextUsed && hasNext) {
            isNextUsed = false;
            return rawNext();
        }
        return hasNext;
    }

    @Override
    public DataSetRow next() throws NoSuchElementException {
        try {
            if (isNextUsed) {
                if (rawNext()) {
                    return createDataSetRow();
                }else{
                    return null;
                }
            } else {
                isNextUsed = true;
                return createDataSetRow();
            }
        } catch (SQLException e) {
            closeConnection();
            throw new APIException("Failed to fetch the next result from the database.", e);
        }
    }

    private boolean rawNext() {
        try {
            if (resultSet.next()) {
                return true;
            } else {
                hasNext = false;
                closeConnection();
                return false;
            }
        } catch (SQLException e) {
            closeConnection();
            throw new APIException("Failed to fetch the next result from the database.", e);
        }
    }

    @Override
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

    public void closeConnection() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception ex) {
            log.error("Failed to close ResultSetIterator connection.", ex);
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
