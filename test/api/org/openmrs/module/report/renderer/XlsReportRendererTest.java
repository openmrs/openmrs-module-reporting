package org.openmrs.module.report.renderer;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import org.junit.Test;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.DataSetRow;
import org.openmrs.module.dataset.SimpleDataSet;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;
import org.openmrs.module.dataset.definition.DataSetWrappingDataSetDefinition;
import org.openmrs.module.dataset.definition.evaluator.DataSetWrappingDataSetEvaluator;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.report.ReportData;
import org.openmrs.test.Verifies;

public class XlsReportRendererTest {

    /**
     * @see {@link XlsReportRenderer#render(ReportData,String,OutputStream)}
     */
    @Test
    @Verifies(value = "should render ReportData to an xls file", method = "render(ReportData,String,OutputStream)")
    public void render_shouldRenderReportDataToAnXlsFile() throws Exception {
        DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
        
        SimpleDataSet ds1 = new SimpleDataSet(null, null);
        ds1.addRow(makeRowHelper("patient_id", 123, "given_name", "Darius", "family_name", "Jazayeri"));
        ds1.addRow(makeRowHelper("patient_id", 321, "given_name", "Ryan", "family_name", "Jazayeri"));
        
        SimpleDataSet ds2 = new SimpleDataSet(null, null);
        ds2.addRow(makeRowHelper("patient_id", 123, "encounter_id", 1, "encounter_date", ymd.parse("2009-03-12")));
        ds2.addRow(makeRowHelper("patient_id", 123, "encounter_id", 2, "encounter_date", ymd.parse("2009-04-11")));
        ds2.addRow(makeRowHelper("patient_id", 123, "encounter_id", 3, "encounter_date", ymd.parse("2009-05-10")));
        
        // need to actually make sure these dataset point back to their implied definitions.
        {
            EvaluationContext ec = new EvaluationContext();
            DataSetWrappingDataSetDefinition def1 = new DataSetWrappingDataSetDefinition(ds1);
            DataSetWrappingDataSetDefinition def2 = new DataSetWrappingDataSetDefinition(ds2);
            DataSetWrappingDataSetEvaluator eval = new DataSetWrappingDataSetEvaluator();
            eval.evaluate(def1, ec);
            eval.evaluate(def2, ec);
        }

        ReportData data = new ReportData();
        data.setDataSets(new LinkedHashMap<String, DataSet>());
        data.getDataSets().put("patients", ds1);
        data.getDataSets().put("encounters", ds2);
        
        XlsReportRenderer renderer = new XlsReportRenderer();
        File file = new File("delete_me.xls");
        System.out.println("Writing as file: " + file.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(file);
        renderer.render(data, null, fos);
        fos.flush();
        fos.close();
    }
    
    // needs to have an even number of arguments
    private DataSetRow<Object> makeRowHelper(Object... o) {
    	DataSetRow<Object> ret = new DataSetRow<Object>();
        for (int i = 0; i < o.length; i += 2) {
            ret.addColumnValue(new SimpleDataSetColumn((String) o[i]), o[i + 1]);
        }
        return ret;
    }
}