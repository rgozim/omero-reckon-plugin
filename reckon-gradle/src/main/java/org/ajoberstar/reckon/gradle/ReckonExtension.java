package org.ajoberstar.reckon.gradle;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.ajoberstar.grgit.Grgit;
import org.ajoberstar.grgit.Repository;
import org.ajoberstar.reckon.core.Reckoner;
import org.ajoberstar.reckon.core.Version;
import org.eclipse.jgit.api.Git;
import org.gradle.api.Project;

public class ReckonExtension {
  private static final String SCOPE_PROP = "reckon.scope";
  private static final String STAGE_PROP = "reckon.stage";
  private static final String SNAPSHOT_PROP = "reckon.snapshot";

  private Project project;
  private Reckoner.Builder reckoner;

  public ReckonExtension(Project project, Grgit grgit) {
    this.project = project;
    this.reckoner = Reckoner.builder();
    org.eclipse.jgit.lib.Repository repo = Optional.ofNullable(grgit)
        .map(Grgit::getRepository)
        .map(Repository::getJgit)
        .map(Git::getRepository)
        .orElse(null);

    this.reckoner.git(repo);
  }

  @Deprecated
  public void setNormal(ReckonExtension ext) {
    project.getLogger().warn("reckon.normal = scopeFromProp() is deprecated and will be removed in 1.0.0. Call reckon.scopeFromProp() instead.");
    // no op
  }

  @Deprecated
  public void setPreRelease(ReckonExtension ext) {
    project.getLogger().warn("reckon.preRelease = stageFromProp() or snapshotFromProp() is deprecated and will be removed in 1.0.0. Call reckon.stageFromProp() or reckon.snapshotFromProp() instead.");
    // no op
  }

  public ReckonExtension scopeFromProp() {
    return scopeFromProp("minor");
  }

  public ReckonExtension scopeFromProp(String scope) {
    reckoner.scopeCalc(inventory -> findProperty(SCOPE_PROP, scope));
    return this;
  }

  public ReckonExtension stageFromProp(String... stages) {
    this.reckoner.stages(stages);
    this.reckoner.stageCalc((inventory, targetNormal) -> findProperty(STAGE_PROP, null));
    return this;
  }

  public ReckonExtension snapshotFromProp() {
      return snapshotFromProp(Collections.emptyMap());
  }

  public ReckonExtension snapshotFromProp(Map<String, Object> options) {
    String stage = (String) options.get("stage");
    Boolean snapshot = (Boolean) options.get("snapshot");
    return snapshotFromProp(stage, snapshot);
  }

  public ReckonExtension snapshotFromProp(String stage, Boolean snapshot) {
    this.reckoner.snapshots();
    this.reckoner.stageCalc((inventory, targetNormal) -> {

      Optional<String> stageProp = findProperty(STAGE_PROP, stage);
      Optional<String> snapshotProp = findProperty(SNAPSHOT_PROP, snapshot)
              .map(Boolean::parseBoolean)
              .map(isSnapshot -> isSnapshot ? "snapshot" : "final");

      snapshotProp.ifPresent(val -> {
        project.getLogger().warn("Property {} is deprecated and will be removed in 1.0.0. Use {} set to one of [snapshot, final].", SNAPSHOT_PROP, STAGE_PROP);
      });

      return stageProp.isPresent() ? stageProp : snapshotProp;
    });
    return this;
  }

  private Optional<String> findProperty(String name, Object fallback) {
     Object result = Optional.ofNullable(project.findProperty(name)).orElse(fallback);
     return Optional.ofNullable(result).map(Object::toString);
  }

  Version reckonVersion() {
    Version version = reckoner.build().reckon();
    project.getLogger().warn("Reckoned version: {}", version);
    return version;
  }
}
