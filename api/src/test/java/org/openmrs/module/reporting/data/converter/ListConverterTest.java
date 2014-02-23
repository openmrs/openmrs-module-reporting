package org.openmrs.module.reporting.data.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.common.TimeQualifier;

import java.util.Arrays;
import java.util.List;

public class ListConverterTest {

	public List<Integer> getList() {
		return Arrays.asList(10, 20, 30, 40, 50);
	}

	@Test
	public void convert_shouldReturnASpecificItemIndexIfValid() throws Exception {
		ListConverter c = new ListConverter(1, Integer.class);
		Assert.assertEquals(20, c.convert(getList()));
	}

	@Test
	public void convert_shouldReturnNullIfSpecificItemIndexIsNotValid() throws Exception {
		ListConverter c = new ListConverter(10, Integer.class);
		Assert.assertEquals(null, c.convert(getList()));
	}

	@Test
	public void convert_shouldReturnFirstItem() throws Exception {
		ListConverter c = new ListConverter(TimeQualifier.FIRST, 1, Integer.class);
		Assert.assertEquals(10, c.convert(getList()));
	}

	@Test
	public void convert_shouldReturnFirst3Items() throws Exception {
		ListConverter c = new ListConverter(TimeQualifier.FIRST, 3, Integer.class);
		List<Integer> ret = (List<Integer>)c.convert(getList());
		Assert.assertEquals(3, ret.size());
		Assert.assertEquals(10, ret.get(0).intValue());
		Assert.assertEquals(20, ret.get(1).intValue());
		Assert.assertEquals(30, ret.get(2).intValue());
	}

	@Test
	public void convert_shouldReturnLastItem() throws Exception {
		ListConverter c = new ListConverter(TimeQualifier.LAST, 1, Integer.class);
		Assert.assertEquals(50, c.convert(getList()));
	}

	@Test
	public void convert_shouldReturnLast3Items() throws Exception {
		ListConverter c = new ListConverter(TimeQualifier.LAST, 3, Integer.class);
		List<Integer> ret = (List<Integer>)c.convert(getList());
		Assert.assertEquals(3, ret.size());
		Assert.assertEquals(50, ret.get(0).intValue());
		Assert.assertEquals(40, ret.get(1).intValue());
		Assert.assertEquals(30, ret.get(2).intValue());
	}
}