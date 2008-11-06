package com.versionone.common.sdk;

/**
 * Helper class to convert between the many ways we need to use Status Codes
 * @author Jerry D. Odenwelder Jr.
 *
 */
public interface ITaskStatus {
	
	/**
	 * Return a list of values to display
	 */
	String[] getDisplayValues();
	
	/**
	 * Given an OID return it's index
	 * Returns 0 if the value is invalid
	 */
	int getOidIndex(String value);
	
	/**
	 * Given an index, return the value
	 */
	String getDisplayValue(int index);

	/**
	 * Return the ID of a status code based on index.
	 * @param value
	 * @return ID of a SatusCode element
	 */
	String getID(int value);

	
	/**
	 * Get the display value from an OID
	 * @param oid
	 * @return
	 */
	String getDisplayFromOid(String oid);
	
}
