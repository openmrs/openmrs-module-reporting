package org.openmrs.module.reporting.evaluation;

/**
 * Indicates that a {@link Definition} cannot be evaluated because it refers to another definition
 * definition that is missing (perhaps deleted by a user) 
 */
public class MissingDependencyException extends EvaluationException {

	public static final long serialVersionUID = 1L;
	
	public MissingDependencyException() {
	    super(null);
    }
	
	public MissingDependencyException(String propertyThatFailed) {
	    super(propertyThatFailed);
    }
	
    public MissingDependencyException(String propertyThatFailed, Throwable cause) {
	    super(propertyThatFailed, cause);
    }

	@Override
    public String getMessage() {
		StringBuilder ret = new StringBuilder();
		ret.append("The property ");
		if (getPropertyThatFailed() != null)
			ret.append("'" + getPropertyThatFailed() + "' ");
		ret.append("cannot be found in the database. (Maybe someone deleted it?)");
		return ret.toString();
    }

}
