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

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfTemplateRendererTest extends BaseModuleContextSensitiveTest {

    // -----------------------------------------------------------------------
    // AcroForm (PDF form-fill) tests
    // -----------------------------------------------------------------------

    @Test
    public void shouldFillAcroFormTextFieldFromReportParameter() throws Exception {
        ReportDefinition reportDef = new ReportDefinition();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("reportName", "My Test Report");

        final ReportDesign design = new ReportDesign();
        design.setRendererType(PdfTemplateRenderer.class);

        ReportDesignResource template = new ReportDesignResource();
        template.setName("template");
        template.setExtension("pdf");
        template.setContents(createAcroFormPdf("parameter.reportName"));
        design.addResource(template);

        ReportData reportData = Context.getService(ReportDefinitionService.class)
                .evaluate(reportDef, context);

        PdfTemplateRenderer renderer = new PdfTemplateRenderer() {
            @Override public ReportDesign getDesign(String argument) { return design; }
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderer.render(reportData, "test", baos);

        try (PDDocument rendered = PDDocument.load(baos.toByteArray())) {
            String text = new PDFTextStripper().getText(rendered);
            Assert.assertTrue("Expected 'My Test Report' in flattened PDF", text.contains("My Test Report"));
        }
    }

    @Test
    public void shouldFormatDateFieldUsingDesignProperty() throws Exception {
        Date startDate = DateUtil.getDateTime(2023, 1, 15);

        ReportDefinition reportDef = new ReportDefinition();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", startDate);

        final ReportDesign design = new ReportDesign();
        design.setRendererType(PdfTemplateRenderer.class);
        design.addPropertyValue("parameter.startDate.format", "dd/MM/yyyy");

        ReportDesignResource template = new ReportDesignResource();
        template.setName("template");
        template.setExtension("pdf");
        template.setContents(createAcroFormPdf("parameter.startDate"));
        design.addResource(template);

        ReportData reportData = Context.getService(ReportDefinitionService.class)
                .evaluate(reportDef, context);

        PdfTemplateRenderer renderer = new PdfTemplateRenderer() {
            @Override public ReportDesign getDesign(String argument) { return design; }
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderer.render(reportData, "test", baos);

        try (PDDocument rendered = PDDocument.load(baos.toByteArray())) {
            String text = new PDFTextStripper().getText(rendered);
            Assert.assertTrue("Expected formatted date '15/01/2023' in PDF", text.contains("15/01/2023"));
        }
    }

    @Test
    public void shouldLeaveUnmatchedAcroFormFieldBlankWithoutThrowingException() throws Exception {
        ReportDefinition reportDef = new ReportDefinition();
        EvaluationContext context = new EvaluationContext();
        // No parameters — field "parameter.reportName" has no match

        final ReportDesign design = new ReportDesign();
        design.setRendererType(PdfTemplateRenderer.class);

        ReportDesignResource template = new ReportDesignResource();
        template.setName("template");
        template.setExtension("pdf");
        template.setContents(createAcroFormPdf("parameter.reportName"));
        design.addResource(template);

        ReportData reportData = Context.getService(ReportDefinitionService.class)
                .evaluate(reportDef, context);

        PdfTemplateRenderer renderer = new PdfTemplateRenderer() {
            @Override public ReportDesign getDesign(String argument) { return design; }
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderer.render(reportData, "test", baos);  // must not throw
        Assert.assertTrue("Expected non-empty PDF even with unmatched field", baos.size() > 0);
    }

    // -----------------------------------------------------------------------
    // HTML→PDF tests
    // -----------------------------------------------------------------------

    @Test
    public void shouldRenderHtmlTemplateWithVariableReplacement() throws Exception {
        ReportDefinition reportDef = new ReportDefinition();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("reportName", "HTML Test Report");

        final ReportDesign design = new ReportDesign();
        design.setRendererType(PdfTemplateRenderer.class);

        ReportDesignResource template = new ReportDesignResource();
        template.setName("template");
        template.setExtension("html");
        template.setContents(
            "<!DOCTYPE html><html><body><p>Name: #parameter.reportName#</p></body></html>"
                .getBytes("UTF-8"));
        design.addResource(template);

        ReportData reportData = Context.getService(ReportDefinitionService.class)
                .evaluate(reportDef, context);

        PdfTemplateRenderer renderer = new PdfTemplateRenderer() {
            @Override public ReportDesign getDesign(String argument) { return design; }
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderer.render(reportData, "test", baos);

        byte[] pdfBytes = baos.toByteArray();
        Assert.assertTrue("Output must start with %PDF header",
            pdfBytes.length > 4
            && pdfBytes[0] == '%' && pdfBytes[1] == 'P'
            && pdfBytes[2] == 'D' && pdfBytes[3] == 'F');

        try (PDDocument rendered = PDDocument.load(pdfBytes)) {
            String text = new PDFTextStripper().getText(rendered);
            Assert.assertTrue("Expected report name in PDF text", text.contains("HTML Test Report"));
        }
    }

    @Test
    public void shouldApplyPipeFormatSeparatorInHtmlTemplate() throws Exception {
        Date startDate = DateUtil.getDateTime(2023, 1, 15);

        ReportDefinition reportDef = new ReportDefinition();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", startDate);

        final ReportDesign design = new ReportDesign();
        design.setRendererType(PdfTemplateRenderer.class);

        ReportDesignResource template = new ReportDesignResource();
        template.setName("template");
        template.setExtension("html");
        template.setContents(
            "<!DOCTYPE html><html><body><p>Date: #parameter.startDate|dd/MM/yyyy#</p></body></html>"
                .getBytes("UTF-8"));
        design.addResource(template);

        ReportData reportData = Context.getService(ReportDefinitionService.class)
                .evaluate(reportDef, context);

        PdfTemplateRenderer renderer = new PdfTemplateRenderer() {
            @Override public ReportDesign getDesign(String argument) { return design; }
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderer.render(reportData, "test", baos);

        try (PDDocument rendered = PDDocument.load(baos.toByteArray())) {
            String text = new PDFTextStripper().getText(rendered);
            Assert.assertTrue("Expected formatted date '15/01/2023'", text.contains("15/01/2023"));
        }
    }

    @Test
    public void shouldResolveAdditionalCssResourceFromDesign() throws Exception {
        ReportDefinition reportDef = new ReportDefinition();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("reportName", "Styled Report");

        final ReportDesign design = new ReportDesign();
        design.setRendererType(PdfTemplateRenderer.class);

        ReportDesignResource template = new ReportDesignResource();
        template.setName("template");
        template.setExtension("html");
        template.setContents(
            ("<!DOCTYPE html><html>"
            + "<head><link rel='stylesheet' href='resource://styles.css'/></head>"
            + "<body><p>Name: #parameter.reportName#</p></body></html>")
                .getBytes("UTF-8"));
        design.addResource(template);

        ReportDesignResource css = new ReportDesignResource();
        css.setName("styles.css");
        css.setContents("p { font-size: 12pt; }".getBytes("UTF-8"));
        design.addResource(css);

        ReportData reportData = Context.getService(ReportDefinitionService.class)
                .evaluate(reportDef, context);

        PdfTemplateRenderer renderer = new PdfTemplateRenderer() {
            @Override public ReportDesign getDesign(String argument) { return design; }
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderer.render(reportData, "test", baos);

        try (PDDocument rendered = PDDocument.load(baos.toByteArray())) {
            String text = new PDFTextStripper().getText(rendered);
            Assert.assertTrue("Expected report name in PDF with CSS resource", text.contains("Styled Report"));
        }
    }

    @Test
    public void shouldRenderGracefullyWhenReferencedResourceIsMissing() throws Exception {
        ReportDefinition reportDef = new ReportDefinition();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("reportName", "No CSS Report");

        final ReportDesign design = new ReportDesign();
        design.setRendererType(PdfTemplateRenderer.class);

        ReportDesignResource template = new ReportDesignResource();
        template.setName("template");
        template.setExtension("html");
        template.setContents(
            ("<!DOCTYPE html><html>"
            + "<head><link rel='stylesheet' href='resource://missing.css'/></head>"
            + "<body><p>Name: #parameter.reportName#</p></body></html>")
                .getBytes("UTF-8"));
        design.addResource(template);

        ReportData reportData = Context.getService(ReportDefinitionService.class)
                .evaluate(reportDef, context);

        PdfTemplateRenderer renderer = new PdfTemplateRenderer() {
            @Override public ReportDesign getDesign(String argument) { return design; }
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderer.render(reportData, "test", baos);
        Assert.assertTrue("Expected non-empty PDF even when CSS resource is missing", baos.size() > 0);
    }

    @Test
    public void shouldRenderGroovyHtmlTemplate() throws Exception {
        ReportDefinition reportDef = new ReportDefinition();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("reportName", "Groovy Report");

        final ReportDesign design = new ReportDesign();
        design.setRendererType(PdfTemplateRenderer.class);
        design.addPropertyValue(PdfTemplateRenderer.TEMPLATE_TYPE_PROPERTY, "Groovy");

        ReportDesignResource template = new ReportDesignResource();
        template.setName("template");
        template.setExtension("html");
        template.setContents(
            ("<!DOCTYPE html><html><body>"
            + "<p>Name: ${data['parameter.reportName']}</p>"
            + "</body></html>")
                .getBytes("UTF-8"));
        design.addResource(template);

        ReportData reportData = Context.getService(ReportDefinitionService.class)
                .evaluate(reportDef, context);

        PdfTemplateRenderer renderer = new PdfTemplateRenderer() {
            @Override public ReportDesign getDesign(String argument) { return design; }
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderer.render(reportData, "test", baos);

        try (PDDocument rendered = PDDocument.load(baos.toByteArray())) {
            String text = new PDFTextStripper().getText(rendered);
            Assert.assertTrue("Expected 'Groovy Report' in PDF", text.contains("Groovy Report"));
        }
    }

    @Test
    public void shouldRenderCode128BarcodeInHtmlTemplate() throws Exception {
        ReportDefinition reportDef = new ReportDefinition();
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("dispenseId", "550e8400-e29b-41d4-a716-446655440000");

        final ReportDesign design = new ReportDesign();
        design.setRendererType(PdfTemplateRenderer.class);

        ReportDesignResource template = new ReportDesignResource();
        template.setName("template");
        template.setExtension("html");
        template.setContents(
            ("<!DOCTYPE html><html><body>"
            + "<p>ID: #parameter.dispenseId#</p>"
            + "<img src='barcode://#parameter.dispenseId#' style='width:3in;height:0.5in;'/>"
            + "</body></html>")
                .getBytes("UTF-8"));
        design.addResource(template);

        ReportData reportData = Context.getService(ReportDefinitionService.class)
                .evaluate(reportDef, context);

        PdfTemplateRenderer renderer = new PdfTemplateRenderer() {
            @Override public ReportDesign getDesign(String argument) { return design; }
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderer.render(reportData, "test", baos);

        Assert.assertTrue("Expected non-empty PDF with barcode", baos.size() > 0);
        byte[] pdfBytes = baos.toByteArray();
        Assert.assertTrue("Expected valid PDF header", new String(pdfBytes, 0, 4).equals("%PDF"));
    }

    // -----------------------------------------------------------------------
    // Helper: build a minimal in-memory AcroForm PDF
    // -----------------------------------------------------------------------

    /**
     * Creates a minimal single-page AcroForm PDF with one text field per supplied name.
     * Field names may contain dots (e.g. "parameter.reportName"), which are treated as
     * a hierarchy: the portion before the last dot becomes a non-terminal parent field,
     * and the portion after the last dot becomes the terminal text-field leaf.
     * This matches PDFBox 2.x behaviour where getFullyQualifiedName() concatenates
     * parent and child partial names with a ".".
     */
    private byte[] createAcroFormPdf(String... fieldNames) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDAcroForm acroForm = new PDAcroForm(doc);
            doc.getDocumentCatalog().setAcroForm(acroForm);
            acroForm.setNeedAppearances(true);

            PDFont font = PDType1Font.HELVETICA;
            PDResources dr = new PDResources();
            dr.put(COSName.getPDFName("Helv"), font);
            acroForm.setDefaultResources(dr);

            // Cache of non-terminal (parent) fields keyed by their partial name,
            // so we reuse a single "parameter" parent for multiple children.
            Map<String, PDNonTerminalField> parents = new HashMap<>();

            float y = 750f;
            for (String fieldName : fieldNames) {
                int lastDot = fieldName.lastIndexOf('.');
                if (lastDot < 0) {
                    // No dot: create a simple top-level text field
                    PDTextField field = new PDTextField(acroForm);
                    field.setPartialName(fieldName);
                    field.setDefaultAppearance("/Helv 12 Tf 0 g");

                    PDAnnotationWidget widget = field.getWidget();
                    widget.setRectangle(new PDRectangle(50f, y, 200f, 20f));
                    widget.setPage(page);
                    page.getAnnotations().add(widget);

                    acroForm.getFields().add(field);
                } else {
                    String parentName = fieldName.substring(0, lastDot);
                    String leafName   = fieldName.substring(lastDot + 1);

                    // Get or create parent non-terminal field
                    PDNonTerminalField parent = parents.get(parentName);
                    if (parent == null) {
                        parent = new PDNonTerminalField(acroForm);
                        parent.setPartialName(parentName);
                        acroForm.getFields().add(parent);
                        parents.put(parentName, parent);
                    }

                    // Create leaf text field as child of the parent
                    PDTextField leaf = new PDTextField(acroForm);
                    leaf.setPartialName(leafName);
                    leaf.setDefaultAppearance("/Helv 12 Tf 0 g");

                    PDAnnotationWidget widget = leaf.getWidget();
                    widget.setRectangle(new PDRectangle(50f, y, 200f, 20f));
                    widget.setPage(page);
                    page.getAnnotations().add(widget);

                    List<org.apache.pdfbox.pdmodel.interactive.form.PDField> children =
                            new ArrayList<>(parent.getChildren());
                    children.add(leaf);
                    parent.setChildren(children);
                }
                y -= 30f;
            }

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                doc.save(baos);
                return baos.toByteArray();
            }
        }
    }
}
