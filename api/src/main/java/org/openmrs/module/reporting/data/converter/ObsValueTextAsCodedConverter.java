package org.openmrs.module.reporting.data.converter;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;

/**
 * Interprets the valueText of an Obs as the id of an OpenMRS object, and converts
 * to that object; currently only supports Locations
 *
 * @param <T>
 */
public class ObsValueTextAsCodedConverter<T extends OpenmrsObject> implements DataConverter {

    private Class<T> dataType;

    public ObsValueTextAsCodedConverter(){
    }

    public ObsValueTextAsCodedConverter(Class<T> dataType) {
        this.dataType = dataType;
    }

    @Override
    public Object convert(Object original) {

        if (dataType != Location.class) {
            throw new IllegalArgumentException("ObValueTextAsCodedConverter only currently supports Location.class");
        }

        Obs obs = (Obs) original;

        if (obs == null || StringUtils.isBlank(obs.getValueText())) {
            return null;
        }

        return Context.getLocationService().getLocation(Integer.valueOf(obs.getValueText()));
    }

    @Override
    public Class<?> getInputDataType() {
        return Obs.class;
    }

    @Override
    public Class<?> getDataType() {
        return dataType;
    }

    public void setDataType(Class<T> dataType) {
        this.dataType = dataType;
    }

}
