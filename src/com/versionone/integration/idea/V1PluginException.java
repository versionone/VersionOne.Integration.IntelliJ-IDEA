package com.versionone.integration.idea;

/**
 * Throws due any errors in plugin (connection to V1, getting projects)
 */
public class V1PluginException extends Exception{
    public V1PluginException(String message) {
        super(message);
    }

    public V1PluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
