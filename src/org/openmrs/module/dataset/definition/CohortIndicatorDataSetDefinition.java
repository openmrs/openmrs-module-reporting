/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.dataset.definition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.api.APIException;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.dimension.CohortDimension;
import org.openmrs.module.indicator.dimension.DimensionSet;

/**
 * Defines a DataSetDefinition which consists of Indicators and Dimensions
 */
public class CohortIndicatorDataSetDefinition extends BaseDataSetDefinition {
	
	private static final long serialVersionUID = 1L;
	
	private DimensionSet<CohortDimension> dimensions;
	private Map<String, Mapped<CohortIndicator>> indicators;
	private Map<DataSetColumn, ColumnDefinition> columnSpecifications;
	
	/**
	 * Default constructor
	 */
	public CohortIndicatorDataSetDefinition() {
		super();
		dimensions = new DimensionSet<CohortDimension>();
		indicators = new LinkedHashMap<String, Mapped<CohortIndicator>>();
		columnSpecifications = new LinkedHashMap<DataSetColumn, ColumnDefinition>();
	}
	
	/**
	 * Public constructor
	 * 
	 * @param name
	 * @param description
	 * @param questions
	 */
	public CohortIndicatorDataSetDefinition(
			String name, 
			String description, 
			DimensionSet<CohortDimension> dimensions, 
			Map<String, Mapped<CohortIndicator>> indicators, 
			Map<DataSetColumn, ColumnDefinition> columnSpecs) { 
		this.setName(name);
		this.setDescription(description);
		this.dimensions = dimensions;
		this.indicators = indicators;
		this.columnSpecifications = columnSpecs;
	}	
	

	/**
     * @see DataSetDefinition#getColumns()
     */
    public List<DataSetColumn> getColumns() {
    	return new ArrayList<DataSetColumn>(getColumnSpecifications().keySet());
    }
	
	//****** INSTANCE METHODS ******

	/**
	 * Adds a Dimension with the given key
	 * @param key the key with which to associate this dimension
	 * @param dimension the dimension to add
	 * @throws APIException if the key already exists
	 */
    public void addDimension(String key, Mapped<CohortDimension> dimension) {
    	getDimensions().addDimension(key, dimension);
    }
    
	/**
	 * Adds a Dimension with the given key
	 * @param key the key with which to associate this dimension
	 * @param dimension the dimension to add
	 * @throws APIException if the key already exists or is a reserved key
	 */
    public void addDimension(String key, CohortDimension dimension, String mappings) {
    	addDimension(key, new Mapped<CohortDimension>(dimension, mappings));
    }
    
	/**
	 * Returns the dimension with the given key
	 * @return the Dimension specified with the given key
	 */
    public Mapped<CohortDimension> getDimension(String dimensionKey) {
    	return getDimensions().getDimension(dimensionKey);
    }
    
    /**
     * Returns all specified option keys for the named dimension.
     * Included in this will be a default <em>unspecified</em> option key
     * @param dimensionKey the dimension to look up
     * @return all specified options for the named dimension, including an <em>unspecified</em> option key
     */
    public List<String> getDimensionOptionKeys(String dimensionKey) {
    	Mapped<CohortDimension> d = getDimension(dimensionKey);
    	if (d == null) {
    		return new ArrayList<String>();
    	}
    	return d.getParameterizable().getOptionKeys();
    }
    
	/**
	 * Adds a Indicator with the given key
	 * @param key the key with which to associate this indicator
	 * @param indicator the indicator to add
	 * @throws APIException if the key already exists
	 */
    public void addIndicator(String key, Mapped<CohortIndicator> indicator) {
    	for (String k : getIndicators().keySet()) {
    		if (k.equalsIgnoreCase(key)) {
    			throw new APIException("Cannot add key <" + k + "> because it is already added.");
    		}
    	}
    	getIndicators().put(key, indicator);
    }
    
	/**
	 * Adds a Indicator with the given key
	 * @param key the key with which to associate this indicator
	 * @param indicator the indicator to add
	 * @throws APIException if the key already exists
	 */
    public void addIndicator(String key, CohortIndicator indicator, String mappings) {
    	addIndicator(key, new Mapped<CohortIndicator>(indicator, mappings));
    }
    
	/**
	 * Adds a Indicator with the given key
	 * @param key the key with which to associate this indicator
	 * @param indicator the indicator to add
	 * @throws APIException if the key already exists
	 */
    public void addIndicator(String key, CohortIndicator indicator, Map<String, String> mappings) {
    	addIndicator(key, new Mapped<CohortIndicator>(indicator, mappings));
    }
    
	/**
	 * Returns the indicator with the given key
	 * @return the Indicator specified with the given key
	 */
    public Mapped<CohortIndicator> getIndicator(String indicatorKey) {
    	for (String key : getIndicators().keySet()) {
    		if (key.equalsIgnoreCase(indicatorKey)) {
    			return getIndicators().get(key);
    		}
    	}
    	return null;
    }
    
	/**
	 * Adds a Column Specification with the given data
	 * @param key the key with which to associate this indicator
	 * @param indicator the indicator to add
	 * @throws APIException if the key already exists
	 */
    public void addColumnSpecification(DataSetColumn column, ColumnDefinition specification) {
    	for (DataSetColumn k : getColumnSpecifications().keySet()) {
    		if (k.getKey().equalsIgnoreCase(column.getKey())) {
    			throw new APIException("Cannot add key <" + k + "> because it is already added.");
    		}
    	}
    	getColumnSpecifications().put(column, specification);
    }
    
	/**
	 * Adds a Column Specification with the given data
	 * @param key the key with which to associate this indicator
	 * @param indicator the indicator to add
	 * @throws APIException if the key already exists
	 */
    public void addColumnSpecification(String key, String description, Class<?> clazz, String indicatorKey, String dimensionQuery) {
    	DataSetColumn column = new SimpleDataSetColumn(key, description, clazz);
    	ColumnDefinition def = new ColumnDefinition(indicatorKey, dimensionQuery);
    	addColumnSpecification(column, def);
    }
    
    //****** PROPERTY ACCESS ******

    /**
     * @return DimensionSet<CohortDimension>
     */
	public DimensionSet<CohortDimension> getDimensions() {
		if (dimensions == null) {
			dimensions = new DimensionSet<CohortDimension>();
		}
		return dimensions;
	}

	/**
	 * @param dimensions
	 */
	public void setDimensions(DimensionSet<CohortDimension> dimensions) {
		this.dimensions = dimensions;
	}
	
	/**
	 * @return Map<String, Mapped<CohortIndicator>>
	 */
	public Map<String, Mapped<CohortIndicator>> getIndicators() {
		if (indicators == null) {
			indicators = new HashMap<String, Mapped<CohortIndicator>>();
		}
		return indicators;
	}

	/**
	 * @param indicators
	 */
	public void setIndicators(Map<String, Mapped<CohortIndicator>> indicators) {
		this.indicators = indicators;
	}
	
	/**
	 * @return Map<DataSetColumn, ColumnDefinition>
	 */
	public Map<DataSetColumn, ColumnDefinition> getColumnSpecifications() {
		return columnSpecifications;
	}

	/**
	 * @param columnSpecifications
	 */
	public void setColumnSpecifications(Map<DataSetColumn, ColumnDefinition> columnSpecifications) {
		if (columnSpecifications == null) {
			columnSpecifications = new HashMap<DataSetColumn, ColumnDefinition>();
		}
		this.columnSpecifications = columnSpecifications;
	}

	//****** INNER CLASSES ******

	/**
	 * Defines a Definition of a defined indicator and list of dimensions
	 */
	public class ColumnDefinition implements Serializable {
		
		// Properties
		private String indicatorKey;
		private String dimensionQuery;
		
		// Constructors
		public ColumnDefinition() {}
		
		public ColumnDefinition(String indicatorKey, String dimensionQuery) {
			this.indicatorKey = indicatorKey;
			this.dimensionQuery = dimensionQuery;
		}

		// Property Access
		
		public String getIndicatorKey() {
			return indicatorKey;
		}

		public void setIndicatorKey(String indicatorKey) {
			this.indicatorKey = indicatorKey;
		}

		public String getDimensionQuery() {
			return dimensionQuery;
		}

		public void setDimensionQuery(String dimensionQuery) {
			this.dimensionQuery = dimensionQuery;
		}
	}
}
