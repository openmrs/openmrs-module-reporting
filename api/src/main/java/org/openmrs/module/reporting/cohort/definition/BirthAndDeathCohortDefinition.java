package org.openmrs.module.reporting.cohort.definition;

import java.util.Date;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Query for patients based on ranges for birthdate and death date
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.BirthAndDeathCohortDefinition")
public class BirthAndDeathCohortDefinition extends BaseCohortDefinition {
	
	public static final long serialVersionUID = 1L;
		
	@ConfigurationProperty(group="birth")
	private Date bornOnOrAfter;

	@ConfigurationProperty(group="birth")
	private Date bornOnOrBefore;
	
	@ConfigurationProperty(group="death")
	private Date diedOnOrAfter;

	@ConfigurationProperty(group="death")
	private Date diedOnOrBefore;

	public BirthAndDeathCohortDefinition() { }

	
    /**
     * @return the bornOnOrAfter
     */
    public Date getBornOnOrAfter() {
    	return bornOnOrAfter;
    }

	
    /**
     * @param bornOnOrAfter the bornOnOrAfter to set
     */
    public void setBornOnOrAfter(Date bornOnOrAfter) {
    	this.bornOnOrAfter = bornOnOrAfter;
    }

	
    /**
     * @return the bornOnOrBefore
     */
    public Date getBornOnOrBefore() {
    	return bornOnOrBefore;
    }

	
    /**
     * @param bornOnOrBefore the bornOnOrBefore to set
     */
    public void setBornOnOrBefore(Date bornOnOrBefore) {
    	this.bornOnOrBefore = bornOnOrBefore;
    }

	
    /**
     * @return the diedOnOrAfter
     */
    public Date getDiedOnOrAfter() {
    	return diedOnOrAfter;
    }

	
    /**
     * @param diedOnOrAfter the diedOnOrAfter to set
     */
    public void setDiedOnOrAfter(Date diedOnOrAfter) {
    	this.diedOnOrAfter = diedOnOrAfter;
    }

	
    /**
     * @return the diedOnOrBefore
     */
    public Date getDiedOnOrBefore() {
    	return diedOnOrBefore;
    }

	
    /**
     * @param diedOnOrBefore the diedOnOrBefore to set
     */
    public void setDiedOnOrBefore(Date diedOnOrBefore) {
    	this.diedOnOrBefore = diedOnOrBefore;
    }
	
}
