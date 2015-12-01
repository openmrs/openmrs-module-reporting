package org.openmrs.module.reporting.data.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.common.DateUtil;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class MapConverterTest {

	@Test
	public void convert_shouldHandleKeyValueProperty() throws Exception {
		MapConverter c = new MapConverter(" = ", null, null, null);
		checkVal(c, "Key1 = Value1", "Key1", "Value1");
	}

	@Test
	public void convert_shouldHandleEntryProperty() throws Exception {
		MapConverter c = new MapConverter(": ", " | ", null, null);
		checkVal(c, "Key1: Value1 | Key2: Value2", "Key1", "Value1", "Key2", "Value2");
	}

	@Test
	public void convert_shouldHandleKeyConverter() throws Exception {
		BooleanConverter bc = new BooleanConverter("oui", "non", "?");
		MapConverter c = new MapConverter(" = ", " and ", bc, null);
		checkVal(c, "oui = yes and non = no", Boolean.TRUE, "yes", Boolean.FALSE, "no");
	}

	@Test
	public void convert_shouldHandleValueConverter() throws Exception {
		BooleanConverter bc = new BooleanConverter("oui", "non", "?");
		MapConverter c = new MapConverter(null, null, null, bc);
		checkVal(c, "A1:oui,A2:non", "A1", Boolean.TRUE, "A2", Boolean.FALSE);
	}

	@Test
	public void convert_shouldHandleNulls() throws Exception {
		MapConverter c = new MapConverter();
		checkVal(c, "Key1:Value1", "Key1", "Value1", "Key2", null);
		c.setIncludeNullValues(true);
		checkVal(c, "Key1:Value1,Key2:null", "Key1", "Value1", "Key2", null);
		c.setIncludeNullValues(false);
		c.setValueConverter(new ExistenceConverter("Here", "Not here"));
		checkVal(c, "Key1:Here,Key2:Not here", "Key1", "Value1", "Key2", null);
	}

	private void checkVal(MapConverter converter, String expected, Object...keyVals) {
		Map<Object, Object> m = new LinkedHashMap<Object, Object>();
		for (int i=0; i< keyVals.length; i+=2) {
			m.put(keyVals[i], keyVals[i+1]);
		}
		Object val = converter.convert(m);
		Assert.assertEquals(expected, val);
	}
}