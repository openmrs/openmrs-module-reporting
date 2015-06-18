package org.openmrs.module.reporting.xml.converter;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class StringToObjectMapConverter extends AbstractCollectionConverter{

    private Class<? extends Map> mapType = LinkedHashMap.class;
    private String keyAttributeName = "key";

    public StringToObjectMapConverter(Mapper mapper) {
        super(mapper);
    }

    public StringToObjectMapConverter(Mapper mapper, Class<? extends Map> mapType) {
        this(mapper);
        this.mapType = mapType;
    }

    public StringToObjectMapConverter(Mapper mapper, Class<? extends Map> mapType, String keyAttributeName) {
        this(mapper);
        this.mapType = mapType;
        this.keyAttributeName = keyAttributeName;
    }

    @Override
    public boolean canConvert(Class type) {
        return type == HashMap.class || type == LinkedHashMap.class;
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Map<String, Object> map = (Map<String, Object>) source;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            writer.startNode(mapper().serializedClass(value.getClass()));
            writer.addAttribute(keyAttributeName, entry.getKey());
            context.convertAnother(value);
            writer.endNode();
        }
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
            reader.moveDown();
            String key = reader.getAttribute(keyAttributeName);
            reader.moveDown();
            Object value = readItem(reader, context, m);
            reader.moveUp();
            m.put(key, value);
            reader.moveUp();
        }
        return m;
    }


}
