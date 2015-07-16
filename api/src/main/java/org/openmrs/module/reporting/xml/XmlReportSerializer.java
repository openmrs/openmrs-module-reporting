package org.openmrs.module.reporting.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.lang.StringUtils;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.evaluator.DefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.xml.converter.CollectionOfMappedConverter;
import org.openmrs.module.reporting.xml.converter.DefinitionConverter;
import org.openmrs.module.reporting.xml.converter.StringToMappedMapConverter;
import org.openmrs.module.reporting.xml.converter.StringToObjectMapConverter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class XmlReportSerializer {

    private XStream xstream;

    public XmlReportSerializer() {

        xstream = new XStream(new DomDriver());
        xstream.alias("parameter", Parameter.class);
        xstream.alias("report", ReportDefinition.class);
        xstream.aliasField("dataSets", ReportDefinition.class, "dataSetDefinitions");

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

        // Iterate across all evaluators, get all supported definitions, fpr all types
        // For each, register appropriate alias and iterate over configuration properties
        // For each configuration property, register a converter that uses reflection to pass in
        for (Class<? extends Definition> type : getSupportedDefinitionTypes()) {
            xstream.alias(getAlias(type), type);
            for (Field f : getConfigurationProperties(type)) {
                if (Mapped.class.isAssignableFrom(f.getType())) {
                    xstream.registerLocalConverter(type, f.getName(), new DefinitionConverter(true, xstream.getMapper()));
                }
                else if (Collection.class.isAssignableFrom(f.getType())) {
                    Type[] genericTypes = ReflectionUtil.getGenericTypes(f);
                    if (genericTypes.length > 0) {
                        if (ParameterizedType.class.isAssignableFrom(genericTypes[0].getClass())) {
                            Class genericType = (Class)((ParameterizedType)genericTypes[0]).getRawType();
                            if (Mapped.class.isAssignableFrom(genericType)) {
                                xstream.registerLocalConverter(type, f.getName(), new CollectionOfMappedConverter(xstream.getMapper()));
                            }
                        }
                    }
                }
                else if (Map.class.isAssignableFrom(f.getType())) {
                    Type[] genericTypes = ReflectionUtil.getGenericTypes(f);
                    if (genericTypes.length > 0) {
                        if (genericTypes[0] == String.class) {
                            if (ParameterizedType.class.isAssignableFrom(genericTypes[1].getClass())) {
                                Class genericType = (Class)((ParameterizedType)genericTypes[1]).getRawType();
                                if (Mapped.class.isAssignableFrom(genericType)) {
                                    xstream.registerLocalConverter(type, f.getName(), new StringToMappedMapConverter(xstream.getMapper()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void alias(String alias, Class type) {
        xstream.alias(alias, type);
    }

    public <T> T fromXml(Class<T> type, String xml) {
        return (T)xstream.fromXML(xml);
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

    public List<Field> getConfigurationProperties(Class<? extends Definition> type) {
        List<Field> ret = new ArrayList<Field>();
        Class superclass = type.getSuperclass();
        if (superclass != null) {
            ret.addAll(getConfigurationProperties(superclass));
        }
        for (Field f : type.getDeclaredFields()) {
            ConfigurationProperty ann = f.getAnnotation(ConfigurationProperty.class);
            if (ann != null) {
                ret.add(f);
            }
        }
        return ret;
    }
}
