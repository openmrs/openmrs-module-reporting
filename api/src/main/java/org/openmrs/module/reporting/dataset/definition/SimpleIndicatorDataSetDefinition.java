/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.definition.evaluator.SimpleIndicatorDataSetEvaluator;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.Indicator;

/**
 * This is a DataSet Definition type that supports indicators as columns
 * @see SimpleIndicatorDataSetEvaluator
 */
public class SimpleIndicatorDataSetDefinition extends BaseDataSetDefinition {
	
	public static final long serialVersionUID = 6405583324151111487L;

	@ConfigurationProperty
	List<SimpleIndicatorColumn> columns;
	
	public SimpleIndicatorDataSetDefinition(){
		super();
	}
	
	public List<SimpleIndicatorColumn> getColumns() {
		if (this.columns == null)
			columns = new ArrayList<SimpleIndicatorColumn>();
		return columns;
	}

	public void setColumns(List<SimpleIndicatorColumn> columns) {
		this.columns = columns;
	}

	public void addColumn(SimpleIndicatorColumn column){
		getColumns().add(column);
	}
	
	public void addColumn(String name, String label, Mapped<? extends Indicator> indicator) {
		getColumns().add(new SimpleIndicatorColumn(name, label, indicator));
	}
	
	public void removeColumn(String columnName) {
		for (Iterator<SimpleIndicatorColumn> i = getColumns().iterator(); i.hasNext(); ) {
			SimpleIndicatorColumn col = i.next();
			if (col.getName() != null && col.getName().equals(columnName)) {
				i.remove();
			}
		}
	}

	public class SimpleIndicatorColumn extends DataSetColumn implements Cloneable {

        public static final long serialVersionUID = 1L;
        
        //***** PROPERTIES *****
        
		private Mapped<? extends Indicator> indicator;
		
		//***** CONSTRUCTORS *****
		
		public SimpleIndicatorColumn() {}
		
		public SimpleIndicatorColumn(String name, String label, Mapped<? extends Indicator> indicator) {
			super(name, label, Object.class);
			this.indicator = indicator;
		}
		
        /**
		 * @see java.lang.Object#clone()
		 */
		@Override
		public Object clone() throws CloneNotSupportedException {
			SimpleIndicatorColumn c = new SimpleIndicatorColumn();
			c.setName(this.getName());
			c.setLabel(this.getLabel());
			c.setDataType(this.getDataType());
			c.setIndicator(this.getIndicator());
			return c;
		}

		public Mapped<? extends Indicator> getIndicator() {
			return indicator;
		}

		public void setIndicator(Mapped<? extends Indicator> indicator) {
			this.indicator = indicator;
		}
	}
}
