package org.ajoberstar.reckon.gradle;

import org.gradle.api.provider.ListProperty;

public interface StageOptions {

    ListProperty<String> getStages();
}
