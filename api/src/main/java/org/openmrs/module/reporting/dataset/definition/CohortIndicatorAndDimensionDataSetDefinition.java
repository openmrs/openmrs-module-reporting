package org.openmrs.module.reporting.dataset.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;

/**
 * <pre>
 * This represents a data set which contains indicators and optional dimension combinations
 * </pre>
 */
@Localized("reporting.CohortIndicatorAndDimensionDataSetDefinition")
public class CohortIndicatorAndDimensionDataSetDefinition extends BaseDataSetDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	Map<String, Mapped<CohortDefinitionDimension>> dimensions;
	
	@ConfigurationProperty
	List<CohortIndicatorAndDimensionSpecification> specifications;
	
	//***** CONSTRUCTORS *****
	
	public CohortIndicatorAndDimensionDataSetDefinition() {
		super();
	}

	//***** PROPERTY ACCESS AND INSTANCE METHODS
	
	/**
	 * @return a List of each data set column per CohortIndicatorAndDimensionSpecification defined
	 */
	public Map<CohortIndicatorAndDimensionSpecification, List<DataSetColumn>> getColumnsBySpecification() {
		
		Map<CohortIndicatorAndDimensionSpecification, List<DataSetColumn>> ret =
			new LinkedHashMap<CohortIndicatorAndDimensionSpecification, List<DataSetColumn>>();
		
		for (CohortIndicatorAndDimensionSpecification s : getSpecifications()) {
			
			List<DataSetColumn> columns = new ArrayList<DataSetColumn>();
			
			List<String> combinations = IndicatorUtil.compileColumnDimensionOptions(s.getDimensionOptions());
			combinations.add(0, null); // Add in the "no dimension" case at the start

			for (String combination : combinations) {
				DataSetColumn column = new DataSetColumn(s.getIndicatorNumber(), s.getLabel(), Object.class);	
				if (combination != null) {
					for (String option : combination.split(",")) {
						String[] dimOpt = option.split("=");
						column.setName(column.getName() + "." + option);
						column.setLabel(column.getLabel() + (column.getLabel().equals(s.getLabel()) ? " (" : ", ") + dimOpt[0] + " - " + dimOpt[1]);
					}
					column.setLabel(column.getLabel() + ")");
				}
				columns.add(column);
			}
			ret.put(s, columns);
		}
		return ret;
	}
	
    /**
	 * @return the dimensions
	 */
	public Map<String, Mapped<CohortDefinitionDimension>> getDimensions() {
		if (dimensions == null) {
			dimensions = new HashMap<String, Mapped<CohortDefinitionDimension>>();
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
		for(CohortIndicatorAndDimensionSpecification c : getSpecifications()) {
			c.getDimensionOptions().remove(dimensionKey);
		}
		getDimensions().remove(dimensionKey);
	}

	/**
	 * @return the specifications
	 */
	public List<CohortIndicatorAndDimensionSpecification> getSpecifications() {
		if (specifications == null) {
			specifications = new ArrayList<CohortIndicatorAndDimensionSpecification>();
		}
		return specifications;
	}

	/**
     * @param specifications the specifications to set
     */
    public void setSpecifications(List<CohortIndicatorAndDimensionSpecification> specifications) {
    	this.specifications = specifications;
    }
	
    /**
     * Removes a specification with the given name
     */
	public void removeSpecification(int index) {
		getSpecifications().remove(index);
	}
	
	/**
	 * Adds a new Indicator and Dimension specification
	 */
	public void addSpecification(CohortIndicatorAndDimensionSpecification specification) {
		getSpecifications().add(specification);
	}

	public void addSpecification(String indicatorNumber, String label, Mapped<CohortIndicator> indicator, Map<String, List<String>> dimensionOptions) {
		CohortIndicatorAndDimensionSpecification s = new CohortIndicatorAndDimensionSpecification();
		s.setIndicatorNumber(indicatorNumber);
		s.setLabel(label);
		s.setIndicator(indicator);
		s.setDimensionOptions(dimensionOptions);
		addSpecification(s);
	}
    
    //***** INNER CLASSES *****

    /**
     * Specification which encapsulates information about the indicator and dimensions included
     */
	public class CohortIndicatorAndDimensionSpecification {

        public static final long serialVersionUID = 1L;
        
        //***** PROPERTIES *****
        
        private String indicatorNumber;
        private String label;
		private Mapped<CohortIndicator> indicator;
		private Map<String, List<String>> dimensionOptions;
		
		//***** CONSTRUCTORS *****
		
		public CohortIndicatorAndDimensionSpecification() {}

		//***** PROPERTY ACCESS *****
		
        /**
		 * @return the indicatorNumber
		 */
		public String getIndicatorNumber() {
			return indicatorNumber;
		}

		/**
		 * @param indicatorNumber the indicatorNumber to set
		 */
		public void setIndicatorNumber(String indicatorNumber) {
			this.indicatorNumber = indicatorNumber;
		}

		/**
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * @param label the label to set
		 */
		public void setLabel(String label) {
			this.label = label;
		}

		/**
         * @return the indicator
         */
        public Mapped<CohortIndicator> getIndicator() {
        	return indicator;
        }  

		/**
		 * @param indicator the indicator to set
		 */
		public void setIndicator(Mapped<CohortIndicator> indicator) {
			this.indicator = indicator;
		}

		/**
         * @return the dimensionOptions
         */
        public Map<String, List<String>> getDimensionOptions() {
        	if (dimensionOptions == null) {
        		dimensionOptions = new HashMap<String, List<String>>();
        	}
        	return dimensionOptions;
        }

		/**
		 * @param dimensionOptions the dimensionOptions to set
		 */
		public void setDimensionOptions(Map<String, List<String>> dimensionOptions) {
			this.dimensionOptions = dimensionOptions;
		}
	}
}
