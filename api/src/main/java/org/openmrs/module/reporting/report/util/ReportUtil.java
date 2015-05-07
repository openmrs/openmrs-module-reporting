package org.openmrs.module.reporting.report.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleUtil;
import org.openmrs.module.reporting.common.ContentType;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.TextTemplateRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ReportUtil {
	
	// Logger
	private static Log log = LogFactory.getLog(ReportUtil.class);
	
	public static String toCsv(DataSet dataset) throws Exception {
		ReportRenderer rr = new CsvReportRenderer();
		ReportData rd = new ReportData();
		rd.setDataSets(new HashMap<String, DataSet>());
		rd.getDataSets().put("dataset", dataset);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		rr.render(rd, null, out);
		return out.toString();
	}
	
	public static void writeStringToFile(File f, String s) throws IOException {
		if (f.getAbsolutePath().endsWith(".gz")) {
			String fn = f.getAbsolutePath();
			File unzippedFile = new File(fn.substring(0, fn.length() - 3));
			FileUtils.writeStringToFile(unzippedFile, s, "UTF-8");
			compressFile(unzippedFile, f);
			FileUtils.deleteQuietly(unzippedFile);
		} else {
			FileUtils.writeStringToFile(f, s, "UTF-8");
		}
	}
	
	public static void appendStringToFile(File f, String s) throws IOException {
		String original = null;
		try {
			original = readStringFromFile(f);
		}
		catch (Exception e) {}
		writeStringToFile(f, (original == null ? s : original + System.getProperty("line.separator") + s));
	}
	
	public static String readStringFromFile(File f) throws IOException {
		String ret = null;
		if (f.getAbsolutePath().endsWith(".gz")) {
			String fn = f.getAbsolutePath();
			File unzippedFile = new File(fn.substring(0, fn.length() - 3));
			decompressFile(f, unzippedFile);
			ret = FileUtils.readFileToString(unzippedFile, "UTF-8");
			FileUtils.deleteQuietly(unzippedFile);
		} else {
			ret = FileUtils.readFileToString(f, "UTF-8");
		}
		return ret;
	}
	
	public static List<String> readLinesFromFile(File f) throws IOException {
		List<String> ret = new ArrayList<String>();
		String s = readStringFromFile(f);
		if (s != null) {
			for (String line : s.split(System.getProperty("line.separator"))) {
				ret.add(line);
			}
		}
		return ret;
	}
	
	public static void writeByteArrayToFile(File f, byte[] bytes) throws IOException {
		if (f.getAbsolutePath().endsWith(".gz")) {
			String fn = f.getAbsolutePath();
			File unzippedFile = new File(fn.substring(0, fn.length() - 3));
			FileUtils.writeByteArrayToFile(unzippedFile, bytes);
			compressFile(unzippedFile, f);
			FileUtils.deleteQuietly(unzippedFile);
		} else {
			FileUtils.writeByteArrayToFile(f, bytes);
		}
	}
	
	public static byte[] readByteArrayFromFile(File f) throws IOException {
		byte[] ret = null;
		if (f.getAbsolutePath().endsWith(".gz")) {
			String fn = f.getAbsolutePath();
			File unzippedFile = new File(fn.substring(0, fn.length() - 3));
			decompressFile(f, unzippedFile);
			ret = FileUtils.readFileToByteArray(unzippedFile);
			FileUtils.deleteQuietly(unzippedFile);
		} else {
			ret = FileUtils.readFileToByteArray(f);
		}
		return ret;
	}
	
	public static void compressFile(File inFile, File outFile) {
		FileInputStream in = null;
		GZIPOutputStream out = null;
		try {
			in = new FileInputStream(inFile);
			out = new GZIPOutputStream(new FileOutputStream(outFile));
			IOUtils.copy(in, out);
		}
		catch (Exception e) {
			log.warn("Unable to zip file: " + inFile);
		}
		finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}
	
	public static void decompressFile(File inFile, File outFile) {
		GZIPInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new GZIPInputStream(new FileInputStream(inFile));
			out = new FileOutputStream(outFile);
			IOUtils.copy(in, out);
		}
		catch (Exception e) {
			log.warn("Unable to unzip file: " + inFile);
		}
		finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	public static byte[] readByteArrayFromResource(String resourcePath) {
		InputStream is = null;
		try {
			is = OpenmrsClassLoader.getInstance().getResourceAsStream(resourcePath);
			return IOUtils.toByteArray(is);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Error reading resource from the classpath", e);
		}
		finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * Given a location on the classpath, return the contents of this resource as a String
	 */
	public static String readStringFromResource(String resourceName) {
		InputStream is = null;
		try {
			is = OpenmrsClassLoader.getInstance().getResourceAsStream(resourceName);
			return IOUtils.toString(is, "UTF-8");
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Unable to load resource: " + resourceName, e);
		}
		finally {
			IOUtils.closeQuietly(is);
		}
	}
	
	/**
	 * Looks up a resource on the class path, and returns a RenderingMode based on it
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static RenderingMode renderingModeFromResource(String label, String resourceName) {
		final ReportDesign design = new ReportDesign();
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("template");
		String extension = resourceName.substring(resourceName.lastIndexOf(".") + 1);
		resource.setExtension(extension);
		String contentType = "text/plain";
		for (ContentType type : ContentType.values()) {
			if (type.getExtension().equals(extension)) {
				contentType = type.getContentType();
			}
		}
		resource.setContentType(contentType);
		resource.setContents(readByteArrayFromResource(resourceName));
		design.getResources().add(resource);

		ReportRenderer renderer = null;
		if ("xls".equals(extension)) {
			renderer = new ExcelTemplateRenderer() {
				
				public ReportDesign getDesign(String argument) {
					return design;
				}
			};
		} else {
			renderer = new TextTemplateRenderer() {
				
				public ReportDesign getDesign(String argument) {
					return design;
				}
			};
		}
		return new RenderingMode(renderer, label, extension, null);
	}
	
	/**
	 * Convenience method attempts to load a class, If the ClassNotFoundException is thrown it wraps
	 * it into an APi exception which we don't have to catch
	 * 
	 * @param className the name of the class to load
	 * @return the loaded class object
	 */
	public static Class<?> loadClass(String className) {
		try {
			return (Class<?>) Context.loadClass(className);
		}
		catch (ClassNotFoundException e) {
			throw new APIException(e);
		}
	}

	/**
	 * @return a path like "org/openmrs/module/reporting/report/util" given the class ReportUtil.class
	 */
	public static String getPackageAsPath(Class<?> clazz) {
		return clazz.getPackage().getName().replace(".", "/");
	}
	
	/**
	 * Convenience method that collects available ReportProcessorConfigurations from global ReportProcessorConfigurations, as well as from the ReportDesign associated with the ReportRequest
	 * @param request
	 * @param modes
	 * @return
	 */
	public static List<ReportProcessorConfiguration> getAvailableReportProcessorConfigurations(ReportRequest request, ReportProcessorConfiguration.ProcessorMode ... modes){
		List<ReportProcessorConfiguration> processors = new ArrayList<ReportProcessorConfiguration>();
		for (ReportProcessorConfiguration rpc : Context.getService(ReportService.class).getGlobalReportProcessorConfigurations()){
			for (int i = 0; i < modes.length; i++){
				ReportProcessorConfiguration.ProcessorMode mode = modes[i];
				if (mode.equals(rpc.getProcessorMode())){
					processors.add(rpc);
					break;
				}	
			}	
		}
		// Find ReportDesign processors
		ReportService rs = Context.getService(ReportService.class);
		ReportDefinition rd = Context.getService(ReportDefinitionService.class).getDefinitionByUuid(request.getReportDefinition().getUuidOfMappedOpenmrsObject());
		//TODO: REPORT-314.   This is a hack, and should be changed when there's a direct link between reportRequest and reportDesign.
		//                    i.e., when there's a specific reportDesigns for each possible render, given a reportDefinition
		if (request.getRenderingMode() != null){
			List<ReportDesign> rdList = rs.getReportDesigns(rd, request.getRenderingMode().getRenderer().getClass(), false); //this is the join to report definition
			if (rdList != null){
				for (ReportDesign d : rdList) {
					for (ReportProcessorConfiguration rpc : d.getReportProcessors()){
						for (int i = 0; i < modes.length; i++){
							ReportProcessorConfiguration.ProcessorMode mode = modes[i];
							if (mode.equals(rpc.getProcessorMode())){
								log.debug("runReport matched request renderingMode to reportDesign.rendererType on " + request.getRenderingMode().getRenderer().getClass());
								processors.add(rpc);
								break;
							}	
						}	
					}
				}
			}
		}
		return processors;
	}
	
	/**
	 * Checks if we are running OpenMRS version 1.9 and above
	 * 
	 * @return true if we are running openmrs version 1.9 and above.
	 */
	public static boolean isOpenmrsVersionOnePointNineAndAbove() {
		return ModuleUtil.matchRequiredVersions(OpenmrsConstants.OPENMRS_VERSION_SHORT, "1.9.0");
	}

	public static void printTableContents(String tableName, String...columnNames) {
		StringBuilder sb = new StringBuilder();
		if (columnNames == null || columnNames.length == 0) {
			sb.append("select * ");
		}
		else {
			for (String columnName : columnNames) {
				sb.append(sb.length() == 0 ? "select " : ", ").append(columnName);
			}
		}
		sb.append(" from ").append(tableName);
		System.out.println(tableName + ": " + Context.getAdministrationService().executeSQL(sb.toString(), true));
	}

	public static void updateGlobalProperty(String propertyName, String propertyValue) {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(propertyName);
		if (gp == null) {
			gp = new GlobalProperty(propertyName);
		}
		gp.setPropertyValue(propertyValue);
		Context.getAdministrationService().saveGlobalProperty(gp);
	}
}
