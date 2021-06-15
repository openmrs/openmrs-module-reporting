package org.openmrs.module.reporting.common;

import org.openmrs.api.APIException;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.openmrs.module.reporting.dataset.definition.evaluator.SqlFileDataSetEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlIterator implements Iterator<DataSetRow> {
    private ResultSet resultSet;
    private List<DataSetColumn> columns;
    private Statement statement;
    private SqlFileDataSetEvaluator evaluator;
    private static final Logger log = LoggerFactory.getLogger(SqlIterator.class);


    public SqlIterator(ResultSetMetaData metadata, ResultSet resultSet, SqlFileDataSetEvaluator evaluator, Statement statement) throws SQLException {
        this.resultSet = resultSet;
        this.evaluator = evaluator;
        this.statement = statement;
        this.createDataSetColumns(metadata);
    }

    @Override
    public boolean hasNext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataSetRow next() throws NoSuchElementException {
        try {
            if (resultSet.next()) {
                return createDataSetRow();
            } else {
                closeConnection();
                return null;
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
            evaluator.closeConnection();
        } catch (Exception ex) {
            log.error("Failed to close SqlIterator connection.", ex);
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
