/*
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

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DelimitedTextReportRendererTest extends BaseModuleContextSensitiveTest {

    @Autowired
    ReportDefinitionService reportDefinitionService;

    @Test
    public void getRenderedContentType_shouldBeZipIfMoreThanOneDataSet() throws Exception {
        DelimitedTextReportRenderer renderer = new CsvReportRenderer();
        assertThat(renderer.getRenderedContentType(requestFor(reportDefinitionWithTwoDSDs())), is("application/zip"));
    }

    @Test
    public void getRenderedContentType_shouldBeCsvIfOneDataSet() throws Exception {
        DelimitedTextReportRenderer renderer = new CsvReportRenderer();
        assertThat(renderer.getRenderedContentType(requestFor(reportDefinitionWithOneDSD())), is("text/csv"));
    }

    @Test
    public void getFilename_shouldBeZipIfMoreThanOneDataSet() throws Exception {
        DelimitedTextReportRenderer renderer = new CsvReportRenderer();
        assertTrue(Pattern.matches("Testing_.*\\.zip", renderer.getFilename(requestFor(reportDefinitionWithTwoDSDs()))));
    }

    @Test
    public void getFilename_shouldBeCsvIfOneDataSet() throws Exception {
        DelimitedTextReportRenderer renderer = new CsvReportRenderer();
        assertTrue(Pattern.matches("Testing_.*\\.csv", renderer.getFilename(requestFor(reportDefinitionWithOneDSD()))));
    }

    @Test
    public void render_shouldWritePlainTextIfOneDataSet() throws Exception {
        DelimitedTextReportRenderer renderer = new CsvReportRenderer();
        ReportData data = reportDefinitionService.evaluate(reportDefinitionWithOneDSD(), new EvaluationContext());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        renderer.render(data, "", out);
		assertThat(out.toString().toLowerCase(), startsWith("\"patient_id\""));
    }

    @Test
    public void render_shouldWriteZipIfMoreThanOneDataSet() throws Exception {
        DelimitedTextReportRenderer renderer = new CsvReportRenderer();
        ReportData data = reportDefinitionService.evaluate(reportDefinitionWithTwoDSDs(), new EvaluationContext());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        renderer.render(data, "", out);

        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(out.toByteArray()));
        try {
            ZipEntry entry = zip.getNextEntry();
            assertThat(entry.getName(), is("collision with tricky tough characters.csv"));
            entry = zip.getNextEntry();
            assertThat(entry.getName(), is("collision with tricky tough characters_2.csv"));
        }
        finally {
            IOUtils.closeQuietly(zip);
        }

        // I also did the following, then manually opened the zip file, and opened the CSVs in LibreOffice. It worked
        // correctly. I'm commenting it out since it's pointless for automated testing.
        //   FileOutputStream fos = new FileOutputStream("/tmp/test.zip");
        //   renderer.render(data, "", fos);
        //   IOUtils.closeQuietly(fos);
    }

    @Test
    public void writeDataSet_shouldBeAbleToWriteUtf8() throws Exception {
        testWriteDataSetAs("UTF-8");
    }

    @Test
    public void writeDataSet_shouldBeAbleToWriteLatin1() throws Exception {
        testWriteDataSetAs("ISO-8859-1");
    }

    private void testWriteDataSetAs(String characterEncoding) throws IOException {
        SimpleDataSet ds = new SimpleDataSet(null, null);
        ds.addColumnValue(0, new DataSetColumn("value", "Value", String.class), "sí");

        DelimitedTextReportRenderer renderer = new CsvReportRenderer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        renderer.writeDataSet(ds, out, "", ",", "\n", characterEncoding, null);

        byte[] expected = "value\nsí\n".getBytes(Charset.forName(characterEncoding));
        byte[] actual = out.toByteArray();
        assertThat(actual.length, is(expected.length));
        for (int i = 0; i < actual.length; ++i) {
            assertThat(actual[i], is(expected[i]));
        }
    }

    @Test
    public void writeDataSet_shouldFilterBlacklistedCharacters() throws Exception {
        String actual = writeSingleColumnWithBlacklist("Yes: ÀÁÂÃÄàáâãä, No: ♪�", "[^\\p{InBasicLatin}\\p{InLatin-1Supplement}]");
        assertThat(actual, is("\"value\"\n\"Yes: ÀÁÂÃÄàáâãä, No: ???\"\n"));
    }

    @Test
    public void writeDataSet_shouldNotFilterWithNoBlacklist() throws Exception {
        String actual = writeSingleColumnWithBlacklist("Yes: ÀÁÂÃÄàáâãä, No: ♪�", null);
        assertThat(actual, is("\"value\"\n\"Yes: ÀÁÂÃÄàáâãä, No: ♪�\"\n"));
    }

    private String writeSingleColumnWithBlacklist(String value, String blacklistRegex) throws IOException {
        SimpleDataSet ds = new SimpleDataSet(null, null);
        ds.addColumnValue(0, new DataSetColumn("value", "Value", String.class), value);

        DelimitedTextReportRenderer renderer = new CsvReportRenderer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        renderer.writeDataSet(ds, out, "\"", ",", "\n", "UTF-8", blacklistRegex != null ? Pattern.compile(blacklistRegex) : null);

        return out.toString("UTF-8");
    }

    private ReportDefinition reportDefinitionWithOneDSD() {
        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.setName("Testing");
        reportDefinition.addDataSetDefinition("one", new SqlDataSetDefinition("one", "description", "select patient_id from patient"), null);
        return reportDefinition;
    }

    private ReportDefinition reportDefinitionWithTwoDSDs() {
        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.setName("Testing");
        reportDefinition.addDataSetDefinition("collision with \"tricky!\" tough characters", new SqlDataSetDefinition("one", "description", "select patient_id from patient"), null);
        reportDefinition.addDataSetDefinition("collision with tricky tough characters", new SqlDataSetDefinition("two", "description", "select location_id from location"), null);
        return reportDefinition;
    }

    private ReportRequest requestFor(ReportDefinition definition) {
        ReportRequest request = new ReportRequest();
        request.setReportDefinition(Mapped.noMappings(definition));
        request.setRenderingMode(new RenderingMode());
        return request;
    }

}
