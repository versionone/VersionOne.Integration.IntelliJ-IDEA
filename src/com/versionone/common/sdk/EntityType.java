package com.versionone.common.sdk;

public enum EntityType {

    Story(true),
    Defect(true),
    Test(false),
    Task(false),
    Scope;

    public final Boolean isPrimary;

    private EntityType() {
        isPrimary = null;
    }

    private EntityType(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public boolean isWorkitem() {
        return isPrimary != null;
    }
    
    public boolean isPrimary() {
        return isWorkitem() && isPrimary;
    }

    public boolean isSecondary() {
        return isWorkitem() && !isPrimary;
    }
}
