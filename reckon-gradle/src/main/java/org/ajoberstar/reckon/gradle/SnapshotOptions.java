package org.ajoberstar.reckon.gradle;

import org.gradle.api.provider.Property;

public interface SnapshotOptions extends StageOptions {

    Property<Boolean> getSnapshot();

}
