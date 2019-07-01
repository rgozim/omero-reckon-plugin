package org.ajoberstar.reckon.gradle;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

public interface StageOptions {

    ListProperty<String> getStages();

    Property<String> getDefaultStage();

}
