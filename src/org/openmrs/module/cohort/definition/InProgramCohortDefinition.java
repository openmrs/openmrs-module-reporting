package org.openmrs.module.cohort.definition;

import java.util.Date;
import java.util.List;

import org.openmrs.Program;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;


/**
 * Query for whether the was in a program on a date or date range
 * (Using onDate is equivalent to setting onOrAfter==onOrBefore, but may be more efficient and readable
 */
public class InProgramCohortDefinition extends BaseCohortDefinition {

	private static final long serialVersionUID = 1L;

	@ConfigurationProperty(required=true)
	private List<Program> programs;
	
	@ConfigurationProperty(required=false)
	private Date onOrAfter;

	@ConfigurationProperty(required=false)
	private Date onOrBefore;

	@ConfigurationProperty(required=false)
	private Date onDate;

	/**
	 * Default constructor
	 */
	public InProgramCohortDefinition() {
	}

	
    /**
     * @return the programs
     */
    public List<Program> getPrograms() {
    	return programs;
    }

	
    /**
     * @param programs the programs to set
     */
    public void setPrograms(List<Program> programs) {
    	this.programs = programs;
    }

	
    /**
     * @return the onOrAfter
     */
    public Date getOnOrAfter() {
    	return onOrAfter;
    }

	
    /**
     * @param onOrAfter the onOrAfter to set
     */
    public void setOnOrAfter(Date onOrAfter) {
    	this.onOrAfter = onOrAfter;
    }

	
    /**
     * @return the onOrBefore
     */
    public Date getOnOrBefore() {
    	return onOrBefore;
    }

	
    /**
     * @param onOrBefore the onOrBefore to set
     */
    public void setOnOrBefore(Date onOrBefore) {
    	this.onOrBefore = onOrBefore;
    }

	
    /**
     * @return the onDate
     */
    public Date getOnDate() {
    	return onDate;
    }

	
    /**
     * @param onDate the onDate to set
     */
    public void setOnDate(Date onDate) {
    	this.onDate = onDate;
    }
	
	
}
