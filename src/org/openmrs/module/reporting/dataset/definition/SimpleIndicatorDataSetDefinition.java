package org.openmrs.module.reporting.dataset.definition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.Indicator;

public class SimpleIndicatorDataSetDefinition extends BaseDataSetDefinition {
	
	public static final long serialVersionUID = 6405583324151111487L;

	@ConfigurationProperty
	List<SimpleIndicatorColumn> columns = new ArrayList<SimpleIndicatorColumn>();
	
	public SimpleIndicatorDataSetDefinition(){
		super();
	}
	
	public List<SimpleIndicatorColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<SimpleIndicatorColumn> columns) {
		this.columns = columns;
	}

	public void addcolumn(SimpleIndicatorColumn column){
		this.columns.add(column);
	}
	
	public void addColumn(String name, String label, Mapped<? extends Indicator> indicator) {
		getColumns().add(new SimpleIndicatorColumn(name, label, indicator));
	}
	
	public void removeColumn(String columnName) {
		for (Iterator<SimpleIndicatorColumn> i = getColumns().iterator(); i.hasNext(); ) {
			if (i.next().getName().equals(columnName)) {
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
