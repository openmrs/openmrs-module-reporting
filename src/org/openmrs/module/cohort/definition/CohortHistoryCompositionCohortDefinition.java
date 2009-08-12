package org.openmrs.module.cohort.definition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.cohort.definition.configuration.ConfigurationProperty;
import org.openmrs.module.cohort.definition.history.CohortDefinitionHistory;

public class CohortHistoryCompositionCohortDefinition extends BaseCohortDefinition {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	public static final long serialVersionUID = 6736677001L;
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private CohortDefinitionHistory history;
	
	@ConfigurationProperty(required=true)
	private String compositionString;
	
	//***** CONSTRUCTORS *****

    /**
     * Default Constructor
     */
	public CohortHistoryCompositionCohortDefinition() {
		super();
	}
	
	//***** PROPERTY ACCESS *****
	
    /**
     * @return the history
     */
    public CohortDefinitionHistory getHistory() {
    	return history;
    }

    /**
     * @param history the history to set
     */
    public void setHistory(CohortDefinitionHistory history) {
    	this.history = history;
    }
	
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
}
