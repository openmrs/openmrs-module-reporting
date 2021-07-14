package org.openmrs.module.reporting.common;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.evaluator.SqlFileDataSetEvaluator;

import javax.sql.rowset.RowSetMetaDataImpl;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SqlIteratorTest {

    //@Mock
    private SqlFileDataSetEvaluator mockEvaluator;

    //@Mock
    private ResultSet mockResultSet;

    //@Mock
    private Statement mockStatement;

    private SqlIterator sqlIterator;

    @Before
    public void setUp() throws SQLException {
        mockEvaluator = mock(SqlFileDataSetEvaluator.class);
        mockResultSet = mock(ResultSet.class);
        mockStatement = mock(Statement.class);
        sqlIterator = new SqlIterator(createMetadata(), mockResultSet, mockEvaluator, mockStatement);
        mockResultSet();
    }

    @Test
    public void shouldRead3LinesWithHasNext() {
        List<DataSetRow> rows = new LinkedList<DataSetRow>();
        for (Iterator i = sqlIterator; i.hasNext(); ) {
            DataSetRow row = (DataSetRow) i.next();
            rows.add(row);
        }
        Assert.assertEquals(3, rows.size());
        Assert.assertEquals("result_01", rows.get(0).getColumnValue("Column_01"));
        Assert.assertEquals("result_02", rows.get(0).getColumnValue("Column_02"));
        Assert.assertEquals("result_03", rows.get(1).getColumnValue("Column_01"));
        Assert.assertEquals("result_04", rows.get(1).getColumnValue("Column_02"));
        Assert.assertEquals("result_05", rows.get(2).getColumnValue("Column_01"));
        Assert.assertEquals("result_06", rows.get(2).getColumnValue("Column_02"));
    }

    @Test
    public void shouldRead3LinesWithoutHasNext() {
        List<DataSetRow> rows = new LinkedList<DataSetRow>();
        DataSetRow row = sqlIterator.next();
        while (row != null) {
            rows.add(row);
            row = sqlIterator.next();
        }

        Assert.assertEquals(3, rows.size());
        Assert.assertEquals("result_01", rows.get(0).getColumnValue("Column_01"));
        Assert.assertEquals("result_02", rows.get(0).getColumnValue("Column_02"));
        Assert.assertEquals("result_03", rows.get(1).getColumnValue("Column_01"));
        Assert.assertEquals("result_04", rows.get(1).getColumnValue("Column_02"));
        Assert.assertEquals("result_05", rows.get(2).getColumnValue("Column_01"));
        Assert.assertEquals("result_06", rows.get(2).getColumnValue("Column_02"));
    }

    private ResultSetMetaData createMetadata() throws SQLException {
        RowSetMetaDataImpl metaData = new RowSetMetaDataImpl();
        metaData.setColumnCount(2);
        metaData.setColumnLabel(1, "Column_01");
        metaData.setColumnLabel(2, "Column_02");
        return metaData;
    }

    private void mockResultSet() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getObject(1)).thenReturn("result_01", "result_03", "result_05");
        when(mockResultSet.getObject(2)).thenReturn("result_02", "result_04", "result_06");
    }
}
