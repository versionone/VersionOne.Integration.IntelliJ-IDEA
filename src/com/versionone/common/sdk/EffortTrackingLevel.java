package com.versionone.common.sdk;

import java.util.HashSet;
import java.util.Set;

import com.versionone.apiclient.IV1Configuration.TrackingLevel;

public final class EffortTrackingLevel {

    /**
     * Set of workitem's tokens to be tracked on. Token for primary workitem is
     * just a it's type prefix, e.g. "Story". Token for secondary workitem is a
     * parent type prefix + dot + own type prefix, e.g. "Story.Task".
     */
    private final Set<String> tokens = new HashSet<String>(6);

    public EffortTrackingLevel() {
    }

    public boolean isTracking(Entity item) {
        String token = item.getType().name();
        if (item instanceof SecondaryWorkitem) {
            token = ((SecondaryWorkitem) item).parent.getType() + "." + token;
        }
        return tokens.contains(token);
    }

    public void clear() {
        tokens.clear();
    }

    public void addPrimaryTypeLevel(EntityType type, TrackingLevel trackingLevel) {
        switch (trackingLevel) {
        case On:
            tokens.add(type.name());
            break;
        case Off:
            addSecondaryTypeLevel(type);
            break;
        case Mix:
            tokens.add(type.name());
            addSecondaryTypeLevel(type);
            break;
        }
    }

    private void addSecondaryTypeLevel(EntityType parentType) {
        for (EntityType type : EntityType.values()) {
            if (type.isSecondary()) {
                tokens.add(parentType + "." + type);
            }
        }
    }
}
