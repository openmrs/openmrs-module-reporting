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

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.openmrs.module.reporting.common.SortCriteria;

/**
 * Defines how SortCriteria can be converted.
 * This requires a custom convertor because SortElements used to be implemented as a non-static inner class
 * and this is no longer supported by Java
 * This serialiazer is designed to match the legacy format, but ignore elements within the SortElement
 * that are unrecognized - eg. <outer-class reference="535"/>
 *
 * Example:
 *
 * 		<org.openmrs.module.reporting.common.SortCriteria id="535">
 * 			<sortElements id="536">
 * 				<org.openmrs.module.reporting.common.SortCriteria_-SortElement id="537">
 * 					<elementName>encounterDate</elementName>
 * 					<direction>ASC</direction>
 * 				</org.openmrs.module.reporting.common.SortCriteria_-SortElement>
 * 			</sortElements>
 * 		</org.openmrs.module.reporting.common.SortCriteria>
 *
 */
public class SortCriteriaConverter implements Converter {

	public boolean canConvert(Class clazz) {
		return clazz == SortCriteria.class;
	}

	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		SortCriteria sc = (SortCriteria) value;
		writer.startNode("sortElements");
		for (SortCriteria.SortElement element : sc.getSortElements()) {
			writer.startNode("org.openmrs.module.reporting.common.SortCriteria$SortElement");

			// Element Name
			writer.startNode("elementName");
			writer.setValue(element.getElementName());
			writer.endNode();

			// Element Direction
			writer.startNode("direction");
			writer.setValue(element.getDirection().toString());
			writer.endNode();

			writer.endNode(); // sortElement
		}
		writer.endNode(); // sortElements
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		SortCriteria sc = new SortCriteria();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if ("sortElements".equals(reader.getNodeName())) {
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					if (reader.getNodeName().toLowerCase().contains("sortelement")) {
						SortCriteria.SortElement element = new SortCriteria.SortElement();
						while (reader.hasMoreChildren()) {
							reader.moveDown();
							if ("elementName".equals(reader.getNodeName())) {
								element.setElementName(reader.getValue());
							}
							else if ("direction".equals(reader.getNodeName())) {
								element.setDirection(SortCriteria.SortDirection.valueOf(reader.getValue()));
							}
							reader.moveUp();
						}
						sc.getSortElements().add(element);
					}
					reader.moveUp();
				}
			}
			reader.moveUp();
		}
		return sc;
	}
}
