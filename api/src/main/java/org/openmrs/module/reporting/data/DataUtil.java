/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data;

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.PersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.module.reporting.query.person.PersonIdSet;

import java.util.List;

/**
 * Data utility classes
 */
public class DataUtil {

	/**
	 * @return the data passed through zero or more data converters
	 */
	public static Object convertData(Object data, DataConverter...converters) {
		Object ret = data;
		if (converters != null) {
			for (DataConverter c : converters) {
				ret = c.convert(ret);
			}
		}
		return ret;
	}
	
	/**
	 * @return the data passed through zero or more data converters
	 */
	public static Object convertData(Object data, List<DataConverter> converters) {
		Object ret = data;
		if (converters != null) {
			for (DataConverter c : converters) {
				ret = c.convert(ret);
			}
		}
		return ret;
	}

    /**
     *
     * @param data
     * @param results each element should be an Object[2] with the first being an Integer id, and the second being the value
     */
    public static void populate(BaseData data, List<Object[]> results) {
        for (Object[] row : results) {
            data.getData().put((Integer) row[0], row[1]);
        }
    }

	/**
	 * @return the result of evaluating the given definition against the given person, cast to the given type
	 */
	public static <T> T evaluateForPerson(PersonDataDefinition definition, Person person, Class<T> type) {
		PersonEvaluationContext context = new PersonEvaluationContext();
		context.setBasePersons(new PersonIdSet(person.getPersonId()));
		PersonDataService service = Context.getService(PersonDataService.class);
		try {
			PersonData data = service.evaluate(definition, context);
			return (T) data.getData().get(person.getPersonId());
		}
		catch (EvaluationException e) {
			throw new IllegalArgumentException("Unable to evaluate definition for person", e);
		}
	}

    /**
     * @return the result of evaluating the given definition against the given patient, cast to the given type
     */
    public static <T> T evaluateForPatient(PatientDataDefinition definition, Integer patientId, Class<T> type) {
        EvaluationContext context = new EvaluationContext();
        Cohort c = new Cohort();
        c.addMember(patientId);
        context.setBaseCohort(c);
        try {
            PatientData data = Context.getService(PatientDataService.class).evaluate(definition, context);
            return (T)data.getData().get(patientId);
        }
        catch (EvaluationException e) {
            throw new IllegalArgumentException("Unable to evaluate definition for patient", e);
        }
    }
}
