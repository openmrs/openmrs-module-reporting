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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDComboBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.ReportRequest;

import java.io.IOException;
import java.io.OutputStream;
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
    public void render(ReportData reportData, String argument, OutputStream out)
            throws IOException, RenderingException {
        try {
            ReportDesign design = getDesign(argument);
            ReportDesignResource template = getTemplate(design);
            if (template == null) {
                throw new RenderingException("No template resource found in report design");
            }
            if (isPdfFormTemplate(template)) {
                renderFromPdfForm(reportData, design, template, out);
            } else {
                throw new RenderingException("HTML-to-PDF rendering not yet implemented");
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

    private void renderFromPdfForm(ReportData reportData, ReportDesign design,
                                    ReportDesignResource template, OutputStream out) throws IOException {
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
                        String displayValue = format != null
                                ? ObjectUtil.format(value, format)
                                : ObjectUtil.format(value);
                        try {
                            if (field instanceof PDTextField) {
                                ((PDTextField) field).setValue(displayValue);
                            } else if (field instanceof PDCheckBox) {
                                if (Boolean.parseBoolean(displayValue)) {
                                    ((PDCheckBox) field).check();
                                } else {
                                    ((PDCheckBox) field).unCheck();
                                }
                            } else if (field instanceof PDComboBox) {
                                ((PDComboBox) field).setValue(displayValue);
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
}
