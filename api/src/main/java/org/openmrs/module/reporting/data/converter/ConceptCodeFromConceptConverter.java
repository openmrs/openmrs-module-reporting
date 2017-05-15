package org.openmrs.module.reporting.data.converter;

import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptSource;

public class ConceptCodeFromConceptConverter implements DataConverter {

    private String conceptSourceName;

    public ConceptCodeFromConceptConverter(String conceptSourceName) {
        this.conceptSourceName = conceptSourceName;
    }

    public ConceptCodeFromConceptConverter(ConceptSource conceptSource) {
        this.conceptSourceName = conceptSource.getName();
    }

    /**
     * @should throw conversion exception when class cast fails
     *
     */
    @Override
    public Object convert(Object original) {

        Concept concept = ConverterHelper.convertTo(original, Concept.class);

        if (concept == null) {
            return null;
        }

        for (ConceptMap map : concept.getConceptMappings()) {
            // TODO right now assumes only one reference term per source, just returns first term found
            if (map.getConceptReferenceTerm().getConceptSource().getName().equalsIgnoreCase(conceptSourceName)) {
                return map.getConceptReferenceTerm().getCode();
            }
        }
        return null;
    }

    @Override
    public Class<?> getInputDataType() {
        return Concept.class;
    }

    @Override
    public Class<?> getDataType() {
        return String.class;
    }
}
