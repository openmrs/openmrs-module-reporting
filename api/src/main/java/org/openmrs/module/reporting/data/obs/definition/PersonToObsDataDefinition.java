package org.openmrs.module.reporting.data.obs.definition;

import org.openmrs.module.reporting.data.JoinDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;

public class PersonToObsDataDefinition  extends JoinDataDefinition<PersonDataDefinition> implements ObsDataDefinition {

    /**
     * Default Constructor
     */
    public PersonToObsDataDefinition() {
        super();
    }

    /**
     * Default Constructor
     */
    public PersonToObsDataDefinition(PersonDataDefinition joinedDataDefinition) {
        super(joinedDataDefinition);
    }

    /**
     * Constructor to populate name
     */
    public PersonToObsDataDefinition(String name, PersonDataDefinition joinedDataDefinition) {
        super(name, joinedDataDefinition);
    }

    /**
     * @see JoinDataDefinition#getJoinedDefinitionType()
     */
    @Override
    public Class<PersonDataDefinition> getJoinedDefinitionType() {
        return PersonDataDefinition.class;
    }



}
