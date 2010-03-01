package org.openmrs.module.reporting.dataset.definition;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.column.DataSetColumn;

/**
 * This DataSetDefinition wraps a DataSet that already exists.
 * This is useful for testing purposes. It may or may not be useful in real life.
 */
public class DataSetWrappingDataSetDefinition extends BaseDataSetDefinition {

    private static final long serialVersionUID = 1L;
    
    // ***************
    // PROPERTIES
    // ***************
    
    private DataSet data;
    
    // ***************
    // CONSTRUCTORS
    // ***************
    
    /**
     * Default Constructor
     */
    public DataSetWrappingDataSetDefinition() { }
    
    /**
     * Full Constructor
     * @param data
     */
    public DataSetWrappingDataSetDefinition(DataSet data) {
        this.data = data;
    }
    
    // ***************
    // INSTANCE METHODS
    // ***************
    
    /**
     * Infers columns from the first row in the underlying dataset
     * @see DataSetDefinition#getColumns()
     */
    public List<DataSetColumn> getColumns() {
    	DataSetRow r = data.iterator().next();
    	return new ArrayList<DataSetColumn>(r.getColumnValues().keySet());
    }
    
    // ***************
    // PROPERTY ACCESS
    // ***************
    
    /**
     * @return the data
     */
    public DataSet getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(DataSet data) {
        this.data = data;
    }
}
