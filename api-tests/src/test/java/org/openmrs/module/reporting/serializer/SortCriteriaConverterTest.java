/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.serializer;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class SortCriteriaConverterTest extends BaseModuleContextSensitiveTest {

	@Test
	public void shouldDeserializeLegacyNonStaticSortCriteriaElementClass() throws Exception {
		ReportingSerializer rs = new ReportingSerializer();
		String src = ReportUtil.readStringFromResource("org/openmrs/module/reporting/serializer/non-static-sort-elements.xml");;
		SortCriteria sortCriteria = rs.deserialize(src, SortCriteria.class);
		Assert.assertEquals(1, sortCriteria.getSortElements().size());
		SortCriteria.SortElement sortElement = sortCriteria.getSortElements().get(0);
		Assert.assertEquals("encounterDate", sortElement.getElementName());
		Assert.assertEquals(SortCriteria.SortDirection.ASC, sortElement.getDirection());
	}

	@Test
	public void shouldSerializeStaticSortCriteriaElement() throws Exception {
		ReportingSerializer rs = new ReportingSerializer();
		SortCriteria sc = new SortCriteria();
		sc.addSortElement("element1", SortCriteria.SortDirection.DESC);
		sc.addSortElement("element2", SortCriteria.SortDirection.ASC);
		String xml = rs.serialize(sc);
		String expected = ReportUtil.readStringFromResource("org/openmrs/module/reporting/serializer/static-sort-elements.xml");
		xml = StringUtils.deleteWhitespace(xml);
		expected = StringUtils.deleteWhitespace(xml);
		Assert.assertEquals(expected, xml);
	}

	@Test
	public void shouldDeserializeStaticSortCriteriaElement() throws Exception {
		ReportingSerializer rs = new ReportingSerializer();
		SortCriteria sc = new SortCriteria();
		sc.addSortElement("element1", SortCriteria.SortDirection.DESC);
		sc.addSortElement("element2", SortCriteria.SortDirection.ASC);
		String xml = rs.serialize(sc);

		SortCriteria fromXml = rs.deserialize(xml, SortCriteria.class);
		Assert.assertEquals(sc.getSortElements().size(), fromXml.getSortElements().size());
		for (SortCriteria.SortElement e1 : sc.getSortElements()) {
			SortCriteria.SortElement e2 = fromXml.getSortElement(e1.getElementName());
			Assert.assertEquals(e1.getElementName(), e2.getElementName());
			Assert.assertEquals(e1.getDirection(), e2.getDirection());
		}
	}
}
