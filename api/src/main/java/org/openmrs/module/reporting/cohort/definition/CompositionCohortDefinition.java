package org.openmrs.module.reporting.cohort.definition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

import java.util.HashMap;
import java.util.Map;

@Localized("reporting.CompositionCohortDefinition")
public class CompositionCohortDefinition extends BaseCohortDefinition {
	
	public static final long serialVersionUID = 1L;
	
	protected transient Log log = LogFactory.getLog(getClass());
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private Map<String, Mapped<? extends CohortDefinition>> searches = new HashMap<String, Mapped<? extends CohortDefinition>>();
	
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
    public Map<String, Mapped<? extends CohortDefinition>> getSearches() {
    	if (searches == null) {
    		searches = new HashMap<String, Mapped<? extends CohortDefinition>>();
    	}
    	return searches;
    }
    
    /**
     * Adds a cohort definition
     */
    public void addSearch(String key, Mapped<? extends CohortDefinition> mappedDefinition) {
    	getSearches().put(key, mappedDefinition);
    }
    
    /**
     * Adds a cohort definition
     */
    public void addSearch(String key, CohortDefinition definition, Map<String, Object> mappings) {
    	addSearch(key, new Mapped<CohortDefinition>(definition, mappings));
    }

    /**
     * @param searches the searches to set
     */
    public void setSearches(Map<String, Mapped<? extends CohortDefinition>> searches) {
    	this.searches = searches;
    }
}
