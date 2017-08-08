package org.openmrs.module.reporting.data.converter;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Converts an Obs to its value
 */
public class ObsValueConverter implements DataConverter {

	public ObsValueConverter() { }

	@Override
	public Object convert(Object original) {

		if (original == null) {
			return null;
		}
		if (original instanceof Obs) {
			Obs o = (Obs) original;
			return getObsValue(o);
		} else if (original instanceof List) {
			List<Object> obsValues = new ArrayList<Object>();
			for (Obs o : ((List<Obs>) original) ) {
				obsValues.add(getObsValue(o));
			}
			return obsValues;
		} 
		return original;
	}

	private Object getObsValue(Obs o) {
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
		return Object.class;
	}

	@Override
	public Class<?> getDataType() {
		return Object.class;
	}
}
