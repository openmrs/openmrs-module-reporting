package org.openmrs.module.cohort.definition;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.evaluation.parameter.Mapped;

public class CompositionCohortDefinition extends BaseCohortDefinition {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	public static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private Map<String, Mapped<CohortDefinition>> searches = new HashMap<String, Mapped<CohortDefinition>>();
	
	@ConfigurationProperty(required=true)
	private String compositionString;
	
	//***** CONSTRUCTORS *****

    /**
     * Default Constructor
     */
	public CompositionCohortDefinition() {
		super();
	}
	
	//***** PROPERTY ACCESS *****
	
    /**
     * @return the compositionString
     */
    public String getCompositionString() {
    	return compositionString;
    }
	
	/**
     * @param compositionString the compositionString to set
     */
    public void setCompositionString(String compositionString) {
    	this.compositionString = compositionString;
    }
    
    /**
     * @return the searches
     */
    public Map<String, Mapped<CohortDefinition>> getSearches() {
    	return searches;
    }

	
    /**
     * @param searches the searches to set
     */
    public void setSearches(Map<String, Mapped<CohortDefinition>> searches) {
    	this.searches = searches;
    }

}
