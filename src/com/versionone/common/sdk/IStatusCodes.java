package com.versionone.common.sdk;

/**
 * Helper class to convert between the many ways we need to use Status Codes
 *
 * @author Jerry D. Odenwelder Jr.
 */
public interface IStatusCodes {

    /**
     * @return a list of values to display
     */
    String[] getDisplayValues();

    /**
     * Gets an OID index for specified StatusCode.
     *
     * @param value name of the StatusCode
     * @return it's index; 0 if the value is invalid.
     */
    int getOidIndex(String value);

    /**
     * @param index of the StatusCode
     * @return the value
     */
    String getDisplayValue(int index);

    /**
     * Return the ID of a status code based on index.
     *
     * @param value
     * @return ID of a SatusCode element
     */
    String getID(int value);


    /**
     * Get the display value from an OID
     *
     * @param oid
     * @return
     */
	String getDisplayFromOid(String oid);
}
