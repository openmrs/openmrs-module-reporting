package org.openmrs.module.reporting.query.obs.definition;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.query.BaseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Lets you query for Obs based on simple properties on Obs
 * TODO add more properties to query on
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class BasicObsQuery extends BaseQuery<Obs> implements ObsQuery {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty
    public List<Concept> conceptList;

    @ConfigurationProperty
    public Date onOrAfter;

    @ConfigurationProperty
    public Date onOrBefore;

    public void addConcept(Concept concept) {
        if (conceptList == null) {
            conceptList = new ArrayList<Concept>();
        }
        conceptList.add(concept);
    }

    public List<Concept> getConceptList() {
        return conceptList;
    }

    public void setConceptList(List<Concept> conceptList) {
        this.conceptList = conceptList;
    }

    public Date getOnOrAfter() {
        return onOrAfter;
    }

    public void setOnOrAfter(Date onOrAfter) {
        this.onOrAfter = onOrAfter;
    }

    public Date getOnOrBefore() {
        return onOrBefore;
    }

    public void setOnOrBefore(Date onOrBefore) {
        this.onOrBefore = onOrBefore;
    }
}
