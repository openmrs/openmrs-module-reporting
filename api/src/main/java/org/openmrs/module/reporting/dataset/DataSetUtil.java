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
package org.openmrs.module.reporting.dataset;

import org.apache.commons.io.IOUtils;
import org.openmrs.module.reporting.common.ObjectUtil;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility methods for working with Data Sets
 */
public class DataSetUtil {

	/**
	 * Prints the passed dataset to the console
	 */
	public static void printDataSet(DataSet d, OutputStream out) {

		Map<DataSetColumn, Integer> columnLengthMap = new LinkedHashMap<DataSetColumn, Integer>();
		for (DataSetColumn c : d.getMetaData().getColumns()) {
			columnLengthMap.put(c, c.toString().length());
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
			StringBuilder n = new StringBuilder(c.getKey().toString());
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

		try {
			IOUtils.write(output.toString(), out);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to write dataset to outputstream", e);
		}
	}


    public static List<Map<String, Object>> simplify(DataSet dataSet) {
        List<Map<String, Object>> simplified = new ArrayList<Map<String, Object>>();
        for (DataSetRow row : dataSet) {
            simplified.add(row.getColumnValuesByKey());
        }
        return simplified;
    }
}
