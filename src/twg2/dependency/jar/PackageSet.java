package twg2.dependency.jar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.val;
import twg2.collections.dataStructures.SortedList;
import twg2.dependency.models.LibraryJson;
import twg2.dependency.models.NameVersion;
import twg2.dependency.models.PackageJson;
import twg2.text.stringUtils.StringReplace;

import com.github.zafarkhaja.semver.Parser;
import com.github.zafarkhaja.semver.expr.Expression;
import com.github.zafarkhaja.semver.expr.ExpressionParser;

/** A collection of packages information
 * @author TeamworkGuy2
 * @since 2016-1-28
 */
public class PackageSet implements PackageVersionCache {
	public static Parser<Expression> versionDependencyParser = ExpressionParser.newInstance();

	private Map<String, List<PackageJson>> projects;


	/** Create a package set by loading package information from a group of {@link RepositorySet repositories}
	 * @param repos
	 * @throws IOException
	 */
	public PackageSet(RepositoryStructure<PackageJson> structure, Iterable<RepositorySet> repos, boolean validateProjectDirNames) throws IOException {
		this.projects = new HashMap<>();
		for(val repo : repos) {
			for(val proj : repo.getProjects()) {
				try {
					val pkgInfo = structure.loadProjectInfo(proj);
					if(pkgInfo != null) {
						val pkgName = pkgInfo.getName();
						val ver = pkgInfo.getVersionExpr();

						val projDirName = proj.getName(proj.getNameCount() - 1).toString();
						if(validateProjectDirNames && !equalProjectVsPackageName(projDirName, pkgName)) {
							throw new RuntimeException("package name '" + pkgName + "' did not match project directory name '" + projDirName + "'");
						}

						List<PackageJson> pkgSet = projects.get(pkgName);
						if(pkgSet == null) {
							pkgSet = new ArrayList<>();
							projects.put(pkgName, pkgSet);
						}
						else {
							for(val existingPkg : pkgSet) {
								if(ver.equals(existingPkg.getVersionExpr())) {
									throw new IllegalArgumentException("two projects have same name '" + existingPkg.getName() + "' and version '" + existingPkg.getNameVersion() + "'");
								}
							}
						}
						SortedList.addItem(pkgSet, pkgInfo, (a, b) -> a.getVersionExpr().compareTo(b.getVersionExpr()));
						pkgSet.add(pkgInfo);
					}
				} catch (IOException e) {
					throw new IOException("failed to read project info for '" + proj + "' of repository '" + repo.getName() + "'");
				}
			}
		}
	}


	/** Create a package set from a collection of package information objects
	 * @param pkgs
	 * @throws IOException
	 */
	public PackageSet(Collection<PackageJson> pkgs) throws IOException {
		this.projects = new HashMap<>();
		for(val pkg : pkgs) {
			val pkgName = pkg.getName();
			val ver = pkg.getVersionExpr();

			List<PackageJson> pkgSet = projects.get(pkgName);
			if(pkgSet == null) {
				pkgSet = new ArrayList<>();
				projects.put(pkgName, pkgSet);
			}
			else {
				for(val existingPkg : pkgSet) {
					if(ver.equals(existingPkg.getVersionExpr())) {
						throw new IllegalArgumentException("two projects have same name and version '" + existingPkg.getNameVersion() + "'");
					}
				}
			}
			SortedList.addItem(pkgSet, pkg, (a, b) -> a.getVersionExpr().compareTo(b.getVersionExpr()));
		}
	}


	public Map<String, List<PackageJson>> getProjects() {
		return projects;
	}


	@Override
	public PackageJson getProjectEnsureOne(String name) {
		val dst = new ArrayList<PackageJson>();
		getProjects(name, dst);
		if(dst.size() > 1 || dst.size() < 1) {
			throw new IllegalArgumentException("found " + dst.size() + " packages named '" + name + "', expected only 1");
		}
		return dst.get(0);
	}


	@Override
	public PackageJson getProjectEnsureOne(String name, Expression expr) {
		val dst = new ArrayList<PackageJson>();
		getProjects(name, expr, dst);
		if(dst.size() > 1 || dst.size() < 1) {
			throw new IllegalArgumentException("found " + dst.size() + " packages named '" + name + "', expected only 1");
		}
		return dst.get(0);
	}


	@Override
	public PackageJson getProjectLatest(String name) {
		val list = projects.get(name);
		return list.size() > 0 ? list.get(list.size() - 1) : null;
	}


	@Override
	public PackageJson getProjectLatest(String name, Expression versionReqirement) {
		val list = projects.get(name);
		if(list == null) { return null; }

		for(int i = list.size() - 1; i > -1; i--) {
			val pkgInfo = list.get(i);
			if(versionReqirement.interpret(pkgInfo.getVersionExpr())) {
				return pkgInfo;
			}
		}
		return null;
	}


	@Override
	public List<PackageJson> getProjects(String name) {
		val dst = new ArrayList<PackageJson>();
		getProjects(name, dst);
		return dst;
	}


	@Override
	public List<PackageJson> getProjects(String name, Expression expr) {
		val dst = new ArrayList<PackageJson>();
		getProjects(name, expr, dst);
		return dst;
	}


	public List<PackageJson> getProjects(String name, List<PackageJson> dst) {
		val list = projects.get(name);
		for(int i = 0, size = list.size(); i < size; i++) {
			dst.add(list.get(i));
		}
		return dst;
	}


	public List<PackageJson> getProjects(String name, Expression expr, List<PackageJson> dst) {
		val list = projects.get(name);
		for(int i = 0, size = list.size(); i < size; i++) {
			val pkgInfo = list.get(i);
			if(expr.interpret(pkgInfo.getVersionExpr())) {
				dst.add(pkgInfo);
			}
		}
		return dst;
	}


	public static final boolean equalProjectVsPackageName(String projName, String pkgName) {
		val removeChars = Arrays.asList("-", "_");
		val proj = StringReplace.replaceStrings(projName, 0, removeChars, "").toUpperCase();
		val pkg = StringReplace.replaceStrings(pkgName, 0, removeChars, "").toUpperCase();
		return proj.equals(pkg);
	}


	/** Load package dependencies recursively
	 * @param pkg
	 */
	public Map<NameVersion, DependencyAndDependents> loadDependencies(PackageJson pkg, LibrarySet libSet) {
		val dst = new HashMap<NameVersion, DependencyAndDependents>();
		_loadDependencies(this, pkg, libSet, dst);
		return dst;
	}


	public Map<NameVersion, DependencyAndDependents> loadDependencies(PackageJson pkg, LibrarySet libSet, Map<NameVersion, DependencyAndDependents> pkgInfoToDependentsDst) {
		_loadDependencies(this, pkg, libSet, pkgInfoToDependentsDst);
		return pkgInfoToDependentsDst;
	}


	private static final void _loadDependencies(PackageSet projSet, PackageJson pkg, LibrarySet libSet, Map<NameVersion, DependencyAndDependents> pkgInfoToDependents) {
		// TODO debugging
		System.out.println("resolve: " + pkg + ", dependencies: " + pkg.getDependencies());

		for(val dep : pkg.getDependencies().entrySet()) {
			val depVer = PackageSet.versionDependencyParser.parse(dep.getValue());
			// get the latest matching package
			val childDep = projSet.getProjectLatest(dep.getKey(), depVer);
			LibraryJson libDep = null;
			NameVersion nameVer = null;
			if(childDep == null) {
				if(libSet == null) {
					throw new RuntimeException("could not find dependency '" + dep.getKey() + "@" + dep.getValue() + "'");
				}
				else {
					libDep = libSet.getLatestVersion(dep.getKey(), depVer);
					if(libDep == null) {
						throw new RuntimeException("could not find dependency library '" + dep.getKey() + "@" + dep.getValue() + "' required by package " + pkg.getName());
					}
					else {
						nameVer = new NameVersion(dep.getKey(), libDep.getVersionExpr());
					}
				}
			}
			else {
				nameVer = new NameVersion(dep.getKey(), childDep.getVersionExpr());
			}

			// If the list of dependencies does not yet contain this dependency name-version combination
			if(!pkgInfoToDependents.containsKey(nameVer)) {
				DependencyAndDependents dependentsEntry = pkgInfoToDependents.get(nameVer);
				if(dependentsEntry == null) {
					dependentsEntry = new DependencyAndDependents(childDep, libDep);
					pkgInfoToDependents.put(nameVer, dependentsEntry);
				}
				dependentsEntry.addDependent(pkg);

				if(childDep != null) {
					_loadDependencies(projSet, childDep, libSet, pkgInfoToDependents);
				}
			}

			// TODO debugging
			else {
				System.out.println("skip resolving existing library '" + dep + "'");

				pkgInfoToDependents.get(nameVer).addDependent(pkg);
			}
		}
	}

}
