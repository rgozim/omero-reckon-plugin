package org.ajoberstar.reckon.gradle.internal;

import org.ajoberstar.reckon.core.VcsInventory;
import org.ajoberstar.reckon.gradle.ReckonPlugin;
import org.ajoberstar.reckon.gradle.ScopeOptions;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;

import javax.inject.Inject;
import java.util.Optional;
import java.util.function.Function;

public class DefaultScopeOptions implements ScopeOptions {

    private final Project project;

    private final Property<String> scope;

    @Inject
    public DefaultScopeOptions(Project project) {
        this.project = project;
        this.scope = project.getObjects().property(String.class);
    }

    public Property<String> getScope() {
        return scope;
    }

    public Function<VcsInventory, Optional<String>> evaluateScope() {
        return inventory -> PropertyUtil.findProperty(project, ReckonPlugin.SCOPE_PROP, scope.get());
    }

}
