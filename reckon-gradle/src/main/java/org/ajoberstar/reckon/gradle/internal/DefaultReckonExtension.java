package org.ajoberstar.reckon.gradle.internal;

import groovy.lang.Closure;
import org.ajoberstar.reckon.gradle.ReckonExtension;
import org.ajoberstar.reckon.gradle.ScopeOptions;
import org.ajoberstar.reckon.gradle.SnapshotOptions;
import org.ajoberstar.reckon.gradle.StageOptions;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.internal.Cast;
import org.gradle.util.ConfigureUtil;

import javax.annotation.Nullable;


public class DefaultReckonExtension implements ReckonExtension {

    private Project project;
    private DefaultScopeOptions scopeOptions;
    private DefaultStageOptions stageOptions;

    public DefaultReckonExtension(Project project) {
        this.project = project;

        this.scopeOptions =
                project.getObjects().newInstance(DefaultScopeOptions.class, project);

        this.stageOptions =
                project.getObjects().newInstance(DefaultStageOptions.class, project);
    }

    @Override
    public void setNormal(ReckonExtension ext) {
        project.getLogger().warn("reckon.normal = scopeFromProp() is deprecated and will be removed in 1.0.0." +
                " Call reckon.scopeFromProp() instead.");
        // no op
    }

    @Override
    public void setPreRelease(ReckonExtension ext) {
        project.getLogger().warn("reckon.preRelease = stageFromProp() or snapshotFromProp() is deprecated " +
                "and will be removed in 1.0.0. Call reckon.stageFromProp() or reckon.snapshotFromProp() instead.");
        // no op
    }

    @Override
    public void scopeOptions(Action<? super ScopeOptions> scopeOpts) {
        scopeOpts.execute(this.scopeOptions);
    }

    @Override
    public void scopeOptions(Closure scopeOpts) {
        stageOptions(ConfigureUtil.configureUsing(scopeOpts));
    }

    @Override
    public void stageOptions(Action<? super StageOptions> stageOpts) {
        useStageOptions(new DefaultStageOptions(project), stageOpts);
    }

    @Override
    public void stageOptions(Closure stageOpts) {
        stageOptions(ConfigureUtil.configureUsing(stageOpts));
    }

    @Override
    public void snapShotOptions(Action<? super SnapshotOptions> snapshotOpts) {
        useStageOptions(new DefaultSnapshotOptions(project), snapshotOpts);
    }

    @Override
    public void snapShotOptions(Closure stageOpts) {
        snapShotOptions(ConfigureUtil.configureUsing(stageOpts));
    }

    public DefaultScopeOptions getScopeOptions() {
        return scopeOptions;
    }

    public DefaultStageOptions getStageOptions() {
        return stageOptions;
    }

    private <T extends StageOptions> DefaultStageOptions useStageOptions(
            DefaultStageOptions stageOptions, @Nullable Action<? super T> stageOptionsConfigure) {
        if (stageOptions == null) {
            throw new IllegalArgumentException("StageOptions is null!");
        }

        this.stageOptions = stageOptions;

        if (stageOptionsConfigure != null) {
            stageOptionsConfigure.execute(Cast.<T>uncheckedCast(this.stageOptions));
        }

        return this.stageOptions;
    }

}
