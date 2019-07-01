package org.ajoberstar.reckon.gradle;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.ajoberstar.grgit.Grgit;
import org.ajoberstar.grgit.Repository;
import org.ajoberstar.reckon.core.Reckoner;
import org.ajoberstar.reckon.core.Version;
import org.ajoberstar.reckon.gradle.internal.DefaultReckonExtension;
import org.eclipse.jgit.api.Git;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ReckonPlugin implements Plugin<Project> {
  public static final String TAG_TASK = "reckonTagCreate";
  public static final String PUSH_TASK = "reckonTagPush";

  public static final String SCOPE_PROP = "reckon.scope";
  public static final String STAGE_PROP = "reckon.stage";
  public static final String SNAPSHOT_PROP = "reckon.snapshot";

  private Project project;

  @Override
  public void apply(Project project) {
    if (!project.equals(project.getRootProject())) {
      throw new IllegalStateException("org.ajoberstar.reckon can only be applied to the root project.");
    }
    this.project = project;

    project.getPluginManager().apply("org.ajoberstar.grgit");

    Grgit grgit = (Grgit) project.findProperty("grgit");
    DefaultReckonExtension reckon = (DefaultReckonExtension) project.getExtensions()
            .create(ReckonExtension.class,"reckon", DefaultReckonExtension.class, project, grgit);
    reckonVersion(grgit, reckon);

    Task tag = createTagTask(project, reckon, grgit);
    Task push = createPushTask(project, reckon, grgit, tag);
    push.dependsOn(tag);
  }

  private Task createTagTask(Project project, DefaultReckonExtension extension, Grgit grgit) {
    Task task = project.getTasks().create(TAG_TASK);
    task.setDescription("Tag version inferred by reckon.");
    task.setGroup("publishing");
    task.onlyIf(t -> {
      Version version = ((DelayedVersion) project.getVersion()).getVersion();

      // rebuilds shouldn't trigger a new tag
      boolean alreadyTagged = grgit.getTag().list().stream()
          .anyMatch(tag -> tag.getName().equals(version.toString()));

      return version.isSignificant() && !alreadyTagged;
    });
    task.doLast(t -> {
      Map<String, Object> args = new HashMap<>();
      args.put("name", project.getVersion());
      args.put("message", project.getVersion());
      grgit.getTag().add(args);
    });
    return task;
  }

  private Task createPushTask(Project project, DefaultReckonExtension extension, Grgit grgit, Task create) {
    Task task = project.getTasks().create(PUSH_TASK);
    task.setDescription("Push version tag created by reckon.");
    task.setGroup("publishing");
    task.onlyIf(t -> create.getDidWork());
    task.doLast(t -> {
      Map<String, Object> args = new HashMap<>();
      args.put("refsOrSpecs", Arrays.asList("refs/tags/" + project.getVersion().toString()));
      grgit.push(args);
    });
    return task;
  }

  private void reckonVersion(Grgit grgit, DefaultReckonExtension reckonExt) {
    project.afterEvaluate(project -> {
      org.eclipse.jgit.lib.Repository repo = Optional.ofNullable(grgit)
              .map(Grgit::getRepository)
              .map(Repository::getJgit)
              .map(Git::getRepository)
              .orElse(null);

      Reckoner.Builder reckoner = Reckoner.builder()
              .git(repo)
              .stages(reckonExt.getStageOptions().getStages().get().toArray(new String[0]))
              .scopeCalc(reckonExt.getScopeOptions().evaluateScope())
              .stageCalc(reckonExt.getStageOptions().evaluateStage());

      Version version = reckoner.build().reckon();
      project.getLogger().warn("Reckoned version: {}", version);

      DelayedVersion sharedVersion = new DelayedVersion(() -> version);
      project.allprojects(prj -> prj.setVersion(sharedVersion));
    });
  }

  private static class DelayedVersion {
    private final Supplier<Version> reckoner;

    public DelayedVersion(Supplier<Version> reckoner) {
      this.reckoner = Suppliers.memoize(reckoner);
    }

    public Version getVersion() {
      return reckoner.get();
    }

    @Override
    public String toString() {
      return reckoner.get().toString();
    }
  }
}
