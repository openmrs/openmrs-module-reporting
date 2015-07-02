package org.openmrs.module.reporting.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.lang.StringUtils;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.evaluator.DefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.xml.converter.DefinitionConverter;
import org.openmrs.module.reporting.xml.converter.StringToObjectMapConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class XmlReportSerializer {

    private XStream xstream;

    public XmlReportSerializer() {

        xstream = new XStream(new DomDriver());
        xstream.alias("parameter", Parameter.class);
        xstream.alias("report", ReportDefinition.class);
        xstream.aliasField("dataSets", ReportDefinition.class, "dataSetDefinitions");

        // Iterate across all evaluators, get all supported definitions, fpr all types
        // For each, register appropriate alias and iterate over configuration properties
        // For each configuration property, register a converter that uses reflection to pass in
        for (Class<? extends Definition> type : getSupportedDefinitionTypes()) {
            xstream.alias(getAlias(type), type);
        }

        // TODO

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
    }

    public void alias(String alias, Class type) {
        xstream.alias(alias, type);
    }

    public <T> T fromXml(Class<T> type, String xml) {
        return (T)xstream.fromXML(xml);
    }

    /**
     * @return all Definition classes registered with the system
     */
    public List<Class<? extends Definition>> getSupportedDefinitionTypes() {
        List<Class<? extends Definition>> ret = new ArrayList<Class<? extends Definition>>();
        for (DefinitionEvaluator evaluator : Context.getRegisteredComponents(DefinitionEvaluator.class)) {
            Handler handlerAnnotation = evaluator.getClass().getAnnotation(Handler.class);
            if (handlerAnnotation != null) {
                Class<?>[] types = handlerAnnotation.supports();
                if (types != null) {
                    for (Class<?> type : types) {
                        ret.add((Class<? extends Definition>)type);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * @return an alias that is the uncapitalized simple name of the class.  If a definition, remove "Definition" from the end
     */
    public String getAlias(Class<?> type) {
        String alias = StringUtils.uncapitalize(type.getSimpleName());
        if (Definition.class.isAssignableFrom(type) && alias.endsWith("Definition")) {
            alias = alias.substring(0, alias.lastIndexOf("Definition"));
        }
        return alias;
    }
}
