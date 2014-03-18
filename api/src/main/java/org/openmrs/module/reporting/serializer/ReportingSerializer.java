package org.openmrs.module.reporting.serializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.core.MapBackedDataHolder;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.module.serialization.xstream.mapper.CGLibMapper;
import org.openmrs.module.serialization.xstream.mapper.HibernateCollectionMapper;
import org.openmrs.module.serialization.xstream.mapper.JavassistMapper;
import org.openmrs.module.serialization.xstream.mapper.NullValueMapper;
import org.openmrs.serialization.SerializationException;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;


public class ReportingSerializer extends XStreamShortSerializer {

	private static ThreadLocal<DataHolder> cache = new ThreadLocal<DataHolder>();
	
	/**
	 * @throws SerializationException
	 * @should serialize a cohort definition
	 * @should serialize an indicator that contains a persisted cohort definition
	 * @should serialize an indicator that contains an unsaved cohort definition
	 */
	public ReportingSerializer() throws SerializationException {
	    super(new XStream(new DomDriver()) {
	    	
	    	/**
	    	 * This method copied from XStreamSerializer constructor.
	    	 */
			protected MapperWrapper wrapMapper(MapperWrapper next) {
				MapperWrapper mapper = new CGLibMapper(next);
				mapper = new JavassistMapper(mapper);
				mapper = new HibernateCollectionMapper(mapper);
				mapper = new NullValueMapper(mapper);
				return mapper;
			}
			
	    	/**
	    	 * Override a mid-level XStream method to reuse a DataHolder cache if one is available 
	    	 */
	        public Object unmarshal(HierarchicalStreamReader reader, Object root) {
	            return unmarshal(reader, root, cache.get());
	        }
	    });

	    Mapper mapper = xstream.getMapper();
	    ConverterLookup converterLookup = xstream.getConverterLookup();

	    xstream.registerConverter(new PersonQueryConverter(mapper, converterLookup));
	    xstream.registerConverter(new CohortDefinitionConverter(mapper, converterLookup));
	    xstream.registerConverter(new EncounterQueryConverter(mapper, converterLookup));
	    xstream.registerConverter(new ObsQueryConverter(mapper, converterLookup));
		xstream.registerConverter(new CalculationRegistrationShortConverter(mapper, converterLookup));

		xstream.registerConverter(new PersonDataDefinitionConverter(mapper, converterLookup));
	    xstream.registerConverter(new PatientDataDefinitionConverter(mapper, converterLookup));
	    xstream.registerConverter(new EncounterDataDefinitionConverter(mapper, converterLookup));
	    
	    xstream.registerConverter(new DataSetDefinitionConverter(mapper, converterLookup));
	    
	    xstream.registerConverter(new DimensionConverter(mapper, converterLookup));
	    xstream.registerConverter(new IndicatorConverter(mapper, converterLookup));

		xstream.registerConverter(new ReportDefinitionConverter(mapper, converterLookup));
	}
	
	@Override
	synchronized public <T> T deserialize(String serializedObject, Class<? extends T> clazz) throws SerializationException {
		boolean cacheOwner = cache.get() == null;
		if (cacheOwner) {
			cache.set(new MapBackedDataHolder());
		}
		try {
			return super.deserialize(serializedObject, clazz);
		} finally {
			if (cacheOwner)
				cache.remove();
		}
	}

    /**
     * The OpenMRS serializer interface supports serializing an Object to a String, but when we are serializing a
     * complete ReportData, it may be too memory-intensive to create this String. This method lets you serialize to XML
     * in a streaming fashion.
     * @param object
     * @param out
     */
    public void serializeToStream(Object object, OutputStream out) {
        try {
            xstream.toXML(object, new OutputStreamWriter(out, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unsupported encoding", e);
        }
    }

}
