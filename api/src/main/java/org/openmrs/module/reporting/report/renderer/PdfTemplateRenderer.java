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
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.ReportRequest;

import java.io.IOException;
import java.io.OutputStream;

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
        throw new RenderingException("PdfTemplateRenderer.render() not yet implemented");
    }

    boolean isPdfFormTemplate(ReportDesignResource template) {
        return "pdf".equalsIgnoreCase(template.getExtension());
    }
}
