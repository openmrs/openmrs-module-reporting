package org.openmrs.module.reporting.xml.converter;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * This Converter is meant to handle collections of Mapped definitions
 */
public class CollectionOfMappedConverter extends CollectionConverter {

    private Mapper mapper;

    public CollectionOfMappedConverter(Mapper mapper) {
        super(mapper);
        this.mapper = mapper;
    }

    @Override
    protected Object readItem(HierarchicalStreamReader reader, UnmarshallingContext context, Object current) {
        DefinitionConverter converter = new DefinitionConverter(true, mapper);
        Class type = HierarchicalStreams.readClassType(reader, mapper);
        return context.convertAnother(current, type, converter);
    }
}
