/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
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
