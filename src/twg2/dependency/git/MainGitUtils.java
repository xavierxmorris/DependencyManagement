package twg2.dependency.git;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

import lombok.val;
import twg2.collections.builder.ListUtil;
import twg2.dependency.eclipseProject.ProjectsUtil;
import twg2.dependency.jar.RepositoryStructure;
import twg2.text.stringUtils.StringJoin;

/**
 * @author TeamworkGuy2
 * @since 2016-08-07
 */
public class MainGitUtils {


	@SafeVarargs
	private static <T> List<T> list(T... args) {
		return Arrays.asList(args);
	}


	private static <K, V> Entry<K, V> entry(K key, V value) {
		return new AbstractMap.SimpleImmutableEntry<K, V>(key, value);
	}


	private static <E extends Comparable<? super E>, S extends List<E>> S sort(S list) {
		Collections.sort(list);
		return list;
	}


	public static boolean findUsefulGitFiles(GitStatus.SourceControlFileAndStatus file) {
		return !file.path.endsWith("CHANGELOG.md") && !file.path.endsWith(".gitignore") && !file.path.endsWith(".jar");
	}


	public static Function<Path, Path> createPathRelativizer(Path path, boolean doRelativize) {
		return (p) -> (doRelativize ? path.relativize(p) : p);
	}


	public static void listGitignoreFiles(Path projectPath, boolean relativizeOutputPaths, Path replaceAllWithThis) throws IOException {
		val gitignoreStructure = RepositoryStructure.forTextFile(".gitignore", "ASCII");
		val correctGitignore = replaceAllWithThis != null ? new String(Files.readAllBytes(projectPath.resolve(replaceAllWithThis)), Charset.forName("ASCII")) : null;
		val gitignores = ProjectsUtil.getProjectInfos(Arrays.asList(projectPath), gitignoreStructure);
		val uniqueGitignores = new HashMap<String, List<Path>>();

		for(val gitignore : gitignores) {
			// write
			if(correctGitignore != null) {
				gitignoreStructure.saveProjectInfo(gitignore.getKey(), correctGitignore);
			}
			// read
			List<Path> sameGitignores = uniqueGitignores.get(gitignore.getValue());
			if(sameGitignores == null) {
				sameGitignores = new ArrayList<Path>();
				uniqueGitignores.put(gitignore.getValue(), sameGitignores);
			}
			sameGitignores.add(gitignore.getKey());
		}

		for(val unique : uniqueGitignores.entrySet()) {
			System.out.println(unique.getValue() + "\n====\n" + unique.getKey() + "\n====\n");
		}
	}


	public static void gitStatus(Path projectsPath, String projectName) {
		String gitPath = "\"C:/Program Files (x86)/Git/bin/git.exe\"";

		val modifiedGitFiles = GitStatus.runGitStatus(new GitStatus.Options(gitPath, projectsPath.resolve(Paths.get(projectName)).toString()));

		val filesOfInterest = ListUtil.filter(modifiedGitFiles, MainGitUtils::findUsefulGitFiles);
		System.out.println("git modified files:\n\t" + StringJoin.join(filesOfInterest, "\n\t") + "\n");
	}


	public static void listGitProjects(Path projectsPath, boolean relativizeOutputPaths) {
		val projects = ProjectsUtil.getProjectDirs(list(projectsPath));
		val res = ProjectsUtil.findGitDirs(projects);

		val resWith = sort(ListUtil.map(res.getMatches(), createPathRelativizer(projectsPath, relativizeOutputPaths)));
		System.out.println("With (" + resWith.size() + "):\n" + StringJoin.join(resWith, "\n") + "\n");

		val resWithout = sort(ListUtil.map(res.getFailedMatches(), createPathRelativizer(projectsPath, relativizeOutputPaths)));
		System.out.println("Without (" + resWithout.size() + "):\n" + StringJoin.join(resWithout, "\n"));
	}


	public static void listGitProjectStatuses(Path projectsPath, boolean relativizeOutputPaths) {
		String gitPath = "\"C:/Program Files (x86)/Git/bin/git.exe\"";

		val projects = ProjectsUtil.getProjectDirs(list(projectsPath));
		val res = ProjectsUtil.findGitDirs(projects);

		String[] resWith = res.getMatches().stream().map((p) -> {
			Path path = (relativizeOutputPaths ? projectsPath.relativize(p) : p);
			return entry(path, GitStatus.runGitStatus(new GitStatus.Options(gitPath, p.toString())));
		}).filter((projectsFiles) -> {
			return projectsFiles.getValue().stream().filter(MainGitUtils::findUsefulGitFiles).count() > 0;
		}).sorted(Entry.comparingByKey()).map((projectsFiles) -> {
			return projectsFiles.getKey().toString() + "\n\t" + StringJoin.join(projectsFiles.getValue(), "\n\t") + "\n";
		}).toArray((s) -> new String[s]);
		System.out.println("With (" + resWith.length + "):\n" + StringJoin.join(resWith, "\n") + "\n");

		Path[] resWithout = res.getFailedMatches().stream().map(createPathRelativizer(projectsPath, relativizeOutputPaths)).sorted().toArray((s) -> new Path[s]);
		System.out.println("Without (" + resWithout.length + "):\n" + StringJoin.join(resWithout, "\n"));
	}


	public static void main(String[] args) throws IOException {
		val path = Paths.get("C:/Users/TeamworkGuy2/Documents/Java/Projects/");

		//listGitProjects(path, true);
		//listGitignoreFiles(path, true, null/*Paths.get("DependencyManagement/.gitignore")*/);
		//gitStatus(path, "JTextParser");
		listGitProjectStatuses(path, true);
	}

}
