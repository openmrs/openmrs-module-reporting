package org.openmrs.module.dataset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * A DataSet backed by an ArrayList, with a HashMap for each row.
 */
public class SimpleDataSet implements DataSet {
    
    private DataSetDefinition definition;
    private EvaluationContext context;
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
     * Adds a row to this DataSet
     * @param row the row to add to the DataSet
     */
    public void addRow(DataSetRow row) {
        getRows().add(row);
    }

    /**
     * @see DataSet#iterator()
     */
    public Iterator<DataSetRow> iterator() {
        return getRows().iterator();
    }
    
	/**
	 * Convenience method for JSTL method.  
	 * TODO This will be removed once we get a decent solution for the dataset iterator solution.  
	 */
	public Iterator<DataSetRow> getIterator() {
		return iterator();
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
