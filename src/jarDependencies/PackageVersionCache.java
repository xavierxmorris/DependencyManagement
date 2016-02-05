package jarDependencies;

import java.util.List;

import com.github.zafarkhaja.semver.expr.Expression;

/** A package cache which allows package lookups by name and filtering by version number
 * @author TeamworkGuy2
 * @since 2016-1-2
 */
public interface PackageVersionCache {

	/**
	 * @param name the name of the package to lookup
	 * @return package with matching name. Throw an error if no packages or more than one matching packages are found
	 */
	public PackageJson getProjectEnsureOne(String name);

	/**
	 * @param name the name of the package to lookup
	 * @param expr filter matching packages by this version expression
	 * @return package with matching name and version. Throw an error if no packages or more than one matching packages are found
	 */
	public PackageJson getProjectEnsureOne(String name, Expression expr);

	/**
	 * @param name the name of the package to lookup
	 * @return highest version number package with matching name or null if no matching package found
	 */
	public PackageJson getProjectLatest(String name);

	/**
	 * @param name the name of the package to lookup
	 * @param expr filter matching packages by this version expression
	 * @return highest version number package with matching name and version or null if no matching package found
	 */
	public PackageJson getProjectLatest(String name, Expression expr);

	/**
	 * @param name the name of the package to lookup
	 * @return all packages with matching name or an empty collection if no matching package found
	 */
	public List<PackageJson> getProjects(String name);

	/**
	 * @param name the name of the package to lookup
	 * @param expr filter matching packages by this version expression
	 * @return all packages with matching name and version or empty collection if no matching package found
	 */
	public List<PackageJson> getProjects(String name, Expression expr);

}
