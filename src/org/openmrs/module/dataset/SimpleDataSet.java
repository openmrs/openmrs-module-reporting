package org.openmrs.module.dataset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * A DataSet backed by an ArrayList, with a HashMap for each row.
 */
public class SimpleDataSet implements DataSet<Object> {
    
    private DataSetDefinition dataSetDefinition;
    private EvaluationContext evaluationContext;
    private List<Map<DataSetColumn, Object>> data;
    
    // *************
    // CONSTRUCTORS
    // *************
    
    /**
     * Default Constructor
     */
    public SimpleDataSet() {
        data = new ArrayList<Map<DataSetColumn, Object>>();
    }
    
    /**
     * Default Constructor which creates an empty DataSet for the given definition and evaluationContext
     * @param definition
     * @param evaluationContext
     */
    public SimpleDataSet(DataSetDefinition definition, EvaluationContext evaluationContext) {
        this();
        this.dataSetDefinition = definition;
        this.evaluationContext = evaluationContext;
    }
    
    // *************
    // INSTANCE METHODS
    // *************
    
    /**
     * Adds a row to this DataSet
     * @param row the row to add to the DataSet
     */
    public void addRow(Map<DataSetColumn, Object> row) {
        getData().add(row);
    }

    /**
     * @see DataSet#iterator()
     */
    public Iterator<Map<DataSetColumn, Object>> iterator() {
        return getData().iterator();
    }
    
	/**
	 * Convenience method for JSTL method.  
	 * TODO This will be removed once we get a decent solution for the dataset iterator solution.  
	 */
	public Iterator<Map<DataSetColumn, Object>> getIterator() {
		return iterator();
	}
    
    
    // *************
    // PROPERTY ACCESS
    // *************
    
    /**
	 * @return the dataSetDefinition
	 */
	public DataSetDefinition getDataSetDefinition() {
		return dataSetDefinition;
	}

	/**
	 * @param dataSetDefinition the dataSetDefinition to set
	 */
	public void setDataSetDefinition(DataSetDefinition dataSetDefinition) {
		this.dataSetDefinition = dataSetDefinition;
	}

	/**
	 * @return the evaluationContext
	 */
	public EvaluationContext getEvaluationContext() {
		return evaluationContext;
	}
	
    /**
     * @param evaluationContext the evaluationContext to set
     */
    public void setEvaluationContext(EvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    /**
     * @return the data
     */
    public List<Map<DataSetColumn, Object>> getData() {
    	if (data == null) {
    		data = new ArrayList<Map<DataSetColumn, Object>>();
    	}
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<Map<DataSetColumn, Object>> data) {
        this.data = data;
    }
}
