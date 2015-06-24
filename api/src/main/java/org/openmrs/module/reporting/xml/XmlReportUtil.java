package org.openmrs.module.reporting.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.xml.converter.DefinitionConverter;
import org.openmrs.module.reporting.xml.converter.StringToObjectMapConverter;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class XmlReportUtil {

    public static XStream getXStream() {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("parameter", Parameter.class);
        xstream.alias("report", ReportDefinition.class);
        xstream.aliasField("dataSets", ReportDefinition.class, "dataSetDefinitions");
        xstream.alias("sqlDataSet", SqlDataSetDefinition.class);

        // TODO
        // Iterate across all evaluators, get all supported definitions, fpr all types
        // For each, register appropriate alias and iterate over configuration properties
        // For each configuration property, register a converter that uses reflection to pass in
        //  - the type of the class containing the configuration property
        //  - the fieldName of the configuration property
        //  - the converter itself should be a wrapper that first looks for an attribute whose value contains ${}
        //    and if it finds it, adds an appropriate parameter to the definition and mapping to the mapped
        //  - it should then delegate to the appropriate underlying converter if it doesn't contain this
        //  - returns a mapped if appropriate, otherwise just returns the result of the converter



        // Whenever possible we want to allow simple types to be specified as either attributes or nested elements for readability and simplicity
        xstream.alias("boolean", Boolean.class);
        xstream.useAttributeFor(Boolean.class);
        xstream.useAttributeFor(boolean.class);
        xstream.useAttributeFor(Integer.class);
        xstream.useAttributeFor(int.class);
        xstream.useAttributeFor(Double.class);
        xstream.useAttributeFor(double.class);
        xstream.useAttributeFor(String.class);
        xstream.useAttributeFor(Class.class);
        xstream.useAttributeFor(Date.class);
        xstream.useAttributeFor(Locale.class);

        // Our primary use case is to support dates in the format yyyy-MM-dd, which is not handled by default converter that is registered
        xstream.registerConverter(new DateConverter("yyyy-MM-dd hh:mm:ss", new String[]{"yyyy-MM-dd hh:mm", "yyyy-MM-dd"}, TimeZone.getDefault()));

        // Almost all of the Maps we are converting into have Strings as keys, so we'll register this as the default
        xstream.registerConverter(new StringToObjectMapConverter(xstream.getMapper()));

        xstream.registerConverter(new DefinitionConverter(xstream.getMapper()));

        return xstream;
    }
}
