package com.versionone.integration.idea;

/**
 * Throws due any errors in plugin (connection to V1, getting projects)
 */
public class V1PluginException extends Exception{
    private boolean isError = false;

    public V1PluginException(String message) {
        super(message);
    }

    public V1PluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public V1PluginException(String message, Throwable cause, boolean isError) {
        this(message, cause);
        this.isError = isError; 
    }

    public V1PluginException(String message, boolean isError) {
        this(message);
        this.isError = isError;
    }

    public boolean isError() {
        return isError;
    }
}
