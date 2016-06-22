package twg2.dependency.test;

import java.io.ByteArrayInputStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;

import lombok.val;

import org.junit.Assert;
import org.junit.Test;

import twg2.collections.builder.MapBuilder;
import twg2.collections.tuple.Tuples;
import twg2.dependency.jar.PackageJson;
import twg2.io.json.Json;
import twg2.text.stringUtils.StringJoin;

/**
 * @author TeamworkGuy2
 * @since 2016-1-27
 */
public class PackageJsonTest {
	public static String pkg1Name = "pkg1";
	private static String pkg1Src = StringJoin.join(new String[] {
		"{",
		"	'version' : '3.5.3',",
		"	'name' : '" + pkg1Name + "',",
		"	'description' : 'a package manager for JavaScript',",
		"	'homepage' : 'https://docs.npmjs.com/',",
		"	'author' : {",
		"		'name' : 'Isaac Z. Schlueter',",
		"		'email' : 'i@izs.me',",
		"		'url' : 'http://blog.izs.me'",
		"	},",
		"	'license' : 'Artistic-2.0',",
		"	'keywords' : ['install', 'modules', 'package manager', 'package.json'],",
		"	'repository' : {",
		"		'type' : 'git',",
		"		'url' : 'git+https://github.com/npm/npm.git'",
		"	},",
		"	'dependencies' : {",
		"		'abbrev' : '~1.0.7',",
		"		'ansicolors' : '~0.3.2',",
		"		'ansistyles' : '~0.1.3'",
		"	},",
		"	'devDependencies' : {",
		"		'deep-equal' : '~1.0.1'",
		"	},",
		"	'gitHead' : 'a81f2d231f549aeaa6598b1924c658f814d4bfad',",
		"	'bin' : {",
		"		'npm' : './bin/npm-cli.js'",
		"	},",
		"	'main' : './lib/npm.js',",
		"	'preferGlobal' : true,",
		"	'contributors' : [{",
		"			'name' : 'Isaac Z. Schlueter',",
		"			'email' : 'i@izs.me'",
		"		}",
		"	],",
		"	'config' : {",
		"		'publishtest' : false",
		"	}",
		"}"
	}, "\n").replace('\'', '"');


	public static final PackageJson loadPackage1() {
		try {
			return PackageJson.read(Json.getDefaultInst(), new ByteArrayInputStream(pkg1Src.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new UncheckedIOException(e);
		}
	}


	@Test
	public void saveLoad() throws UnsupportedEncodingException {
		val pkg1 = loadPackage1();

		StringBuilder dst = new StringBuilder();
		pkg1.write(Json.getDefaultInst(), dst);

		Assert.assertEquals("3.5.3", pkg1.getVersion());
		Assert.assertEquals(pkg1Name, pkg1.getName());
		Assert.assertEquals("Artistic-2.0", pkg1.getLicense());
		Assert.assertEquals(MapBuilder.of(
				Tuples.of("abbrev", "~1.0.7"),
				Tuples.of("ansicolors", "~0.3.2"),
				Tuples.of("ansistyles", "~0.1.3")
			), pkg1.getDependencies());

		String expect = pkg1Src.replace("\t", "").replace("\n", "").replace(" ", "");
		String resStr = dst.toString().replace("\t", "").replace("\n", "").replace(" ", "");

		Assert.assertEquals(expect, resStr);
	}

}
