package twg2.dependency.jar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.val;
import twg2.collections.dataStructures.SortedList;
import twg2.collections.tuple.Tuples;

import com.github.zafarkhaja.semver.Parser;
import com.github.zafarkhaja.semver.Version;
import com.github.zafarkhaja.semver.expr.Expression;
import com.github.zafarkhaja.semver.expr.ExpressionParser;

/** A collection of packages information
 * @author TeamworkGuy2
 * @since 2016-1-28
 */
public class PackageSet implements PackageVersionCache {
	public static Parser<Expression> versionDependencyParser = ExpressionParser.newInstance();

	private Map<String, List<PackageInfo>> projects;


	/** Create a package set by loading package information from a group of {@link RepositoryInfo repositories}
	 * @param repos
	 * @throws IOException
	 */
	public PackageSet(Iterable<RepositoryInfo> repos) throws IOException {
		this.projects = new HashMap<>();
		for(val repo : repos) {
			val structure = repo.getStructure();
			for(val proj : repo.getProjects()) {
				try {
					val pkgInfo = structure.loadProjectInfo(proj);
					if(pkgInfo != null) {
						val pkgName = pkgInfo.getName();
						val verStr = pkgInfo.getVersion();
						val ver = Version.valueOf(verStr);
						val pkgVerRepo = new PackageInfo(pkgName, ver, pkgInfo, repo);

						List<PackageInfo> pkgSet = projects.get(pkgName);
						if(pkgSet == null) {
							pkgSet = new ArrayList<>();
							projects.put(pkgName, pkgSet);
						}
						else {
							for(val existingPkg : pkgSet) {
								if(ver.equals(existingPkg.pkgVersion)) {
									throw new IllegalArgumentException("two projects have same name and version '" + existingPkg.pkgInfo.getNameVersion() + "'");
								}
							}
						}
						SortedList.addItem(pkgSet, pkgVerRepo, (a, b) -> a.pkgVersion.compareTo(b.pkgVersion));
						pkgSet.add(pkgVerRepo);
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
			val verStr = pkg.getVersion();
			val ver = Version.valueOf(verStr);
			val pkgVerRepoTuple = new PackageInfo(pkgName, ver, pkg, (RepositoryInfo)null);

			List<PackageInfo> pkgSet = projects.get(pkgName);
			if(pkgSet == null) {
				pkgSet = new ArrayList<>();
				projects.put(pkgName, pkgSet);
			}
			else {
				for(val existingPkg : pkgSet) {
					if(ver.equals(existingPkg.pkgVersion)) {
						throw new IllegalArgumentException("two projects have same name and version '" + existingPkg.pkgInfo.getNameVersion() + "'");
					}
				}
			}
			SortedList.addItem(pkgSet, pkgVerRepoTuple, (a, b) -> a.pkgVersion.compareTo(b.pkgVersion));
		}
	}


	public Map<String, List<PackageInfo>> getProjects() {
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
		return list.size() > 0 ? list.get(list.size() - 1).pkgInfo : null;
	}


	@Override
	public PackageJson getProjectLatest(String name, Expression expr) {
		val list = projects.get(name);
		for(int i = list.size() - 1; i > -1; i--) {
			val pkgInfo = list.get(i);
			if(expr.interpret(pkgInfo.pkgVersion)) {
				return pkgInfo.pkgInfo;
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
			dst.add(list.get(i).pkgInfo);
		}
		return dst;
	}


	public List<PackageJson> getProjects(String name, Expression expr, List<PackageJson> dst) {
		val list = projects.get(name);
		for(int i = 0, size = list.size(); i < size; i++) {
			val pkgInfo = list.get(i);
			if(expr.interpret(pkgInfo.pkgVersion)) {
				dst.add(pkgInfo.pkgInfo);
			}
		}
		return dst;
	}


	/** Load package dependencies recursively
	 * @param pkg
	 */
	public Map<NameVersion, Entry<PackageJson, List<PackageJson>>> loadDependencies(PackageJson pkg) {
		val dst = new HashMap<NameVersion, Entry<PackageJson, List<PackageJson>>>();
		_loadDependencies(this, pkg, dst);
		return dst;
	}


	public Map<NameVersion, Entry<PackageJson, List<PackageJson>>> loadDependencies(PackageJson pkg, Map<NameVersion, Entry<PackageJson, List<PackageJson>>> pkgInfoToDependents) {
		_loadDependencies(this, pkg, pkgInfoToDependents);
		return pkgInfoToDependents;
	}


	private static final void _loadDependencies(PackageSet projSet, PackageJson pkg, Map<NameVersion, Entry<PackageJson, List<PackageJson>>> pkgInfoToDependents) {
		// TODO debugging
		System.out.println("resolve: " + pkg + ", dependencies: " + pkg.getDependencies());

		for(val dep : pkg.getDependencies().entrySet()) {
			val depVer = PackageSet.versionDependencyParser.parse(dep.getValue());
			// get the latest matching package
			val childDep = projSet.getProjectLatest(dep.getKey(), depVer);
			val nameVer = new NameVersion(dep.getKey(), Version.valueOf(childDep.getVersion()));

			// If the 
			if(!pkgInfoToDependents.containsKey(nameVer)) {
				Entry<PackageJson, List<PackageJson>> dependentsEntry = pkgInfoToDependents.get(nameVer);
				if(dependentsEntry == null) {
					dependentsEntry = Tuples.of(childDep, new ArrayList<PackageJson>());
					pkgInfoToDependents.put(nameVer, dependentsEntry);
				}
				dependentsEntry.getValue().add(pkg);

				_loadDependencies(projSet, childDep, pkgInfoToDependents);
			}

			// TODO debugging
			else {
				System.out.println("skip resolving existing library '" + dep + "'");

				pkgInfoToDependents.get(nameVer).getValue().add(pkg);
			}
		}
	}

}
