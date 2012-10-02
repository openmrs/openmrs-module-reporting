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
package org.openmrs.module.reporting.report.processor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.springframework.stereotype.Component;

/**
 * A ReportProcessor which saves the rendered report to disk
 */
@Component
public class DiskReportProcessor implements ReportProcessor {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see ReportProcessor#getConfigurationPropertyNames()
	 */
	public List<String> getConfigurationPropertyNames() {
		List<String> ret = new ArrayList<String>();
		ret.add("saveLocation");
		ret.add("compressOutput");
		ret.add("decompressOutput");
		return ret;
	}
	
	/**
	 * Performs some action on the given report
	 * 
	 * @param report the Report to process
	 */
	public void process(Report report, Properties configuration) {
		
		try {
			String folderName = configuration.getProperty("saveLocation");
			File folder = new File(folderName);
			if (!folder.isDirectory()) {
				throw new IllegalArgumentException(folderName + " is not a valid folder");
			}
			
			//Create the folder if it does not exist
			if (!folder.exists()) {
				folder.mkdir();
			}
			
			ReportRenderer renderer = report.getRequest().getRenderingMode().getRenderer();
			String fileName = renderer.getFilename(report.getReportData().getDefinition(), null);
			String fileNameWithoutExt = fileName.substring(0, fileName.indexOf('.'));
			String extension = fileName.substring(fileName.indexOf('.') + 1);
			
			if ("true".equals(configuration.getProperty("compressOutput"))) {
				try {
					File file = getUniqueFileName(folderName, fileNameWithoutExt, "zip");
					FileOutputStream fos = new FileOutputStream(file);
					ZipOutputStream zos = new ZipOutputStream(fos);
					ZipEntry zipEntry = new ZipEntry(report.getReportData().getDefinition().getName());
					zos.putNextEntry(zipEntry);
					zos.write(report.getRenderedOutput());
					zos.closeEntry();
					zos.close();
					return;
				}
				catch (Exception ex) {
					log.error("Failed to compress report: " + report.getReportData().getDefinition().getName(), ex);
				}
			} else if ("true".equals(configuration.getProperty("deCompressOutput"))) {
				try {
					ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(report.getRenderedOutput()));
					for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
						zip.getNextEntry();
						FileOutputStream fos = new FileOutputStream(new File(folderName, entry.getName() + "." + extension));
						IOUtils.copy(zip, fos);
						fos.close();
					}
					return;
				}
				catch (Exception ex) {
					log.error("Failed to decompress report: " + report.getReportData().getDefinition().getName(), ex);
				}
			}
			
			//Either no compressOutput/deCompressOutput properties, or we got an error trying to do so.
			File file = getUniqueFileName(folderName, fileNameWithoutExt, extension);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(report.getRenderedOutput());
			fos.close();
		}
		catch (Exception e) {
			throw new RuntimeException("Error occurred while saving report to disk", e);
		}
	}
	
	/**
	 * Gets a file object representing a unique file name.
	 * 
	 * @param folderName the name of the folder where the file belongs.
	 * @param fileName the file name without extension.
	 * @param extension the file extension.
	 * @return the file object.
	 */
	private File getUniqueFileName(String folderName, String fileName, String extension) {
		File file = new File(folderName, fileName + "." + extension);
		int count = 0;
		while (file.exists()) {
			count++;
			file = new File(folderName, fileName + count + "." + extension);
		}
		
		return file;
	}
}
