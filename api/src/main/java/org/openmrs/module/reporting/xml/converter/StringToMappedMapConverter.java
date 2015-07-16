package org.openmrs.module.reporting.xml.converter;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This Converter is meant to handle Maps of definitions, using the name of the definition as the key, and the definition itself as the value
 */
public class StringToMappedMapConverter extends AbstractCollectionConverter {

    private Class<? extends Map> mapType = LinkedHashMap.class;

    public StringToMappedMapConverter(Mapper mapper) {
        super(mapper);
    }

    public StringToMappedMapConverter(Mapper mapper, Class<? extends Map> mapType) {
        this(mapper);
        this.mapType = mapType;
    }

    @Override
    public boolean canConvert(Class type) {
        return type == HashMap.class || type == LinkedHashMap.class;
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        throw new UnsupportedOperationException("Marshalling of String to Mapped Map is not yet implemented");
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Map m;
        try {
            m = mapType.newInstance();
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to create new instance of map of type: " + mapType);
        }
        while (reader.hasMoreChildren()) {
            DefinitionConverter converter = new DefinitionConverter(true, mapper());
            reader.moveDown();
            Class type = HierarchicalStreams.readClassType(reader, mapper());
            Mapped mapped = (Mapped)context.convertAnother(m, type, converter);
            m.put(mapped.getParameterizable().getName(), mapped);
            reader.moveUp();
        }
        return m;
    }


}
