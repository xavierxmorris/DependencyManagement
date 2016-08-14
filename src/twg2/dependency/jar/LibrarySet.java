package twg2.dependency.jar;

import java.util.HashMap;
import java.util.Map;

import lombok.val;
import twg2.dependency.models.LibraryJson;

import com.github.zafarkhaja.semver.expr.Expression;

/**
 * @author TeamworkGuy2
 * @since 2016-08-10
 */
public class LibrarySet {
	private Map<String, LibraryJson> libraries;


	public LibrarySet(Iterable<LibraryJson> libs) {
		this.libraries = new HashMap<>();
		for(val lib : libs) {
			this.libraries.put(lib.getName(), lib);
		}
	}


	public LibraryJson getLatestVersion(String libName, Expression versionReqirement) {
		val pkg = this.libraries.get(libName);
		if(pkg == null) { return null; }

		if(versionReqirement.interpret(pkg.getVersionExpr())) {
			return pkg;
		}
		return null;
	}


}
