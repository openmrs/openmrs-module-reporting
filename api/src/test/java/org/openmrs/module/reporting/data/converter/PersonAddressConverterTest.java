package org.openmrs.module.reporting.data.converter;

import org.junit.Assert;

import org.junit.Test;
import org.openmrs.PersonAddress;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PersonAddressConverterTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see PersonAddressConverter#convert(Object)
	 * @verifies convert a Person name into a String using a format expression
	 */
	@Test
	public void convert_shouldConvertAPersonAddressIntoAStringUsingAFormatExpression() throws Exception {
		PersonAddress pa = new PersonAddress();
		pa.setCountyDistrict("Suffolk");
		pa.setCityVillage("Boston");
		pa.setStateProvince("MA");
		pa.setCountry("USA");
		Object result = (new ObjectFormatter("{cityVillage}, {stateProvince}")).convert(pa);
		Assert.assertEquals("Boston, MA", result.toString());
	}
}