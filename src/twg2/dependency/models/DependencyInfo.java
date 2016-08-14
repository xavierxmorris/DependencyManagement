package twg2.dependency.models;

import com.github.zafarkhaja.semver.Version;

/** A generic interface for any kind of Java project dependency
 * @author TeamworkGuy2
 * @since 2016-08-12
 */
public interface DependencyInfo {

	public String getName();

	public String getVersion();

	public Version getVersionExpr();

	/** @return the main entry file/directory path (relative or absolute)
	 */
	public String getMain();

	public String getPropString(String name);

	public String getPropStringOptional(String name, String defaultValue);

	public boolean isLibrary();

	public boolean isPackage();
}
