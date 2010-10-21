package org.openmrs.module.reporting.common;

import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;


public class LogicUtil {

	/**
	 * Tries to parse a String into a {@link LogicCriteria}.
	 * This method is safe to run on versions of logic before 0.4.1--if the parser runs for more than 2
	 * seconds, this method will throw an exception.
	 * @see http://tickets.openmrs.org/browse/LOGIC-53
	 * @param logic
	 */
	public static LogicCriteria parse(final String logic) throws LogicException {
		// these need to be final so we can refer to them from the thread
		// therefore they must be arrays so they can be final but have the value they're holding change
		final LogicCriteria[] resultHolder = new LogicCriteria[1];
		final Exception[] exceptionHolder = new Exception[1];

		// create a thread that will parse the input and store its success or failure in a holder 
		Thread parser = new Thread(new Runnable() {
			public void run() {
				Context.openSession();
				try {
					resultHolder[0] = Context.getLogicService().parseString(logic);
				} catch (Exception ex) {
					exceptionHolder[0] = ex;
				} finally {
					Context.closeSession();
				}
            }
		});
		
		// try running the thread for up to 2 seconds
		long startTime = System.currentTimeMillis();
		parser.start();
		while (resultHolder[0] == null &&
				exceptionHolder[0] == null &&
				System.currentTimeMillis() < startTime + 2000) {
			try {
				parser.join(250);
			} catch (InterruptedException ex) { }
		}

		// If neither success or failure was signalled, the thread is running forever and we need
		// to kill it. Calling Thread.stop is bad Java practice, but I have no other way to
		// interrupt the antlr parser
		if (exceptionHolder[0] == null && resultHolder[0] == null) {
			parser.stop();
		}

		// if an error was singalled, we throw it
		if (exceptionHolder[0] != null) {
			if (exceptionHolder[0] instanceof LogicException)
				throw (LogicException) exceptionHolder[0];
			else
				throw new LogicException(exceptionHolder[0]);
		}
		
		// if success was signalled, return that, otherwise throw a generic logic exception
		if (resultHolder[0] != null)
			return resultHolder[0];
		else
			throw new LogicException("Took too long to parse");
    }

}
