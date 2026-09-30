/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.ProgramWorkflow;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.converter.AttributeValueConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.CurrentPatientStateDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientObjectDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PreferredIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * The logic that evaluates a {@link SimplePatientDataSetDefinition} and produces an {@link DataSet}
 * @see SimplePatientDataSetDefinition
 */
@Handler(supports={SimplePatientDataSetDefinition.class})
public class SimplePatientDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public SimplePatientDataSetEvaluator() { }
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 * @should evaluate a SimplePatientDataSetDefinition
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {

		SimplePatientDataSetDefinition definition = (SimplePatientDataSetDefinition) dataSetDefinition;

        PatientDataSetDefinition d = new PatientDataSetDefinition();
        for (PatientIdentifierType t : definition.getIdentifierTypes()) {
            PreferredIdentifierDataDefinition pidd = new PreferredIdentifierDataDefinition();
            pidd.setIdentifierType(t);
            d.addColumn(t.getName(), pidd, "", new PropertyConverter(PatientIdentifier.class, "identifier"));
        }

        for (String s : definition.getPatientProperties()) {
            try {
                PatientObjectDataDefinition podd = new PatientObjectDataDefinition();
                d.addColumn(s, podd, "", new PropertyConverter(Patient.class, s));
            }
            catch (Exception e) {
                log.error("Unable to get property " + s + " on patient for dataset", e);
            }
        }

        for (PersonAttributeType t : definition.getPersonAttributeTypes()) {
            PersonAttributeDataDefinition padd = new PersonAttributeDataDefinition(t);
            d.addColumn(t.getName(), padd, "", new PropertyConverter(PersonAttribute.class, "value"), new AttributeValueConverter(t));
        }

        for (ProgramWorkflow t : definition.getProgramWorkflows()) {
            String name = ObjectUtil.format(t.getProgram()) + " - " + ObjectUtil.format(t);
            CurrentPatientStateDataDefinition cpsdd = new CurrentPatientStateDataDefinition();
            cpsdd.setWorkflow(t);
            d.addColumn(name, cpsdd, "", new ObjectFormatter());
        }

        SimpleDataSet dataSet = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, context);
        dataSet.setDefinition(definition);
        dataSet.setContext(context);

		return dataSet;
	}
}
