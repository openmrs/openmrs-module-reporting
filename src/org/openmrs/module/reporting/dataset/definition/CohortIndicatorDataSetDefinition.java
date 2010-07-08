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
	
	//***** PROPERTIES *****
	
	Map<String, Mapped<CohortDefinitionDimension>> dimensions = new HashMap<String, Mapped<CohortDefinitionDimension>>();
	List<CohortIndicatorAndDimensionColumn> columns = new ArrayList<CohortIndicatorAndDimensionColumn>();
	
	//***** CONSTRUCTORS *****
	
	public CohortIndicatorDataSetDefinition() {
		super();
	}

	//***** PROPERTY ACCESS AND INSTANCE METHODS
	
    /**
	 * @return the dimensions
	 */
	public Map<String, Mapped<CohortDefinitionDimension>> getDimensions() {
		return dimensions;
	}
	
    /**
     * @param dimensions the dimensions to set
     */
    public void setDimensions(Map<String, Mapped<CohortDefinitionDimension>> dimensions) {
    	this.dimensions = dimensions;
    }
    
	/**
	 * Adds a Mapped<Dimension> referenced by the given key
	 */
	public void addDimension(String dimensionKey, Mapped<CohortDefinitionDimension> dimension) {
		dimensions.put(dimensionKey, dimension);
	}
	
	/**
	 * Adds a Dimension referenced by the given key, dimension, and parameter mappings
	 */
	public void addDimension(String dimensionKey, CohortDefinitionDimension dimension, Map<String, Object> parameterMappings) {
		addDimension(dimensionKey, new Mapped<CohortDefinitionDimension>(dimension, parameterMappings));
	}
	
	/**
	 * @return the Mapped<CohortDefinitionDimension> with the given key
	 */
	public Mapped<CohortDefinitionDimension> getDimension(String key) {
	    return dimensions.get(key);
    }
	
	/**
	 * Removes a Dimension with the given key
	 */
	public void removeDimension(String dimensionKey) {
		List<CohortIndicatorAndDimensionColumn> listToRemove = new ArrayList<CohortIndicatorAndDimensionColumn>();
		for(CohortIndicatorAndDimensionColumn c : columns) {
			Map<String, String> dimOpts = c.getDimensionOptions();
			if (dimOpts.keySet().contains(dimensionKey)) {
				listToRemove.add(c);
			}
		}
		columns.removeAll(listToRemove);
		dimensions.remove(dimensionKey);
	}

    /**
	 * @return the columns
	 */
	public List<CohortIndicatorAndDimensionColumn> getColumns() {
		return columns;
	}

	/**
     * @param columns the columns to set
     */
    public void setColumns(List<CohortIndicatorAndDimensionColumn> columns) {
    	this.columns = columns;
    }
    
    /**
     * Adds a Column with the given properties
     */
	public void addColumn(String name, String label, Mapped<? extends CohortIndicator> indicator, Map<String, String> dimensionOptions) {
		columns.add(new CohortIndicatorAndDimensionColumn(name, label, indicator, dimensionOptions));
	}
	
    /**
     * Removes a column with the given name
     */
	public void removeColumn(String columnName) {
		for (Iterator<CohortIndicatorAndDimensionColumn> i = columns.iterator(); i.hasNext(); ) {
			if (i.next().getName().equals(columnName)) {
				i.remove();
			}
		}
	}
	
	/**
	 * Adds a Column with the given properties
	 * @param dimensionOptions something like gender=male|age=adult, where gender and age are keys into 'dimensions'
	 */
	public void addColumn(String name, String label, Mapped<? extends CohortIndicator> indicator, String dimensionOptions) {
		addColumn(name, label, indicator, OpenmrsUtil.parseParameterList(dimensionOptions));
	}
    
    //***** INNER CLASSES *****

    /**
     * Column Definition which encapsulates information about the indicator and dimensions chosen for each column
     */
	public class CohortIndicatorAndDimensionColumn extends DataSetColumn {

        private static final long serialVersionUID = 1L;
        
        //***** PROPERTIES *****
        
		private Mapped<? extends CohortIndicator> indicator;
		private Map<String, String> dimensionOptions;
		
		//***** CONSTRUCTORS *****
		
		public CohortIndicatorAndDimensionColumn(String name, String label, Mapped<? extends CohortIndicator> indicator, Map<String, String> dimensionOptions) {
			super(name, label, Object.class);
			this.indicator = indicator;
			this.dimensionOptions = dimensionOptions;
		}
		
		//***** PROPERTY ACCESS *****

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
