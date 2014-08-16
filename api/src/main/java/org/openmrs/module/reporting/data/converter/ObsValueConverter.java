package org.openmrs.module.reporting.data.converter;

import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Converts an Obs to it's value
 */
public class ObsValueConverter implements DataConverter {

    public ObsValueConverter() { }

    @Override
    public Object convert(Object original) {
		Obs o = (Obs) original;
		if (o == null) {
			return null;
		}
		if (o.getValueBoolean() != null) {
			return o.getValueBoolean();
		}
		if (o.getValueCoded() != null) {
			return ObjectUtil.format(o.getValueCoded());
		}
		if (o.getValueComplex() != null) {
			return o.getValueComplex();
		}
		if (o.getValueDatetime() != null) {
			return o.getValueDatetime();
		}
		if (o.getValueDate() != null) {
			return o.getValueDate();
		}
		if (o.getValueDrug() != null) {
			return ObjectUtil.format(o.getValueDrug());
		}
		if (o.getValueNumeric() != null) {
			return o.getValueNumeric();
		}
		if (o.getValueText() != null) {
			return o.getValueText();
		}
		if (o.getValueTime() != null) {
			return o.getValueTime();
		}
		return o.getValueAsString(Context.getLocale());
    }

    @Override
    public Class<?> getInputDataType() {
        return Obs.class;
    }

    @Override
    public Class<?> getDataType() {
        return Object.class;
    }
}
