package org.openmrs.module.reporting.evaluation.querybuilder;

import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationProfiler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class ResultSetIterator implements Iterator<DataSetRow> {
    private ResultSet resultSet;
    private List<DataSetColumn> columns;
    private PreparedStatement statement;
    private EvaluationProfiler profiler;

    public ResultSetIterator(List<DataSetColumn> columns, ResultSet resultSet, PreparedStatement statement, EvaluationContext context) {
        this.columns = columns;
        this.resultSet = resultSet;
        this.statement = statement;
        this.profiler =  new EvaluationProfiler(context);
    }

    public boolean hasNext() {
        throw new UnsupportedOperationException();
    }

    public DataSetRow next() throws IllegalArgumentException{
        try {
            if (resultSet.next()){
                return createDataSetRow();
            }else{
                closeStatement();
                return null;
            }
        } catch (Exception e) {
            profiler.logError("EXECUTING_QUERY", toString(), e);
            throw new IllegalArgumentException("Unable to execute query", e);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private DataSetRow createDataSetRow() throws SQLException {
        DataSetRow dataSetRow = new DataSetRow();
        for (int i=0; i<columns.size(); i++) {
            dataSetRow.addColumnValue(columns.get(i), resultSet.getObject(i+1));
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
}
