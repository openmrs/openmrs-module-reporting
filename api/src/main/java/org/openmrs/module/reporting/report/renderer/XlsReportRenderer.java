package org.openmrs.module.reporting.report.renderer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ExcelBuilder;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.ReportRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Report renderer that produces an Excel pre-2007 workbook with one sheet per dataset in the report.
 */
@Handler
@Localized("reporting.XlsReportRenderer")
public class XlsReportRenderer extends ReportTemplateRenderer {

	private Log log = LogFactory.getLog(this.getClass());

	public static String INCLUDE_DATASET_NAME_AND_PARAMETERS_PROPERTY = "includeDataSetNameAndParameters";
    public static String PASSWORD_PROPERTY = "password";
	
	public XlsReportRenderer() { }
    
    /**
     * @see ReportRenderer#getRenderedContentType(org.openmrs.module.reporting.report.ReportRequest)
     */
    public String getRenderedContentType(ReportRequest request) {
        return "application/vnd.ms-excel";
    }

	/**
	 * @see ReportRenderer#getFilename(org.openmrs.module.reporting.report.ReportRequest)
	 */
    @Override
	public String getFilename(ReportRequest request) {
		String fileName = super.getFilename(request);
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
    	Workbook wb = getExcelTemplate(design);
        
        if (wb != null) {
        	ExcelTemplateRenderer templateRenderer = new ExcelTemplateRenderer();
			templateRenderer.render(reportData, argument, out);
        }
		else {
			ExcelBuilder excelBuilder = new ExcelBuilder();
            for (Map.Entry<String, DataSet> e : reportData.getDataSets().entrySet()) {
                DataSet dataset = e.getValue();
				excelBuilder.newSheet(e.getKey());

				if (getIncludeDataSetNameAndParameters(design)) {
					String displayName = ObjectUtil.nvlStr(dataset.getDefinition().getName(), e.getKey());
					excelBuilder.addCell(displayName, "bold");
					excelBuilder.nextRow();
					for (Parameter p : dataset.getDefinition().getParameters()) {
						Object parameterValue = dataset.getContext().getParameterValue(p.getName());
						if (ObjectUtil.notNull(parameterValue)) {
							excelBuilder.addCell(p.getLabelOrName() + ":", "align=right");
							excelBuilder.addCell(ObjectUtil.format(parameterValue));
							excelBuilder.nextRow();
						}
					}
					excelBuilder.nextRow();
				}

                List<DataSetColumn> columnList = dataset.getMetaData().getColumns();
                for (DataSetColumn column : columnList) {
					excelBuilder.addCell(column.getLabel(), "bold,border=bottom");
                }
                for (DataSetRow row : dataset ) {
					excelBuilder.nextRow();
                    for (DataSetColumn column : columnList) {
                    	Object cellValue = row.getColumnValue(column);
                        excelBuilder.addCell(cellValue);
                    }
                }
            }

			excelBuilder.write(out, getPassword(design));
        }
    }

    /**
     * @return a password configured for this spreadsheet, or an empty string if none configured
     */
    public String getPassword(ReportDesign design) {
        return design.getPropertyValue(PASSWORD_PROPERTY, "");
    }

	/**
	 * @return true if the Excel output should include the data set name and parameters in the top rows
	 */
	public boolean getIncludeDataSetNameAndParameters(ReportDesign design) {
		return "true".equalsIgnoreCase(design.getPropertyValue(INCLUDE_DATASET_NAME_AND_PARAMETERS_PROPERTY, ""));
	}

	/**
	 * @return an Excel Workbook for the given argument
	 */
	protected Workbook getExcelTemplate(ReportDesign design) throws IOException {
		Workbook wb = null;
		InputStream is = null;
		try {
			ReportDesignResource r = getTemplate(design);
			is = new ByteArrayInputStream(r.getContents());
			POIFSFileSystem fs = new POIFSFileSystem(is);
			wb = WorkbookFactory.create(fs);
		}
		catch (Exception e) {
			if (!design.getResources().isEmpty()) {
				throw new RenderingException("There was an error loading the Excel template", e);
			}
			log.debug("No template file found, will use default Excel output");
		}
		finally {
			IOUtils.closeQuietly(is);
		}
		return wb;
	}
}
