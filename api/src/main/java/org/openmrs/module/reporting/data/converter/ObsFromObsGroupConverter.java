package org.openmrs.module.reporting.data.converter;

import org.openmrs.Concept;
import org.openmrs.Obs;

public class ObsFromObsGroupConverter implements DataConverter {

    private Concept concept;

    public ObsFromObsGroupConverter(Concept concept) {
        this.concept = concept;
    }

    @Override
    public Object convert(Object original) {
        Obs o = (Obs) original;
        if (o == null) {
            return null;
        }
        // just returns the first match if more than one group member with that concept
        for (Obs groupMembers : o.getGroupMembers()) {
            if (groupMembers.getConcept().equals(concept)) {
                return groupMembers;
            }
        }
        return null;
    }

    @Override
    public Class<?> getInputDataType() {
        return Obs.class;
    }

    @Override
    public Class<?> getDataType() {
        return Obs.class;
    }
}
