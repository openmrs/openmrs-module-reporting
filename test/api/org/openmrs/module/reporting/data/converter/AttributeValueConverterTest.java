package org.openmrs.module.reporting.data.converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.converter.AttributeValueConverter;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class AttributeValueConverterTest extends BaseModuleContextSensitiveTest {
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
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
}