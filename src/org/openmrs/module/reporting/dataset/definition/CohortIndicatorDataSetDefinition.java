package org.openmrs.module.reporting.dataset.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.util.OpenmrsUtil;

/**
 * <pre>
 * This represents a data set where each column is an Indicator plus (optionally) dimension options
 * 
 * For example:
 *   1.a. Enrolled in HIV Program at start of Period (gender=Female, age=Adult)
 *   1.b. Enrolled in HIV Program at start of Period (gender=Male, age=Adult)
 *   2.a. Newly Enrolled in HIV Program during Period (gender=Female, age=Adult)
 *   2.b. Newly Enrolled in HIV Program during Period (gender=Male, age=Adult)
 * </pre>
 */
public class CohortIndicatorDataSetDefinition extends BaseDataSetDefinition {
	
	Map<String, Mapped<CohortDefinitionDimension>> dimensions = new HashMap<String, Mapped<CohortDefinitionDimension>>();
	List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
	
	public CohortIndicatorDataSetDefinition() {
		super();
	}
	
	public List<DataSetColumn> getColumns() {
		return new ArrayList<DataSetColumn>(columns);
	}
	
	public Map<String, Mapped<CohortDefinitionDimension>> getDimensions() {
		return dimensions;
	}
	
	public void addDimension(String dimensionKey, Mapped<CohortDefinitionDimension> dimension) {
		dimensions.put(dimensionKey, dimension);
	}
	
	public void addDimension(String dimensionKey, CohortDefinitionDimension dimension, Map<String, Object> parameterMappings) {
		addDimension(dimensionKey, new Mapped<CohortDefinitionDimension>(dimension, parameterMappings));
	}
	
	public void removeDimension(String dimensionKey) {
		dimensions.remove(dimensionKey);
	}
	
	public void addColumn(String key, String displayName, Mapped<? extends CohortIndicator> indicator, Map<String, String> dimensionOptions) {
		columns.add(new ColumnDefinition(key, displayName, indicator, dimensionOptions));
	}
	
	/**
	 * Removes a column by its key
	 * 
	 * @param key
	 */
	public void removeColumn(String key) {
		for (Iterator<ColumnDefinition> i = columns.iterator(); i.hasNext(); ) {
			if (i.next().getColumnKey().equals(key)) {
				i.remove();
			}
		}
	}
	
	/**
	 * 
	 * Auto generated method comment
	 * 
	 * @param key
	 * @param displayName
	 * @param indicator
	 * @param dimensionOptions something like gender=male|age=adult, where gender and age are keys into 'dimensions'
	 */
	public void addColumn(String key, String displayName, Mapped<? extends CohortIndicator> indicator, String dimensionOptions) {
		addColumn(key, displayName, indicator, OpenmrsUtil.parseParameterList(dimensionOptions));
	}
	
	public Mapped<CohortDefinitionDimension> getDimension(String key) {
	    return dimensions.get(key);
    }
	
    /**
     * @param dimensions the dimensions to set
     */
    public void setDimensions(Map<String, Mapped<CohortDefinitionDimension>> dimensions) {
    	this.dimensions = dimensions;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(List<ColumnDefinition> columns) {
    	this.columns = columns;
    }

	public class ColumnDefinition extends DataSetColumn {

        private static final long serialVersionUID = 1L;
        
		private Mapped<? extends CohortIndicator> indicator;
		private Map<String, String> dimensionOptions;
		
		public ColumnDefinition(String columnKey, String displayName, Mapped<? extends CohortIndicator> indicator, Map<String, String> dimensionOptions) {
			super(columnKey, displayName, null, Object.class);
			this.indicator = indicator;
			this.dimensionOptions = dimensionOptions;
		}

        /**
         * @return the indicator
         */
        public Mapped<? extends CohortIndicator> getIndicator() {
        	return indicator;
        }
		
        /**
         * @return the dimensionOptions
         */
        public Map<String, String> getDimensionOptions() {
        	return dimensionOptions;
        }
        		
	}
	
}
