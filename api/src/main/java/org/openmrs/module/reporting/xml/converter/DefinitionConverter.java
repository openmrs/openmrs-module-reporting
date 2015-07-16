package org.openmrs.module.reporting.xml.converter;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.HashMap;
import java.util.Map;

/**
 * This Converter is meant to handle top-level definitions that are not mapped.
 */
public class DefinitionConverter implements Converter {

    private boolean mapped = false;
    private Mapper mapper;

    public DefinitionConverter(Mapper mapper) {
        this(false, mapper);
    }

    public DefinitionConverter(boolean mapped, Mapper mapper) {
        this.mapped = mapped;
        this.mapper = mapper;
    }

    @Override
    public boolean canConvert(Class type) {
        return Definition.class.isAssignableFrom(type) || Mapped.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        throw new UnsupportedOperationException("Marshalling of definitions is not yet implemented");
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

        Definition d = getDefinitionInstance(context);
        Map<String, Object> mappings = new HashMap<String, Object>();

        for (int i=0; i<reader.getAttributeCount(); i++) {
            String attributeName = reader.getAttributeName(i);
            String attributeValueString = reader.getAttribute(i);
            if (EvaluationUtil.isExpression(attributeValueString)) {
                d.addParameter(new Parameter(attributeName, attributeName, getPropertyType(d, attributeName))); // TODO: Support collections
                mappings.put(attributeName, attributeValueString);
            }
            else {
                setPropertyValueFromString(d, attributeName, attributeValueString);
            }
        }

        while (reader.hasMoreChildren()) {
            reader.moveDown();

            String originalNodeName = reader.getNodeName();
            String fieldName = mapper.realMember(d.getClass(), originalNodeName);
            Class fieldType = getPropertyType(d, fieldName);
            Object fieldValue = context.convertAnother(d, fieldType);
            setPropertyValue(d, fieldName, fieldValue);

            reader.moveUp();
        }

        if (mapped) {
            return new Mapped(d, mappings);
        }
        return d;
    }

    /**
     * Sets a property with the given string value
     */
    protected void setPropertyValueFromString(Object o, String propertyName, String propertyValue) {
        SingleValueConverter c = mapper.getConverterFromAttribute(o.getClass(), propertyName, getPropertyType(o, propertyName));
        Object convertedValue = c.fromString(propertyValue);
        setPropertyValue(o, propertyName, convertedValue);
    }

    /**
     * @return a new Definition instance given the configured definitionType
     */
    protected Definition getDefinitionInstance(UnmarshallingContext context) {
        try {
            return (Definition)context.getRequiredType().newInstance();
        }
        catch (Exception e) {
            throw new ConversionException("Unable to instantiate a new " + context.getRequiredType());
        }
    }

    /**
     * @return the declared type of the given bean property
     */
    protected Class getPropertyType(Object o, String propertyName) {
        try {
            return PropertyUtils.getPropertyType(o, propertyName);
        }
        catch (Exception e) {
            throw new ConversionException("Unable to retrieve property named " + propertyName + " from " + o.getClass());
        }
    }

    /**
     * Set the property value of the given object
     */
    protected void setPropertyValue(Object o, String propertyName, Object propertyValue) {
        try {
            PropertyUtils.setProperty(o, propertyName, propertyValue);
        }
        catch (Exception e) {
            throw new ConversionException("Unable to set property named " + propertyName + " on " + o.getClass() + " to " + propertyValue);
        }
    }
}
