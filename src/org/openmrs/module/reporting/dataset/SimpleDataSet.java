package org.openmrs.module.reporting.dataset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * A basic implementation of a DataSet.
 */
public class SimpleDataSet implements DataSet {
    
    private DataSetDefinition definition;
    private EvaluationContext context;
    private SimpleDataSetMetaData metaData = new SimpleDataSetMetaData();
    private List<DataSetRow> rows;
    
    // *************
    // CONSTRUCTORS
    // *************
    
    /**
     * Default Constructor which creates an empty DataSet for the given definition and evaluationContext
     * @param definition
     * @param evaluationContext
     */
    public SimpleDataSet(DataSetDefinition definition, EvaluationContext evaluationContext) {
        this.definition = definition;
        this.context = evaluationContext;
    }
    
    // *************
    // INSTANCE METHODS
    // *************
    
    /**
     * Adds a row to this DataSet.  Also ensures all the Columns are added to the metadata
     * @param row the row to add to the DataSet
     */
    public void addRow(DataSetRow row) {
        getRows().add(row);
        for (DataSetColumn c : row.getColumnValues().keySet()) {
        	if (getMetaData().getColumn(c.getName()) == null) {
        		getMetaData().addColumn(c);
        	}
        }
    }

    /**
     * @see DataSet#iterator()
     */
    public Iterator<DataSetRow> iterator() {
        return getRows().iterator();
    }

    // *************
    // PROPERTY ACCESS
    // *************

	/**
	 * @return the definition
	 */
	public DataSetDefinition getDefinition() {
		return definition;
	}

	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(DataSetDefinition definition) {
		this.definition = definition;
	}

	/**
	 * @return the context
	 */
	public EvaluationContext getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(EvaluationContext context) {
		this.context = context;
	}

	/**
	 * @return the metaData
	 */
	public SimpleDataSetMetaData getMetaData() {
		return metaData;
	}

	/**
	 * @param metaData the metaData to set
	 */
	public void setMetaData(SimpleDataSetMetaData metaData) {
		this.metaData = metaData;
	}

	/**
     * @return the data
     */
    public List<DataSetRow> getRows() {
    	if (rows == null) {
    		rows = new ArrayList<DataSetRow>();
    	}
        return rows;
    }

	/**
     * @param data the data to set
     */
    public void setRows(List<DataSetRow> rows) {
        this.rows = rows;
    }
}
