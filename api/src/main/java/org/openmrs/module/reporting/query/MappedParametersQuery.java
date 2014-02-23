package org.openmrs.module.reporting.query;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

import java.util.Map;

/**
 * Allows you to easily expose a query with different names for its parameters
 * (which typically must be the same as @ConfigurationProperty-annotated properties.
 */
public abstract class MappedParametersQuery<Q extends Query<T>, T extends OpenmrsObject> extends BaseQuery<T> {

    @ConfigurationProperty
    private Mapped<Q> wrapped;

	public MappedParametersQuery() {}

    public MappedParametersQuery(Q toWrap, Map<String, String> renamedParameters) {
		wrapped = ParameterizableUtil.copyAndMap(toWrap, this, renamedParameters);
    }

    public Mapped<Q> getWrapped() {
        return wrapped;
    }

    public void setWrapped(Mapped<Q> wrapped) {
        this.wrapped = wrapped;
    }

}
