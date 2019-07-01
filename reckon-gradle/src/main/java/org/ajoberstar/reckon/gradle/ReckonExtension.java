package org.ajoberstar.reckon.gradle;

import groovy.lang.Closure;
import org.gradle.api.Action;

public interface ReckonExtension {

    @Deprecated
    void setNormal(ReckonExtension ext);

    @Deprecated
    void setPreRelease(ReckonExtension ext);

    void scopeOptions(Action<? super ScopeOptions> scopeOpts);

    void scopeOptions(Closure scopeOpts);

    void stageOptions(Action<? super StageOptions> stageOpts);

    void stageOptions(Closure stageOpts);

    void snapShotOptions(Action<? super SnapshotOptions> snapshotOpts);

    void snapShotOptions(Closure stageOpts);

}
