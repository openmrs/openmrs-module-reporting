package org.openmrs.module.reporting.common;

import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * This comparator is meant to be used to sort delimited keys.
 * For example, given keys "1.A", "1.B", and "10.A", this should sort them by level, first numerically if possible,
 * and then alpha-numerically otherwise.  The keys above, compared strictly by their natural order as Strings,
 * would sort as "1.A", "10.A", "1.B".  This comparator ensures that "1.B" is sorted higher than "10.A".
 */
public class DelimitedKeyComparator implements Comparator<String> {

	// *******************
	// Constructor
	// *******************
	public DelimitedKeyComparator() {}
	
	// *******************
	// Properties
	// *******************
	private Pattern mRegex = java.util.regex.Pattern.compile("[\\.\\-\\_]");
	public void setRegex(Pattern pRegex) {mRegex = pRegex;}
	public Pattern getRegex() {return mRegex;}

	// *******************
	// Instance methods
	// *******************
	
	/**
	 * @see Comparator#compare(Object, Object)
	 * @should compare two strings
	 */
	public int compare(String s1, String s2) {
		
		if (s1 == s2) {return 0;}
		if (s1 == null) {return -1;}
		if (s2 == null) {return 1;}

		String[] s1Split = mRegex.split(s1);
		String[] s2Split = mRegex.split(s2);
		
		int maxInCommon = (s1Split.length > s2Split.length ? s2Split.length : s1Split.length);
		
		// Compare each common level
		for (int i=0; i<maxInCommon; i++) {
			String sub1 = s1Split[i];
			String sub2 = s2Split[i];
			int currentRes = 0;
			try {
				Integer i1 = Integer.valueOf(sub1);
				Integer i2 = Integer.valueOf(sub2);
				currentRes = i1.compareTo(i2);
			}
			catch (NumberFormatException nfe) {
				currentRes = sub1.compareTo(sub2);
			}
			if (currentRes != 0) { return currentRes; }
		}
		
		// If all common levels are the same, one with least number of sub-levels is greater
		if (s1Split.length > s2Split.length) {
			return 1;
		}
		if (s2Split.length > s2Split.length) {
			return -1;
		}
		return 0;
	}
}
