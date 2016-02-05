package test;

import jarDependencies.NameVersion;
import jarDependencies.PackageJson;
import jarDependencies.PackageSet;
import jarDependencies.RepositoryInfo;
import jarDependencies.RepositoryStructure;
import jarDependencies.VersionResolution;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import lombok.val;

import org.junit.Assert;
import org.junit.Test;

import com.github.zafarkhaja.semver.expr.CompositeExpression;
import com.github.zafarkhaja.semver.expr.Expression;
import com.github.zafarkhaja.semver.expr.ExpressionParser;

/**
 * @author TeamworkGuy2
 * @since 2016-2-1
 */
public class VersionResolutionTest {

	@Test
	public void test() {
		ExpressionParser parser = (ExpressionParser)ExpressionParser.newInstance();
		CompositeExpression expr;

		expr = (CompositeExpression)parser.parse("*");
		Assert.assertEquals(">=0.0.0", exprString(expr));

		expr = (CompositeExpression)parser.parse("~0.1.2");
		Assert.assertEquals(">=0.1.2 AND <0.2.0", exprString(expr));

		expr = (CompositeExpression)parser.parse("^0.1.2");
		Assert.assertEquals(">=0.1.2 AND <0.2.0", exprString(expr));

		expr = (CompositeExpression)parser.parse("~3.2.1");
		Assert.assertEquals(">=3.2.1 AND <3.3.0", exprString(expr));

		expr = (CompositeExpression)parser.parse("^3.2.1");
		Assert.assertEquals(">=3.2.1 AND <4.0.0", exprString(expr));

		expr = (CompositeExpression)parser.parse("4.2.0");
		Assert.assertEquals("==4.2.0", exprString(expr));
	}


	@Test
	public void getAllDependencies() throws IOException {
		val javaProjsPath = Paths.get("C:/Users/TeamworkGuy2/Documents/Java/Projects/");
		// all twg2 Java project package definition files are named 'package-lib.json'
		val javaRepoBldr = new RepositoryInfo.Builder("java-projects", new RepositoryStructure("package-lib.json"));
		javaRepoBldr.addRepository(javaProjsPath);

		val javaRepo = javaRepoBldr.build();

		val projSet = new PackageSet(Arrays.asList(javaRepo));

		val proj = javaRepo.getStructure().loadProjectInfo(Paths.get("C:/Users/TeamworkGuy2/Documents/Java/Projects/JParserDataTypeLike"));

		val pkgInfoToDependents = new HashMap<NameVersion, Entry<PackageJson, List<PackageJson>>>();
		projSet.loadDependencies(proj, pkgInfoToDependents);

		// TODO debugging
		pkgInfoToDependents.entrySet().stream().forEach((e) -> System.out.println(e.getKey() + ": " + e.getValue().getValue()));
	}


	static void print(Expression expr) {
		System.out.println(expr.toString() + ":\n" + VersionResolution.toString(expr) + "\n");
	}


	static String exprString(Expression expr) {
		return VersionResolution.toString(expr);
	}

}
