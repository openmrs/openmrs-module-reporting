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
package org.openmrs.module.reporting;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;

/**
 * Utility methods for use in reporting tests
 */
public class ReportingTestUtils {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Prints the passed dataset to the console
	 */
	public static void printDataSetToConsole(DataSet d) {

		Map<DataSetColumn, Integer> columnLengthMap = new LinkedHashMap<DataSetColumn, Integer>();
		for (DataSetColumn c : d.getMetaData().getColumns()) {
			columnLengthMap.put(c, c.getLabel().length());
		}
		for (Iterator<DataSetRow> i = d.iterator(); i.hasNext();) {
			DataSetRow r = i.next();
			for (DataSetColumn c : r.getColumnValues().keySet()) {
				String val = ObjectUtil.nvlStr(r.getColumnValue(c), "");
				if (columnLengthMap.get(c) < val.length()) {
					columnLengthMap.put(c, val.length());
				}
			}
		}
	
		StringBuilder output = new StringBuilder();
		for (Map.Entry<DataSetColumn, Integer> c : columnLengthMap.entrySet()) {
			StringBuilder n = new StringBuilder(c.getKey().getLabel());
			while (n.length() < c.getValue()) {
				n.append(" ");
			}
			output.append(n.toString() + "\t");
		}
		output.append("\n");
		for (Map.Entry<DataSetColumn, Integer> c : columnLengthMap.entrySet()) {
			StringBuilder n = new StringBuilder();
			while (n.length() < c.getValue()) {
				n.append("-");
			}
			output.append(n.toString() + "\t");
		}
		output.append("\n");
		for (Iterator<DataSetRow> i = d.iterator(); i.hasNext();) {
			DataSetRow r = i.next();
			for (Map.Entry<DataSetColumn, Integer> c : columnLengthMap.entrySet()) {
				StringBuilder n = new StringBuilder(ObjectUtil.nvlStr(r.getColumnValue(c.getKey()), ""));
				while (n.length() < c.getValue()) {
					n.append(" ");
				}
				output.append(n + "\t");
			}
			output.append("\n");
		}
		System.out.println(output.toString());
	}
}