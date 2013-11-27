package org.openmrs.module.reporting.dataset.definition;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.EncounterToObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.PatientToObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.PersonToObsDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;

import java.util.ArrayList;
import java.util.List;

@Localized("reporting.ObsDataSetDefinition")
public class ObsDataSetDefinition extends RowPerObjectDataSetDefinition {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty
    private List<Mapped<? extends ObsQuery>> rowFilters;

    /**
     * @see RowPerObjectDataSetDefinition#getSupportedDataDefinitionTypes()
     */
    @Override
    public List<Class<? extends DataDefinition>> getSupportedDataDefinitionTypes() {
        List<Class<? extends DataDefinition>> l = new ArrayList<Class<? extends DataDefinition>>();
        l.add(ObsDataDefinition.class);
        l.add(EncounterDataDefinition.class);
        l.add(PatientDataDefinition.class);
        l.add(PersonDataDefinition.class);
        return l;
    }

    /**
     * Adds a new Column Definition given the passed parameters
     */
    public void addColumn(String name, DataDefinition dataDefinition, String mappings,  DataConverter... converters) {
        if (dataDefinition instanceof ObsDataDefinition) {
            getColumnDefinitions().add(new RowPerObjectColumnDefinition(name, dataDefinition, mappings, converters));
        }
        else if (dataDefinition instanceof EncounterDataDefinition) {
            ObsDataDefinition odd = new EncounterToObsDataDefinition((EncounterDataDefinition) dataDefinition);
            getColumnDefinitions().add(new RowPerObjectColumnDefinition(name, odd, mappings, converters));
        }
        else if (dataDefinition instanceof PatientDataDefinition) {
            ObsDataDefinition odd = new PatientToObsDataDefinition((PatientDataDefinition) dataDefinition);
            getColumnDefinitions().add(new RowPerObjectColumnDefinition(name, odd, mappings, converters));
        }
        else if (dataDefinition instanceof PersonDataDefinition) {
            ObsDataDefinition odd = new PersonToObsDataDefinition((PersonDataDefinition) dataDefinition);
            getColumnDefinitions().add(new RowPerObjectColumnDefinition(name, odd, mappings, converters));
        }
        else {
            throw new IllegalArgumentException("Unable to add data definition of type " + dataDefinition.getClass().getSimpleName());
        }
    }

    @Override
    public void addColumns(String name, RowPerObjectDataSetDefinition dataSetDefinition, String mappings, TimeQualifier whichValues, Integer numberOfValues, DataConverter... converters) {
        // TODO
    }

    /**
     * Add a new row filter with the passed parameter mappings
     */
    public void addRowFilter(ObsQuery filter, String mappings) {
        getRowFilters().add(new Mapped<ObsQuery>(filter, ParameterizableUtil.createParameterMappings(mappings)));
    }

    public List<Mapped<? extends ObsQuery>> getRowFilters() {
        if (rowFilters == null) {
            rowFilters = new ArrayList<Mapped<? extends ObsQuery>>();
        }
        return rowFilters;

    }

    public void setRowFilters(List<Mapped<? extends ObsQuery>> rowFilters) {
        this.rowFilters = rowFilters;
    }

}
