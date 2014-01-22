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
package org.openmrs.module.reporting.report.renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ReportRenderer that renders to a delimited text file
 */
public class DelimitedTextReportRenderer extends ReportDesignRenderer {
	
	transient protected final Log log = LogFactory.getLog(getClass());
	
	/**
     * @param design
	 * @return the filename extension for the particular type of delimited file
	 */
	public String getFilenameExtension(ReportDesign design) {
		return design.getPropertyValue("filenameExtension", "csv");
	}
	
	/**
     * @param design
	 * @return the delimiter that surrounds each column value, if applicable
	 */
	public String getTextDelimiter(ReportDesign design) {
		return design.getPropertyValue("textDelimiter", "\"");
	}

	/**
     * @param design
	 * @return the delimiter that separates each column value
	 */
	public String getFieldDelimiter(ReportDesign design) {
		return design.getPropertyValue("fieldDelimiter", ",");
	}

    /**
     * Defaults to \r\n, per http://tools.ietf.org/html/rfc4180#page-2
     * @param design
     * @return what to end each line with
     */
    public String getLineEnding(ReportDesign design) {
        return design.getPropertyValue("lineDelimiter", "\r\n");
    }

    /**
     * @param design
     * @return character encoding to use when writing the text output
     */
    public String getCharacterEncoding(ReportDesign design) {
        return design.getPropertyValue("characterEncoding", "UTF-8");
    }

    /**
     * @param design
     * @return regex defining characters that should be replaced by ? in output
     */
    public Pattern getBlacklistRegex(ReportDesign design) {
        String blacklistRegex = design.getPropertyValue("blacklistRegex", null);
        return blacklistRegex == null ? null : Pattern.compile(blacklistRegex);
    }

    /**
	 * @see org.openmrs.module.reporting.report.renderer.ReportRenderer#getRenderedContentType(ReportDefinition, String)
	 */
	public String getRenderedContentType(ReportDefinition model, String argument) {
        if (model.getDataSetDefinitions().size() > 1) {
            return "application/zip";
        } else {
            ReportDesign design = getDesign(argument);
            return "text/" + getFilenameExtension(design);
        }
	}

	/**
	 * Convenience method used to escape a string of text. Replaces " with "" (per the CSV standard), which is not
     * necessarily ideal behavior for other use cases.
	 * 
	 * @param	text 	The text to escape.
	 * @return	The escaped text.
	 */
	public String escape(String text) {
		if (text == null) {
			return null;
		}
		else {
			return text.replaceAll("\\\"", "\"\"");
		}
	}			
	
	/**
	 * @see ReportRenderer#getFilename(ReportDefinition, String)
	 */
	public String getFilename(ReportDefinition reportDefinition, String argument) {
		ReportDesign design = getDesign(argument);
		String dateStr = DateUtil.formatDate(new Date(), "yyyy-MM-dd-hhmmss");
        String extension = reportDefinition.getDataSetDefinitions().size() > 1 ? "zip" : getFilenameExtension(design);
		return getFilenameBaseForName(reportDefinition.getName(), new HashSet<String>()) + "_" + dateStr + "." + extension;
	}

    /**
     * @param definitionName name to base the returned filename on (e.g. a ReportDefinition name or a data set key)
     * @param alreadyUsed names that have already been used, and should be avoided; the returned value will be added to this set
     * @return definitionName, with characters unsuitable for a filename removed, and a suffix added if necessary for uniqueness
     */
    private String getFilenameBaseForName(String definitionName, Set<String> alreadyUsed) {
        String clean = definitionName.replaceAll("[^a-zA-Z_0-9 ]", "");
        if (alreadyUsed.contains(clean)) {
            int i = 2;
            while (alreadyUsed.contains(clean + "_" + i)) {
                ++i;
            }
            clean = clean + "_" + i;
        }
        alreadyUsed.add(clean);
        return clean;
    }
	
	/**
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {
		DataSet dataset = results.getDataSets().values().iterator().next();
		
		ReportDesign design = getDesign(argument);
		String textDelimiter = getTextDelimiter(design);
		String fieldDelimiter = getFieldDelimiter(design);
        String lineEnding = getLineEnding(design);
        String characterEncoding = getCharacterEncoding(design);
        Pattern blacklistRegex = getBlacklistRegex(design);

        if (results.getDataSets().size() > 1) {
            ZipOutputStream zip = new ZipOutputStream(out);
            Set<String> usedFilenames = new HashSet<String>();
            for (Map.Entry<String, DataSet> e : results.getDataSets().entrySet()) {
                String fn = getFilenameBaseForName(e.getKey(), usedFilenames) + "." + getFilenameExtension(getDesign(argument));
                zip.putNextEntry(new ZipEntry(fn));
                writeDataSet(e.getValue(), zip, textDelimiter, fieldDelimiter, lineEnding, characterEncoding, blacklistRegex);
                zip.closeEntry();
            }
            zip.finish();
        } else {
            writeDataSet(dataset, out, textDelimiter, fieldDelimiter, lineEnding, characterEncoding, blacklistRegex);
        }
	}

    /**
     * Only visible for testing
     *
     * @param dataset
     * @param out
     * @param textDelimiter
     * @param fieldDelimiter
     * @param lineEnding
     * @param characterEncoding
     * @param blacklist
     * @throws IOException
     */
    void writeDataSet(DataSet dataset, OutputStream out, String textDelimiter, String fieldDelimiter, String lineEnding,
                      String characterEncoding, Pattern blacklist) throws IOException {
        Writer w = new OutputStreamWriter(out, characterEncoding);
        List<DataSetColumn> columns = dataset.getMetaData().getColumns();

		// header row
		for (Iterator<DataSetColumn> i = columns.iterator(); i.hasNext();) {
			DataSetColumn column = i.next();
			w.write(textDelimiter + filterBlacklist(escape(column.getName()), blacklist) + textDelimiter);
			if (i.hasNext()) {
				w.write(fieldDelimiter);
			}
		}
		w.write(lineEnding);

		// data rows
		for (DataSetRow row : dataset) {
			for (Iterator<DataSetColumn> i = columns.iterator(); i.hasNext();) {
				DataSetColumn column = i.next();
				Object colValue = row.getColumnValue(column);
				w.write(textDelimiter);
				if (colValue != null) {
                    String toPrint;
					if (colValue instanceof Cohort) {
                        toPrint = escape(Integer.toString(((Cohort) colValue).size()));
					} else if (colValue instanceof IndicatorResult) {
                        toPrint = ((IndicatorResult) colValue).getValue().toString();
					}
					else {
						// this check is because a logic EmptyResult .toString() -> null
						String temp = escape(colValue.toString());
						if (temp != null) {
                            toPrint = temp;
                        } else {
                            toPrint = "";
                        }
					}
                    w.write(filterBlacklist(toPrint, blacklist));
				}
				w.write(textDelimiter);
				if (i.hasNext()) {
					w.write(fieldDelimiter);
				}
			}
			w.write(lineEnding);
		}

		w.flush();
	}

    /**
     * @param input
     * @param blacklist
     * @return replaces any matches of blacklist in input with ?
     */
    private String filterBlacklist(String input, Pattern blacklist) {
        if (blacklist == null) {
            return input;
        }
        return blacklist.matcher(input).replaceAll("?");
    }

}
