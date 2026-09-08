/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.renderer;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.openhtmltopdf.extend.FSStream;
import com.openhtmltopdf.extend.FSStreamFactory;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.renderer.template.TemplateEngine;
import org.openmrs.module.reporting.report.renderer.template.TemplateEngineManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Renders a report to PDF using either an AcroForm PDF template (field fill)
 * or an HTML/CSS template (converted to PDF via openhtmltopdf).
 * Mode is determined by the extension of the resource named "template" in the ReportDesign:
 *   .pdf  -> AcroForm field-fill mode
 *   .html -> HTML-to-PDF mode
 */
@Handler
@Localized("reporting.PdfTemplateRenderer")
public class PdfTemplateRenderer extends ReportTemplateRenderer {

    public static final String TEMPLATE_TYPE_PROPERTY = "templateType";

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public String getRenderedContentType(ReportRequest request) {
        return "application/pdf";
    }

    @Override
    public String getFilename(ReportRequest request) {
        return getFilenameBase(request) + ".pdf";
    }

    @Override
    public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {
        try {
            ReportDesign design = getDesign(argument);
            ReportDesignResource template = getTemplate(design);
            if (template == null) {
                throw new RenderingException("No template resource found in report design");
            }
            if (isPdfFormTemplate(template)) {
                renderFromPdfForm(reportData, design, template, out);
            } else {
                renderFromHtml(reportData, design, template, out);
            }
        } catch (RenderingException re) {
            throw re;
        } catch (Throwable e) {
            throw new RenderingException("Unable to render PDF: " + e, e);
        }
    }

    boolean isPdfFormTemplate(ReportDesignResource template) {
        return "pdf".equalsIgnoreCase(template.getExtension());
    }

    private void renderFromPdfForm(ReportData reportData, ReportDesign design, ReportDesignResource template, OutputStream out) throws IOException {
        Map<String, Object> replacements = getBaseReplacementData(reportData, design);
        try (PDDocument doc = PDDocument.load(template.getContents())) {
            PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();
            if (acroForm != null) {
                // Use getFieldIterator() to visit all fields including nested ones
                Iterator<PDField> fieldIterator = acroForm.getFieldIterator();
                while (fieldIterator.hasNext()) {
                    PDField field = fieldIterator.next();
                    String fieldName = field.getFullyQualifiedName();
                    if (replacements.containsKey(fieldName)) {
                        Object value = replacements.get(fieldName);
                        String format = design.getPropertyValue(fieldName + ".format", null);
                        String displayValue = format != null ? ObjectUtil.format(value, format) : ObjectUtil.format(value);
                        try {
                            if (field instanceof PDCheckBox) {
                                PDCheckBox pdCheckBox = (PDCheckBox) field;
                                if (Boolean.parseBoolean(displayValue)) {
                                    pdCheckBox.check();
                                } else {
                                    pdCheckBox.unCheck();
                                }
                            }
                            else {
                                field.setValue(displayValue);
                            }
                        } catch (Exception e) {
                            log.warn("Unable to set value for PDF field '" + fieldName + "': " + e.getMessage());
                        }
                    }
                }
                acroForm.refreshAppearances();
                acroForm.flatten();
            }
            doc.save(out);
        }
    }

    private void renderFromHtml(ReportData reportData, ReportDesign design, ReportDesignResource template, OutputStream out) throws IOException {
        String html = new String(template.getContents(), StandardCharsets.UTF_8);
        Map<String, Object> replacements = getBaseReplacementData(reportData, design);
        String engineName = design.getPropertyValue(TEMPLATE_TYPE_PROPERTY, null);
        TemplateEngine engine = TemplateEngineManager.getTemplateEngineByName(engineName);
        if (engine != null) {
            Map<String, Object> bindings = new HashMap<String, Object>();
            bindings.put("reportData", reportData);
            bindings.put("reportDesign", design);
            bindings.put("data", replacements);
            bindings.put("util", new ObjectUtil());
            bindings.put("dateUtil", new DateUtil());
            bindings.put("msg", new MessageUtil());
            html = engine.evaluate(html, bindings);
        }
        String prefix = getExpressionPrefix(design);
        String suffix = getExpressionSuffix(design);
        html = EvaluationUtil.evaluateExpression(html, replacements, prefix, suffix).toString();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(html, null);
        builder.useProtocolsStreamImplementation(new ReportDesignFSStreamFactory(design), "resource");
        builder.useProtocolsStreamImplementation(new BarcodeStreamFactory(), "barcode");
        builder.toStream(out);
        builder.run();
    }

    private class ReportDesignFSStreamFactory implements FSStreamFactory {

        private final ReportDesign design;

        ReportDesignFSStreamFactory(ReportDesign design) {
            this.design = design;
        }

        @Override
        public FSStream getUrl(String url) {
            String resourceName = url.replaceFirst("resource://", "");
            ReportDesignResource resource = design.getResourceByName(resourceName);
            if (resource == null) {
                log.warn("PDF template referenced resource not found in report design: " + resourceName);
                return emptyStream();
            }
            final byte[] contents = resource.getContents();
            return new FSStream() {
                @Override public InputStream getStream() { return new ByteArrayInputStream(contents); }
                @Override public Reader getReader() { return new InputStreamReader(new ByteArrayInputStream(contents), StandardCharsets.UTF_8); }
            };
        }

        private FSStream emptyStream() {
            return new FSStream() {
                @Override public InputStream getStream() { return new ByteArrayInputStream(new byte[0]); }
                @Override public Reader getReader() { return new StringReader(""); }
            };
        }
    }

    private class BarcodeStreamFactory implements FSStreamFactory {

        @Override
        public FSStream getUrl(String url) {
            try {
                String content = url.replaceFirst("barcode://", "");
                Map<EncodeHintType, Object> hints = new HashMap<>();
                hints.put(EncodeHintType.MARGIN, 0);
                BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, 600, 100, hints);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
                final byte[] imageBytes = baos.toByteArray();
                return new FSStream() {
                    @Override public InputStream getStream() { return new ByteArrayInputStream(imageBytes); }
                    @Override public Reader getReader() { return new InputStreamReader(new ByteArrayInputStream(imageBytes), StandardCharsets.UTF_8); }
                };
            } catch (Exception e) {
                log.warn("Unable to generate barcode for: " + url + ": " + e.getMessage());
                return new FSStream() {
                    @Override public InputStream getStream() { return new ByteArrayInputStream(new byte[0]); }
                    @Override public Reader getReader() { return new StringReader(""); }
                };
            }
        }
    }
}
