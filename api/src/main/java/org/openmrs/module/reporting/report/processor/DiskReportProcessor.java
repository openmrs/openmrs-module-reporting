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

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * A ReportProcessor which saves the rendered report to disk
 */
@Component
public class DiskReportProcessor implements ReportProcessor {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public static final String SAVE_LOCATION = "saveLocation";
	public static final String COMPRESS_OUTPUT = "compressOutput";
	public static final String DECOMPRESS_OUTPUT = "decompressOutput";
	
	/**
	 * @see ReportProcessor#getConfigurationPropertyNames()
	 */
	public List<String> getConfigurationPropertyNames() {
		List<String> ret = new ArrayList<String>();
		ret.add(SAVE_LOCATION);
		ret.add(COMPRESS_OUTPUT);
		ret.add(DECOMPRESS_OUTPUT);
		return ret;
	}
	
	/**
	 * Saves the report to disk
	 * @param report the Report to process
	 */
	public void process(Report report, Properties configuration) {
		
		try {
			String folderName = configuration.getProperty(SAVE_LOCATION);
			File folder = new File(folderName);

			if (folder.exists() && !folder.isDirectory()) {
				throw new IllegalArgumentException(folderName + " is not a valid folder");
			}
			
			//Create the folder if it does not exist
			if (!folder.exists()) {
				if(!folder.mkdir()) {
                    throw new IllegalArgumentException(folderName + " could not be created");
                }
			}
			
			ReportRenderer renderer = report.getRequest().getRenderingMode().getRenderer();
			String fileName = renderer.getFilename(report.getRequest());
			String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
			String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
			
			if ("true".equals(configuration.getProperty(COMPRESS_OUTPUT))) {
				ZipOutputStream zos = null;
				try {
					File file = getUniqueFileName(folderName, fileNameWithoutExt, "zip");
					zos = new ZipOutputStream(new FileOutputStream(file));
					ZipEntry zipEntry = new ZipEntry(report.getRequest().getReportDefinition().getParameterizable().getName());
					zos.putNextEntry(zipEntry);
					zos.write(report.getRenderedOutput());
					zos.closeEntry();
					return;
				}
				catch (Exception ex) {
					log.error("Failed to compress report: " + report.getRequest().getReportDefinition().getParameterizable().getName(), ex);
				}
				finally {
					IOUtils.closeQuietly(zos);
				}
			} 
			else if ("true".equals(configuration.getProperty(DECOMPRESS_OUTPUT))) {
				try {
					ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(report.getRenderedOutput()));
					for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
						zip.getNextEntry();
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream(new File(folderName, entry.getName() + "." + extension));
							IOUtils.copy(zip, fos);
						}
						finally {
							IOUtils.closeQuietly(fos);
						}
					}
					return;
				}
				catch (Exception ex) {
					log.error("Failed to decompress report: " + report.getReportData().getDefinition().getName(), ex);
				}
			}
			
			//Either no compressOutput/deCompressOutput properties, or we got an error trying to do so.
			File file = getUniqueFileName(folderName, fileNameWithoutExt, extension);
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				fos.write(report.getRenderedOutput());
			}
			finally {
				IOUtils.closeQuietly(fos);
			}
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
