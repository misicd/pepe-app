package org.dmship.config;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum PepeApplicationFeatures implements Feature {

    @Label("Delete Pet")
    DELETE_PET;

    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
