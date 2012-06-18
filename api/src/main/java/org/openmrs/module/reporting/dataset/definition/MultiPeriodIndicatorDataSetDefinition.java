package org.openmrs.module.reporting.dataset.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;


/**
 * This DataSetDefinition wraps a {@link CohortIndicatorDataSetDefinition} and allows you to run it on
 * a series of startDate/endDate/location triplet. The {@link DataSet} that this defines has the same columns
 * as the wrapped {@link DataSetDefinition}, prepended by columns for 'startDate', 'endDate', and 'location'
 * for each iteration. Each row of the DataSet is the result you'd get from evaluating the wrapped
 * DataSetDefinition on one startDate/endDate/location triplet, 
 */
@Localized("reporting.MultiPeriodIndicatorDataSetDefinition")
public class MultiPeriodIndicatorDataSetDefinition extends BaseDataSetDefinition implements DataSetDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	private CohortIndicatorDataSetDefinition baseDefinition;
	
	@ConfigurationProperty
	private List<Iteration> iterations;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public MultiPeriodIndicatorDataSetDefinition() {
		iterations = new ArrayList<Iteration>();
	}
	
	/**
	 * Base Constructor
	 */
	public MultiPeriodIndicatorDataSetDefinition(CohortIndicatorDataSetDefinition baseDefinition) {
		this();
		this.baseDefinition = baseDefinition;
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * Add a new Iteration
	 */
	public void addIteration(Iteration iteration) {
		iterations.add(iteration);
	}

	/**
	 * @return all the Columns for this DataSetDefinition
	 */
	public List<DataSetColumn> getColumns() {
		List<DataSetColumn> ret = new ArrayList<DataSetColumn>();
		ret.add(new DataSetColumn("startDate", "Start Date", Date.class));
		ret.add(new DataSetColumn("endDate", "End Date", Date.class));
		ret.add(new DataSetColumn("location", "Location", Location.class));
		ret.addAll(baseDefinition.getColumns());
		return ret;
    }
	
	//***** PROPERTY ACCESS *****
	
    /**
     * @return the baseDefinition
     */
    public CohortIndicatorDataSetDefinition getBaseDefinition() {
    	return baseDefinition;
    }
	
    /**
     * @param baseDefinition the baseDefinition to set
     */
    public void setBaseDefinition(CohortIndicatorDataSetDefinition baseDefinition) {
    	this.baseDefinition = baseDefinition;
    }
	
    /**
     * @return the iterations
     */
    public List<Iteration> getIterations() {
    	return iterations;
    }

    /**
     * @param iterations the iterations to set
     */
    public void setIterations(List<Iteration> iterations) {
    	this.iterations = iterations;
    }
    
    //***** INNER CLASS *****

	/**
	 * This represents one pass running the underlying data set definition
	 */
	public static class Iteration {
		
		private Date startDate;
		private Date endDate;
		private Location location;
		
		public Iteration() { }
		
		public Iteration(Date startDate, Date endDate, Location location) {
			this.startDate = startDate;
			this.endDate = endDate;
			this.location = location;
		}
		
        public Date getStartDate() {
        	return startDate;
        }
		
        public void setStartDate(Date startDate) {
        	this.startDate = startDate;
        }
		
        public Date getEndDate() {
        	return endDate;
        }
		
        public void setEndDate(Date endDate) {
        	this.endDate = endDate;
        }
		
        public Location getLocation() {
        	return location;
        }

        public void setLocation(Location location) {
        	this.location = location;
        }	
	}
}
