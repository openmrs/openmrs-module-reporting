package org.openmrs.module.reporting.report.renderer;

/**
 * Marker interface that represents renderers that don't write to an output stream, but rather provide
 * some interactive view into the raw ReportData
 */
public interface InteractiveReportRenderer extends ReportRenderer {

}
