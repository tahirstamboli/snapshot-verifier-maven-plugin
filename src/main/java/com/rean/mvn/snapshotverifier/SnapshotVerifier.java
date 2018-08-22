package com.rean.mvn.snapshotverifier;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Mojo that can be used to validate snapshot version of dependent artifacts.
 * 
 * @author tahir
 *
 */
@Mojo(name = "verify", defaultPhase = LifecyclePhase.VALIDATE)
public class SnapshotVerifier extends AbstractMojo {

	private final String SNAPSHOT = "snapshot";

	@Parameter(property = "skip")
	protected boolean skip;

	@Component
	private MavenProject project;

	public void execute() throws MojoExecutionException {
		getLog().info("Started verification of dependent artifacts for snapshot version.");
		if (!skip) {
			List<String> allSnapshotArtifacts = getAllSnapshotArtifacts();
			if (allSnapshotArtifacts != null && !allSnapshotArtifacts.isEmpty()) {
				getLog().info("-----------------------------------------------------------------------");
				getLog().info("Following artifacts are having snapshot versions.");
				getLog().info(allSnapshotArtifacts.toString());
				getLog().info("-----------------------------------------------------------------------");
				throw new MojoExecutionException("Build failed due to use of snapshot version artifacts.");
			} else
				getLog().info("Snapshot dependency verification done. Snapshot version not found for any artifacts.");
		} else {
			getLog().info("Verification of dependent artifacts for snapshot version is skipped.");
		}

	}

	/**
	 * Get list of artifacts having SNAPSHOT version.
	 * 
	 * @return
	 */
	private List<String> getAllSnapshotArtifacts() {
		List<String> snapshotArtifacts = new ArrayList<String>();
		if (project != null) {
			List<Dependency> allDependencies = new ArrayList<>();
			List<Dependency> directDependencies = project.getDependencies();

			List<Dependency> dependenciesFromManagement = project.getDependencyManagement() != null
					? project.getDependencyManagement().getDependencies()
					: null;
			if (directDependencies != null)
				allDependencies.addAll(directDependencies);
			if (dependenciesFromManagement != null)
				allDependencies.addAll(dependenciesFromManagement);

			for (Dependency dependency : allDependencies) {
				if (dependency.getVersion() != null && dependency.getVersion().toLowerCase().contains(SNAPSHOT)) {
					snapshotArtifacts.add(
							dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion());
				}
			}

			List<Plugin> allPlugins = new ArrayList<>();
			List<Plugin> buildPlugins = project.getBuildPlugins();
			List<Plugin> pluginsFromManagement = project.getPluginManagement() != null
					? project.getPluginManagement().getPlugins()
					: null;
			if (buildPlugins != null)
				allPlugins.addAll(buildPlugins);
			if (pluginsFromManagement != null)
				allPlugins.addAll(pluginsFromManagement);

			for (Plugin plugin : allPlugins) {
				if (plugin.getVersion() != null && plugin.getVersion().toLowerCase().contains(SNAPSHOT)) {
					snapshotArtifacts
							.add(plugin.getGroupId() + ":" + plugin.getArtifactId() + ":" + plugin.getVersion());
				}
			}
		}
		return snapshotArtifacts;
	}

	public void setProject(MavenProject project) {
		this.project = project;
	}

}
