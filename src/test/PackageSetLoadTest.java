package test;

import jarDependencies.PackageJson;
import jarDependencies.PackageSet;
import jarDependencies.RepositoryInfo;
import jarDependencies.RepositoryStructure;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import lombok.val;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author TeamworkGuy2
 * @since 2016-1-28
 */
public class PackageSetLoadTest {

	@Test
	public void load() throws IOException {
		val javaProjsPath = Paths.get("C:/Users/TeamworkGuy2/Documents/Java/Projects/");
		// all twg2 Java project package definition files are named 'package-lib.json'
		val javaRepoBldr = new RepositoryInfo.Builder("java-projects", new RepositoryStructure("package-lib.json"));
		javaRepoBldr.addRepository(javaProjsPath);

		val javaRepo = javaRepoBldr.build();

		val projSet = new PackageSet(Arrays.asList(javaRepo));

		Assert.assertTrue(projSet.getProjects("jtext-util").size() > 0);
	}


	@Test
	public void lookupVersion() throws IOException {
		val pkg1Name = PackageJsonTest.pkg1Name;
		val pkg1 = PackageJsonTest.loadPackage1();
		val projSet = new PackageSet(Arrays.asList(pkg1));

		Assert.assertEquals(1, getProjs(projSet, pkg1Name, "~3.5.0").size());
		Assert.assertEquals(1, getProjs(projSet, pkg1Name, "^3.5.0").size());
		Assert.assertEquals(1, getProjs(projSet, pkg1Name, "^3.0").size());
		Assert.assertEquals(0, getProjs(projSet, pkg1Name, "<= 3.1.1").size());
		Assert.assertEquals(0, getProjs(projSet, pkg1Name, "~3.1.0").size());
	}


	private static List<PackageJson> getProjs(PackageSet pkgSet, String name, String expr) {
		return pkgSet.getProjects(name, PackageSet.versionDependencyParser.parse(expr));
	}

}
