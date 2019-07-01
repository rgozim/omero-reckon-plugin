package org.ajoberstar.reckon.gradle.internal;

import org.ajoberstar.reckon.core.VcsInventory;
import org.ajoberstar.reckon.core.Version;
import org.ajoberstar.reckon.gradle.ReckonPlugin;
import org.ajoberstar.reckon.gradle.SnapshotOptions;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;

import javax.inject.Inject;
import java.util.Optional;
import java.util.function.BiFunction;

public class DefaultSnapshotOptions extends DefaultStageOptions implements SnapshotOptions {

    private final Property<Boolean> snapshot;

    @Inject
    public DefaultSnapshotOptions(Project project) {
        super(project);
        this.stages.addAll("snapshot", "final");
        this.snapshot = project.getObjects().property(Boolean.class);
    }

    public Property<Boolean> getSnapshot() {
        return snapshot;
    }

    @Override
    public BiFunction<VcsInventory, Version, Optional<String>> evaluateStage() {
        return (inventory, targetNormal) -> {

            Optional<String> stageProp =
                    PropertyUtil.findProperty(project, ReckonPlugin.STAGE_PROP, defaultStage.get());

            Optional<String> snapshotProp =
                    PropertyUtil.findProperty(project, ReckonPlugin.SNAPSHOT_PROP, snapshot.get())
                            .map(Boolean::parseBoolean)
                            .map(isSnapshot -> isSnapshot ? "snapshot" : "final");

            snapshotProp.ifPresent(val -> {
                project.getLogger().warn("Property {} is deprecated and will be removed in 1.0.0. Use {} set to one of" +
                        " [snapshot, final].", ReckonPlugin.SNAPSHOT_PROP, ReckonPlugin.STAGE_PROP);
            });

            return stageProp.isPresent() ? stageProp : snapshotProp;
        };
    }

}
