package org.openmrs.module.reporting.cohort.definition.history;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;


/**
 * Represents a history of having used CohortDefinitions with parameter values specified
 * 
 * Hopefully this will replace CohortDefinitionHistory soon -DJ (2009-09-16)
 */
public class CohortDefinitionSearchHistory extends BaseOpenmrsMetadata {

	private Integer id;
	private List<Mapped<CohortDefinition>> history = new Vector<Mapped<CohortDefinition>>();
	
	public CohortDefinitionSearchHistory() { }

	/**
	 * Adds a search to the history
	 * 
	 * @param search
	 */
	public void addSearch(Mapped<CohortDefinition> search) {
		history.add(search);
	}
	
	/**
	 * Adds a search to the history
	 * 
	 * @param search
	 * @param parameters
	 */
	public void addSearch(CohortDefinition search, Map<String, Object> parameters) {
		history.add(new Mapped<CohortDefinition>(search, parameters));
	}
	
	/**
	 * Removes a search from the history
	 * 
	 * @param index
	 * @return
	 */
	public Mapped<CohortDefinition> removeSearch(int index) {
		return history.remove(index);
	}
	
    /**
     * @return the history
     */
    public List<Mapped<CohortDefinition>> getHistory() {
    	return history;
    }
	
    /**
     * @param history the history to set
     */
    public void setHistory(List<Mapped<CohortDefinition>> history) {
    	this.history = history;
    }

	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	public Integer getId() {
	    return id;
    }

	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	public void setId(Integer id) {
	    this.id = id;
    }
	
}
