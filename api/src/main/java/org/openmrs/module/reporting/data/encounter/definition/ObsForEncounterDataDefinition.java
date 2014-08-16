package org.openmrs.module.reporting.data.encounter.definition;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.List;

/**
 * Column of a specific obs or set of obs associated with an encounter based on the question concept
 * associated with the obs
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class ObsForEncounterDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty
    private Concept question;

    @ConfigurationProperty
    private boolean singleObs = true;

    public ObsForEncounterDataDefinition() {
        super();
    }

    public ObsForEncounterDataDefinition(String name) {
        super(name);
    }

    public Concept getQuestion() {
        return question;
    }

    public void setQuestion(Concept question) {
        this.question = question;
    }

    public boolean isSingleObs() {
        return singleObs;
    }

    public void setSingleObs(boolean singleObs) {
        this.singleObs = singleObs;
    }

    @Override
    public Class<?> getDataType() {
        return singleObs ? Obs.class : List.class;
    }

}
