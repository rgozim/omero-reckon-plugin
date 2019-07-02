package org.ajoberstar.reckon.gradle.internal;

import org.ajoberstar.reckon.core.VcsInventory;
import org.ajoberstar.reckon.gradle.ReckonPlugin;
import org.ajoberstar.reckon.gradle.ScopeOptions;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.util.ConfigureUtil;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DefaultScopeOptions extends BaseOptions implements ScopeOptions {

    final Property<String> scope;

    @Inject
    public DefaultScopeOptions(Project project, @Nullable Map<String, ?> args) {
        super(project);

        this.scope = project.getObjects().property(String.class);

        // Default to minor scope
        this.scope.convention("minor");

        if (args != null){
            ConfigureUtil.configureByMap(args, this);
        }
    }

    @Override
    public Property<String> getScope() {
        return scope;
    }

    public Function<VcsInventory, Optional<String>> evaluateScope() {
        return inventory -> findProperty(ReckonPlugin.SCOPE_PROP, scope.get());
    }

}
