/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Concept;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.converter.AttributeValueConverter;
import org.openmrs.test.jupiter.BaseContextSensitiveTest;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

public class AttributeValueConverterTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@BeforeEach" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	/**
	 * @see AttributeValueConverter#convert(Object)
	 * @verifies convert a serialized attribute value into its hydrated object form
	 */
	@Test
	public void convert_shouldConvertASerializedAttributeValueIntoItsHydratedObjectForm() throws Exception {
		PersonAttribute stringValue = Context.getPersonService().getPersonAttribute(10);
		Object value = (new AttributeValueConverter(stringValue.getAttributeType())).convert(stringValue.getValue());
		Assertions.assertEquals(String.class, value.getClass());
		Assertions.assertEquals(stringValue.getValue(), value.toString());
	}

	/**
	 * @see AttributeValueConverter#convert(Object)
	 * @verifies return the passed in value if it is not attributable
	 */
	@Test
	public void convert_shouldReturnThePassedInValueIfItIsNotAttributable() throws Exception {
		PersonAttribute conceptValue = Context.getPersonService().getPersonAttribute(14);
		Object value = (new AttributeValueConverter(conceptValue.getAttributeType())).convert(conceptValue.getValue());
		Assertions.assertEquals(Concept.class, value.getClass());
		Assertions.assertEquals(conceptValue.getHydratedObject(), value);
	}
	
}