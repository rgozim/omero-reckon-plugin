package org.ajoberstar.reckon.gradle;

import groovy.lang.Closure;
import org.gradle.api.Action;

import java.util.Map;

public interface ReckonExtension {

    @Deprecated
    void setNormal(ReckonExtension ext);

    @Deprecated
    void setPreRelease(ReckonExtension ext);

    /**
     * @param scopeOpts
     */
    void scopeOptions(Map<String, ?> scopeOpts);

    /**
     * Configures scope specific options of reckon.
     * <p>
     * The supplied action configures an instance of {@link ScopeOptions},
     * which can be used to configure how scope is determined.
     * </p>
     *
     * @param scopeOpts An action used to configure the scope options.
     */
    void scopeOptions(Action<? super ScopeOptions> scopeOpts);

    /**
     * Configures scope specific options of reckon.
     * <p>
     * The supplied action configures an instance of {@link ScopeOptions},
     * which can be used to configure how scope is determined.
     * </p>
     *
     * @param scopeOpts A closure used to configure the scope options.
     */
    void scopeOptions(Closure scopeOpts);

    /**
     * @param stageOpts
     */
    void stageOptions(Map<String, ?> stageOpts);

    /**
     * @param stageOpts
     */
    void stageOptions(Action<? super StageOptions> stageOpts);

    /**
     * @param stageOpts
     */
    void stageOptions(Closure stageOpts);

    /**
     *
     */
    void snapShotOptions();

    /**
     * @param snapshotOpts
     */
    void snapShotOptions(Map<String, ?> snapshotOpts);

    /**
     * @param snapshotOpts
     */
    void snapShotOptions(Action<? super SnapshotOptions> snapshotOpts);

    /**
     * @param stageOpts
     */
    void snapShotOptions(Closure stageOpts);

}
