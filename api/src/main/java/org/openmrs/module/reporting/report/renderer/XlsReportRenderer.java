package org.openmrs.module.reporting.report.renderer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * Report renderer that produces an Excel pre-2007 workbook with one sheet per dataset in the report.
 */
@Handler
@Localized("reporting.XlsReportRenderer")
public class XlsReportRenderer extends ReportTemplateRenderer {

	private Log log = LogFactory.getLog(this.getClass());
	
	public XlsReportRenderer() { }
    
    /**
     * @see ReportRenderer#getRenderedContentType(ReportDefinition, String)
     */
    public String getRenderedContentType(ReportDefinition schema, String argument) {
        return "application/vnd.ms-excel";
    }

	/**
	 * @see ReportRenderer#getFilename(org.openmrs.module.reporting.report.definition.ReportDefinition, String)
	 */
	public String getFilename(ReportDefinition definition, String argument) {
		String fileName = super.getFilename(definition, argument);
		if (!fileName.endsWith(".xls")) {
			fileName += ".xls";
		}
		return fileName;
	}

    /**
     * @see ReportRenderer#render(ReportData, String, OutputStream)
     * @should render ReportData to an xls file
     */
    public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {
        ReportDesign design = getDesign(argument);
    	HSSFWorkbook wb = getExcelTemplate(design);
        
        if (wb != null) {
        	ExcelTemplateRenderer templateRenderer = new ExcelTemplateRenderer();
			templateRenderer.render(reportData, argument, out);
        }
		else {
        	wb = new HSSFWorkbook();
            ExcelStyleHelper styleHelper = new ExcelStyleHelper(wb);
            for (Map.Entry<String, DataSet> e : reportData.getDataSets().entrySet()) {
                DataSet dataset = e.getValue();
                HSSFSheet sheet = wb.createSheet(ExcelSheetHelper.fixSheetName(e.getKey()));
                ExcelSheetHelper helper = new ExcelSheetHelper(sheet);
                List<DataSetColumn> columnList = dataset.getMetaData().getColumns();
                
                // Display top header
                for (DataSetColumn column : columnList) {
                	helper.addCell(column.getLabel(), styleHelper.getStyle("bold,border=bottom"));
                }
                for (DataSetRow row : dataset ) {
                    helper.nextRow();
                    for (DataSetColumn column : columnList) {
                    	Object cellValue = row.getColumnValue(column);
                        CellStyle style = null;
                        if (cellValue instanceof Date) {
                            style = styleHelper.getStyle("date");
                        }
                        helper.addCell(cellValue, style);
                    }
                }
            }
            
            wb.write(out);
        }
    }

	/**
	 * @return an Excel Workbook for the given argument
	 */
	protected HSSFWorkbook getExcelTemplate(ReportDesign design) throws IOException {
		HSSFWorkbook wb = null;
		InputStream is = null;
		try {
			ReportDesignResource r = getTemplate(design);
			is = new ByteArrayInputStream(r.getContents());
			POIFSFileSystem fs = new POIFSFileSystem(is);
			wb = new HSSFWorkbook(fs);
		}
		catch (Exception e) {
			log.debug("No template file found, will use default Excel output");
		}
		finally {
			IOUtils.closeQuietly(is);
		}
		return wb;
	}
}
