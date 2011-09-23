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
package org.openmrs.module.reporting.data;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.RowPerObjectDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.BaseDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

/**
 * DataSet Data Definition
 */
public abstract class DataSetDataDefinition extends BaseDataDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private RowPerObjectDataSetDefinition definition;
	
	@ConfigurationProperty(required=true)
	private TimeQualifier whichValues;
	
	@ConfigurationProperty(required=true)
	private Integer numberOfValues;
	
	//***** CONSTRUCTORS *****
		
	/**
	 * Default Constructor
	 */
	public DataSetDataDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate definition only
	 */
	public DataSetDataDefinition(RowPerObjectDataSetDefinition definition) {
		this();
		setDefinition(definition);
	}
	
	/**
	 * Constructor to populate all properties
	 */
	public DataSetDataDefinition(RowPerObjectDataSetDefinition definition, TimeQualifier whichValues, Integer numberOfValues) {
		this(definition);
		this.whichValues = whichValues;
		this.numberOfValues = numberOfValues;
	}

	//***** INSTANCE METHODS *****
	
	/**
	 * @return the DataSetColumns that are supported in the returned DataSetRow
	 */
	public List<FlattenedDataSetColumn> getDataSetColumns() {
		List<FlattenedDataSetColumn> l = new ArrayList<FlattenedDataSetColumn>();
		if (definition != null) {
			if (whichValues != null && numberOfValues != null) {
				for (int index = 0; index < getNumberOfValues(); index++) {
					for (DataSetColumn c : definition.getDataSetColumns()) {
						l.add(new FlattenedDataSetColumn(c, index));
					}
				}
			}
			else {
				for (DataSetColumn c : definition.getDataSetColumns()) {
					l.add(new FlattenedDataSetColumn(c, null));
				}
			}
		}
		return l;
	}
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return DataSetRow.class;
	}
	
	/**
	 * @see BaseDefinition#getParameter(String)
	 */
	@Override
	public Parameter getParameter(String name) {
		return definition.getParameter(name);
	}

	/**
	 * @see BaseDefinition#getParameters()
	 */
	@Override
	public List<Parameter> getParameters() {
		return definition.getParameters();
	}

	//***** PROPERTY ACCESS *****
	
	/**
	 * @return the definition
	 */
	public RowPerObjectDataSetDefinition getDefinition() {
		return definition;
	}

	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(RowPerObjectDataSetDefinition definition) {
		this.definition = definition;
	}

	/**
	 * @return the whichValues
	 */
	public TimeQualifier getWhichValues() {
		return whichValues;
	}

	/**
	 * @param whichValues the whichValues to set
	 */
	public void setWhichValues(TimeQualifier whichValues) {
		this.whichValues = whichValues;
	}

	/**
	 * @return the numberOfValues
	 */
	public Integer getNumberOfValues() {
		return numberOfValues;
	}

	/**
	 * @param numberOfValues the numberOfValues to set
	 */
	public void setNumberOfValues(Integer numberOfValues) {
		this.numberOfValues = numberOfValues;
	}
	
	//***** INSTANCE METHODS *****
	
	public class FlattenedDataSetColumn extends DataSetColumn {

		private static final long serialVersionUID = 1L;
		
		//***** PROPERTIES *****
		
		private DataSetColumn originalColumn;
		private Integer index;
		
		//***** CONSTRUCTORS *****
		
		/**
		 * Default constructor
		 */
		public FlattenedDataSetColumn() { }
		
		/**
		 * Full constructor
		 */
		public FlattenedDataSetColumn(DataSetColumn c, Integer index) {
			String prefix = (index == null ? "" : (index+1) + "_");
			setName(prefix + c.getName());
			setLabel(prefix + c.getLabel());
			setDataType(c.getDataType());
			setOriginalColumn(c);
			setIndex(index);
		}
		
		//***** PROPERTY ACCESS *****

		/**
		 * @return the originalColumn
		 */
		public DataSetColumn getOriginalColumn() {
			return originalColumn;
		}

		/**
		 * @param originalColumn the originalColumn to set
		 */
		public void setOriginalColumn(DataSetColumn originalColumn) {
			this.originalColumn = originalColumn;
		}
		
		/**
		 * @return the index
		 */
		public Integer getIndex() {
			return index;
		}

		/**
		 * @param index the index to set
		 */
		public void setIndex(Integer index) {
			this.index = index;
		}	
	}
}