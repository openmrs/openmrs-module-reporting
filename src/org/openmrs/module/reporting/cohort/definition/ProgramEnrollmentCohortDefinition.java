package org.openmrs.module.reporting.cohort.definition;

import java.util.Date;
import java.util.List;

import org.openmrs.Program;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Query for whether the patient enrolled in or completed any of the specified programs in a date range
 */
public class ProgramEnrollmentCohortDefinition extends BaseCohortDefinition {

	private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(required=true)
	private List<Program> programs;
	
	@ConfigurationProperty(required=false)
	private Date enrolledOnOrAfter;

	@ConfigurationProperty(required=false)
	private Date enrolledOnOrBefore;

	@ConfigurationProperty(required=false)
	private Date completedOnOrAfter;

	@ConfigurationProperty(required=false)
	private Date completedOnOrBefore;
	
	/**
	 * Default constructor
	 */
	public ProgramEnrollmentCohortDefinition() {
	}

	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("Patients ");
		if (enrolledOnOrAfter != null)
			ret.append("who enrolled on or after " + enrolledOnOrAfter + " ");
		if (enrolledOnOrBefore != null)
			ret.append("who enrolled on or before " + enrolledOnOrBefore+ " ");
		if (completedOnOrAfter != null)
			ret.append("who completed on or after " + completedOnOrAfter + " ");
		if (completedOnOrBefore != null)
			ret.append("who completed on or before " + completedOnOrBefore + " ");
			
		if (programs != null && programs.size() > 0) {
			ret.append(" in ");
			for (Program p : programs)
				ret.append(p.getName() + " ");
		}		
		return ret.toString();
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
     * @return the enrolledOnOrAfter
     */
    public Date getEnrolledOnOrAfter() {
    	return enrolledOnOrAfter;
    }

	
    /**
     * @param enrolledOnOrAfter the enrolledOnOrAfter to set
     */
    public void setEnrolledOnOrAfter(Date enrolledOnOrAfter) {
    	this.enrolledOnOrAfter = enrolledOnOrAfter;
    }

	
    /**
     * @return the enrolledOnOrBefore
     */
    public Date getEnrolledOnOrBefore() {
    	return enrolledOnOrBefore;
    }

	
    /**
     * @param enrolledOnOrBefore the enrolledOnOrBefore to set
     */
    public void setEnrolledOnOrBefore(Date enrolledOnOrBefore) {
    	this.enrolledOnOrBefore = enrolledOnOrBefore;
    }

	
    /**
     * @return the completedOnOrAfter
     */
    public Date getCompletedOnOrAfter() {
    	return completedOnOrAfter;
    }

	
    /**
     * @param completedOnOrAfter the completedOnOrAfter to set
     */
    public void setCompletedOnOrAfter(Date completedOnOrAfter) {
    	this.completedOnOrAfter = completedOnOrAfter;
    }

	
    /**
     * @return the completedOnOrBefore
     */
    public Date getCompletedOnOrBefore() {
    	return completedOnOrBefore;
    }

	
    /**
     * @param completedOnOrBefore the completedOnOrBefore to set
     */
    public void setCompletedOnOrBefore(Date completedOnOrBefore) {
    	this.completedOnOrBefore = completedOnOrBefore;
    }

    
}
