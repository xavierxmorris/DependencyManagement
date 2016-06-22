package twg2.dependency.jar;

import com.github.zafarkhaja.semver.Version;

/**
 * @author TeamworkGuy2
 * @since 2016-2-3
 */
public class PackageInfo {
	public final String pkgName;
	public final Version pkgVersion;
	public final PackageJson pkgInfo;
	public final RepositoryInfo parentRepository;


	public PackageInfo(String pkgName, Version pkgVersion, PackageJson pkgInfo, RepositoryInfo parentRepository) {
		this.pkgName = pkgName;
		this.pkgVersion = pkgVersion;
		this.pkgInfo = pkgInfo;
		this.parentRepository = parentRepository;
	}

}
