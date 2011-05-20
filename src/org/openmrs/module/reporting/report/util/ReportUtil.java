package org.openmrs.module.reporting.report.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;

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
			File unzippedFile = new File(fn.substring(0, fn.length()-3));
			FileUtils.writeStringToFile(unzippedFile, s, "UTF-8");
			compressFile(unzippedFile, f);
			FileUtils.deleteQuietly(unzippedFile);
		}
		else {
			FileUtils.writeStringToFile(f, s, "UTF-8");
		}
	}
	
	public static String readStringFromFile(File f) throws IOException {
		String ret = null;
		if (f.getAbsolutePath().endsWith(".gz")) {
			String fn = f.getAbsolutePath();
			File unzippedFile = new File(fn.substring(0, fn.length()-3));
			decompressFile(f, unzippedFile);
			ret = FileUtils.readFileToString(unzippedFile, "UTF-8");
			FileUtils.deleteQuietly(unzippedFile);
		}
		else {
			ret = FileUtils.readFileToString(f, "UTF-8");
		}
		return ret;
	}
	
	public static void writeByteArrayToFile(File f, byte[] bytes) throws IOException {
		if (f.getAbsolutePath().endsWith(".gz")) {
			String fn = f.getAbsolutePath();
			File unzippedFile = new File(fn.substring(0, fn.length()-3));
			FileUtils.writeByteArrayToFile(unzippedFile, bytes);
			compressFile(unzippedFile, f);
			FileUtils.deleteQuietly(unzippedFile);
		}
		else {
			FileUtils.writeByteArrayToFile(f, bytes);
		}
	}
	
	public static byte[] readByteArrayFromFile(File f) throws IOException {
		byte[] ret = null;
		if (f.getAbsolutePath().endsWith(".gz")) {
			String fn = f.getAbsolutePath();
			File unzippedFile = new File(fn.substring(0, fn.length()-3));
			decompressFile(f, unzippedFile);
			ret = FileUtils.readFileToByteArray(unzippedFile);
			FileUtils.deleteQuietly(unzippedFile);
		}
		else {
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
}
