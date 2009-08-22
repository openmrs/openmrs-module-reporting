package org.openmrs.module.report.renderer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openmrs.annotation.Handler;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;

/**
 * Report renderer that produces an Excel pre-2007 workbook with one sheet per dataset in the report.
 */
@Handler
public class XlsReportRenderer extends AbstractReportRenderer {

    /**
     * @see org.openmrs.module.report.renderer.ReportRenderer#getFilename(org.openmrs.module.report.ReportDefinition, java.lang.String)
     */
    public String getFilename(ReportDefinition schema, String argument) {
        return schema.getName() + ".xls";
    }

    /**
     * @see org.openmrs.module.report.renderer.ReportRenderer#getLabel()
     */
    public String getLabel() {
        return "XLS (Excel 97-2003)";
    }

    /**
     * @see org.openmrs.module.report.renderer.ReportRenderer#getRenderedContentType(org.openmrs.module.report.ReportDefinition, java.lang.String)
     */
    public String getRenderedContentType(ReportDefinition schema, String argument) {
        return "application/vnd.ms-excel";
    }

    /**
     * @see org.openmrs.module.report.renderer.ReportRenderer#getRenderingModes(org.openmrs.module.report.ReportDefinition)
     */
    public Collection<RenderingMode> getRenderingModes(ReportDefinition schema) {
        // Don't return *quite* the lowest priority, so that this appears above CSV and TSV.
        return Collections.singleton(new RenderingMode(this, this.getLabel(), null, Integer.MIN_VALUE + 1));
    }

    /**
     * @see org.openmrs.module.report.renderer.ReportRenderer#render(org.openmrs.module.report.ReportData, java.lang.String, java.io.OutputStream)
     * @should render ReportData to an xls file
     */
    public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {
        HSSFWorkbook wb = new HSSFWorkbook();
        ExcelStyleHelper styleHelper = new ExcelStyleHelper(wb);
        for (Map.Entry<String, DataSet> e : reportData.getDataSets().entrySet()) {
            DataSet dataset = e.getValue();
            HSSFSheet sheet = wb.createSheet(ExcelSheetHelper.fixSheetName(e.getKey()));
            ExcelSheetHelper helper = new ExcelSheetHelper(sheet);
            List<DataSetColumn> columnList = dataset.getDataSetDefinition().getColumns();
            
            // Display top header
            for (DataSetColumn column : columnList) {
            	if (isDisplayColumn(column.getColumnKey()))
            		helper.addCell(column.getDisplayName(), styleHelper.getStyle("bold,border=bottom"));
            }
            for (Iterator<Map<DataSetColumn, Object>> i = dataset.iterator(); i.hasNext(); ) {
                helper.nextRow();
                Map<DataSetColumn, Object> row = i.next();
                for (DataSetColumn column : columnList) {

                	// If the column is meant for display, we display it
                	if (isDisplayColumn(column.getColumnKey())) { 
	                	Object cellValue = row.get(column);
	                    HSSFCellStyle style = null;
	                    if (cellValue instanceof Date) {
	                        style = styleHelper.getStyle("date");
	                    }
	                    helper.addCell(cellValue, style);
                	}
                }
            }
        }
        
        wb.write(out);
    }

    /**
     * This throws an {@link UnsupportedOperationException} because a binary Excel file cannot be outputted to a Writer.
     * @see org.openmrs.module.report.renderer.ReportRenderer#render(org.openmrs.module.report.ReportData, java.lang.String, java.io.Writer)
     */
    public void render(ReportData reportData, String argument, Writer writer) throws IOException, RenderingException {
        throw new UnsupportedOperationException();
    }

}
