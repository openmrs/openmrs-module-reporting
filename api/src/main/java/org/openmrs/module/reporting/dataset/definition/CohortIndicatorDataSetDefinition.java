package org.openmrs.module.reporting.dataset.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
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
@Localized("reporting.CohortIndicatorDataSetDefinition")
public class CohortIndicatorDataSetDefinition extends BaseDataSetDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	Map<String, Mapped<CohortDefinitionDimension>> dimensions = new HashMap<String, Mapped<CohortDefinitionDimension>>();
	
	@ConfigurationProperty
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
		if (dimensions == null) {
			dimensions = new LinkedHashMap<String, Mapped<CohortDefinitionDimension>>();
		}
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
		getDimensions().put(dimensionKey, dimension);
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
	    return getDimensions().get(key);
    }
	
	/**
	 * Removes a Dimension with the given key
	 */
	public void removeDimension(String dimensionKey) {
		List<CohortIndicatorAndDimensionColumn> listToRemove = new ArrayList<CohortIndicatorAndDimensionColumn>();
		for(CohortIndicatorAndDimensionColumn c : getColumns()) {
			Map<String, String> dimOpts = c.getDimensionOptions();
			if (dimOpts.keySet().contains(dimensionKey)) {
				listToRemove.add(c);
			}
		}
		getColumns().removeAll(listToRemove);
		getDimensions().remove(dimensionKey);
	}

    /**
	 * @return the columns
	 */
	public List<CohortIndicatorAndDimensionColumn> getColumns() {
		if (columns == null) {
			columns = new ArrayList<CohortIndicatorAndDimensionColumn>();
		}
		return columns;
	}

	/**
     * @param columns the columns to set
     */
    public void setColumns(List<CohortIndicatorAndDimensionColumn> columns) {
    	this.columns = columns;
    }
    
    /**
     * Adds a Column 
     */
	public void addColumn(CohortIndicatorAndDimensionColumn column) {
		getColumns().add(column);
	}
    
    /**
     * Adds a Column with the given properties
     */
	public void addColumn(String name, String label, Mapped<? extends CohortIndicator> indicator, Map<String, String> dimensionOptions) {
		getColumns().add(new CohortIndicatorAndDimensionColumn(name, label, indicator, dimensionOptions));
	}
	
    /**
     * Removes a column with the given name
     */
	public void removeColumn(String columnName) {
		for (Iterator<CohortIndicatorAndDimensionColumn> i = getColumns().iterator(); i.hasNext(); ) {
			CohortIndicatorAndDimensionColumn col = i.next();
			if (col.getName() != null && col.getName().equals(columnName)) {
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
	public class CohortIndicatorAndDimensionColumn extends DataSetColumn implements Cloneable {

        public static final long serialVersionUID = 1L;
        
        //***** PROPERTIES *****
        
		private Mapped<? extends CohortIndicator> indicator;
		private Map<String, String> dimensionOptions;
		
		//***** CONSTRUCTORS *****
		
		public CohortIndicatorAndDimensionColumn() {}
		
		public CohortIndicatorAndDimensionColumn(String name, String label, Mapped<? extends CohortIndicator> indicator, Map<String, String> dimensionOptions) {
			super(name, label, Object.class);
			this.indicator = indicator;
			this.dimensionOptions = dimensionOptions;
		}
		
        /**
		 * @see java.lang.Object#clone()
		 */
		@Override
		public Object clone() throws CloneNotSupportedException {
			CohortIndicatorAndDimensionColumn c = new CohortIndicatorAndDimensionColumn();
			c.setName(this.getName());
			c.setLabel(this.getLabel());
			c.setDataType(this.getDataType());
			c.setIndicator(this.getIndicator());
			c.setDimensionOptions(this.getDimensionOptions());
			return c;
		}
		
		//***** PROPERTY ACCESS *****

		/**
         * @return the indicator
         */
        public Mapped<? extends CohortIndicator> getIndicator() {
        	return indicator;
        }  
		
        /**
		 * @param indicator the indicator to set
		 */
		public void setIndicator(Mapped<? extends CohortIndicator> indicator) {
			this.indicator = indicator;
		}

		/**
         * @return the dimensionOptions
         */
        public Map<String, String> getDimensionOptions() {
        	return dimensionOptions;
        }

		/**
		 * @param dimensionOptions the dimensionOptions to set
		 */
		public void setDimensionOptions(Map<String, String> dimensionOptions) {
			this.dimensionOptions = dimensionOptions;
		}
	}
}
