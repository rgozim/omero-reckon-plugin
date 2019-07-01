package org.ajoberstar.reckon.gradle.internal;

import org.ajoberstar.reckon.core.VcsInventory;
import org.ajoberstar.reckon.core.Version;
import org.ajoberstar.reckon.gradle.ReckonPlugin;
import org.ajoberstar.reckon.gradle.StageOptions;
import org.gradle.api.Project;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

import javax.inject.Inject;
import java.util.Optional;
import java.util.function.BiFunction;

public class DefaultStageOptions implements StageOptions {

    protected Project project;

    protected final Property<String> defaultStage;

    protected final ListProperty<String> stages;

    @Inject
    public DefaultStageOptions(Project project) {
        this.project = project;
        this.defaultStage = project.getObjects().property(String.class);
        this.stages = project.getObjects().listProperty(String.class);

        // Default to selecting the first stage alphabetically
        this.defaultStage.convention(this.stages.map(strings ->
                strings.stream().sorted().findFirst().orElseThrow(
                        () -> new IllegalArgumentException("No stages supplied.")))
        );
    }

    @Override
    public ListProperty<String> getStages() {
        return stages;
    }

    @Override
    public Property<String> getDefaultStage() {
        return defaultStage;
    }

    public BiFunction<VcsInventory, Version, Optional<String>> evaluateStage() {
        return (inventory, targetNormal) -> PropertyUtil.findProperty(
                project, ReckonPlugin.STAGE_PROP, defaultStage.get());
    }

}
