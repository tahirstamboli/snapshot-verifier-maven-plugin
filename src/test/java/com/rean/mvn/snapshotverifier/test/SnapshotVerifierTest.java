package com.rean.mvn.snapshotverifier.test;

import java.io.File;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;

import com.rean.mvn.snapshotverifier.SnapshotVerifier;

import junit.framework.Assert;

/**
 * @author tahir
 *
 */
public class SnapshotVerifierTest extends AbstractMojoTestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testMojoGoal() throws Exception {
		File pom = getTestFile("src/test/resources/snapshot-verifier-mojo/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());
		SnapshotVerifier myMojo = (SnapshotVerifier) lookupMojo("verify", pom);
		assertNotNull(myMojo);
	}

	public void testMojoGoalExecutionWithoutSnapshotArtifact() throws Exception {
		File pom = getTestFile("src/test/resources/snapshot-verifier-mojo/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
		ProjectBuildingRequest buildingRequest = executionRequest.getProjectBuildingRequest();
		ProjectBuilder projectBuilder = this.lookup(ProjectBuilder.class);
		MavenProject mavenProject = projectBuilder.build(pom, buildingRequest).getProject();

		SnapshotVerifier mojo = (SnapshotVerifier) lookupMojo("verify", pom);
		mojo.setProject(mavenProject);
		mojo.execute();
	}

	public void testMojoGoalExecutionWithSnapshotArtifact() throws Exception {
		File pom = getTestFile("src/test/resources/snapshot-verifier-mojo/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
		ProjectBuildingRequest buildingRequest = executionRequest.getProjectBuildingRequest();
		ProjectBuilder projectBuilder = this.lookup(ProjectBuilder.class);
		MavenProject mavenProject = projectBuilder.build(pom, buildingRequest).getProject();
		Dependency dummyDependency = new Dependency();
		dummyDependency.setGroupId("dummy-group-id");
		dummyDependency.setArtifactId("dummy-artifact-id");
		dummyDependency.setVersion("dummy-version-snapshot");
		mavenProject.getDependencies().add(dummyDependency);
		mavenProject.setDependencies(mavenProject.getDependencies());
		SnapshotVerifier mojo = (SnapshotVerifier) lookupMojo("verify", pom);
		mojo.setProject(mavenProject);
		try {
			mojo.execute();
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e instanceof MojoExecutionException);
		}
	}
}
