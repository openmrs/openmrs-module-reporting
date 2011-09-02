package org.openmrs.module.reporting.dataset;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.openmrs.module.reporting.dataset.definition.RowPerObjectDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * A DataSet which represents one Row per OpenmrsData object
 * For example, row-per-patient, row-per-encounter, row-per-obs
 * Each row is a Map from object id to DataSetRow
 */
public class RowPerObjectDataSet implements DataSet {
    
    private RowPerObjectDataSetDefinition<?> definition;
    private EvaluationContext context;
    private SimpleDataSetMetaData metaData = new SimpleDataSetMetaData();
    private Map<Integer, DataSetRow> rows;
    
    // *************
    // CONSTRUCTORS
    // *************
    
    /**
     * Default Constructor which creates an empty DataSet for the given definition and evaluationContext
     * @param definition
     * @param evaluationContext
     */
    public RowPerObjectDataSet(RowPerObjectDataSetDefinition<?> definition, EvaluationContext evaluationContext) {
        this.definition = definition;
        this.context = evaluationContext;
    }
    
    // *************
    // INSTANCE METHODS
    // *************
    
    /**
     * Adds a Column value to this DataSet
     * @param id the id of the object
     * @param columnName the name of the column
     * @param columnValue the value to add
     */
    public void addColumnValue(Integer id, DataSetColumn column, Object columnValue) {
    	DataSetRow row = getRows().get(id);
    	if (row == null) {
    		row = new DataSetRow();
    		getRows().put(id, row);
    	}
    	row.addColumnValue(column, columnValue);
    }
    
    /**
     * Gets a Column value from this DataSet
     * @param id the id of the object
     * @param columnName the name of the column
     */
    public Object getColumnValue(Integer id, String columnName) {
    	DataSetRow row = getRows().get(id);
    	if (row != null) {
    		return row.getColumnValue(columnName);
    	}
    	return null;
    }

    /**
     * @see DataSet#iterator()
     */
    public Iterator<DataSetRow> iterator() {
        return getRows().values().iterator();
    }
    
    // *************
    // PROPERTY ACCESS
    // *************
	
	/**
	 * @return the definition
	 */
	public RowPerObjectDataSetDefinition<?> getDefinition() {
		return definition;
	}

	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(RowPerObjectDataSetDefinition<?> definition) {
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
	 * @return the rows
	 */
	public Map<Integer, DataSetRow> getRows() {
		if (rows == null) {
			rows = new TreeMap<Integer, DataSetRow>();
		}
		return rows;
	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(Map<Integer, DataSetRow> rows) {
		this.rows = rows;
	}
}
