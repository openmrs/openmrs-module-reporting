package org.openmrs.module.reporting.data.converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.converter.AttributeValueConverter;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class AttributeValueConverterTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
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
		Assert.assertEquals(String.class, value.getClass());
		Assert.assertEquals(stringValue.getValue(), value.toString());
	}

	/**
	 * @see AttributeValueConverter#convert(Object)
	 * @verifies return the passed in value if it is not attributable
	 */
	@Test
	public void convert_shouldReturnThePassedInValueIfItIsNotAttributable() throws Exception {
		PersonAttribute conceptValue = Context.getPersonService().getPersonAttribute(14);
		Object value = (new AttributeValueConverter(conceptValue.getAttributeType())).convert(conceptValue.getValue());
		Assert.assertEquals(Concept.class, value.getClass());
		Assert.assertEquals(conceptValue.getHydratedObject(), value);
	}
	
}