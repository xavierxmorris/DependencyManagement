package twg2.dependency.git;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import lombok.val;
import twg2.collections.builder.AddCondition;
import twg2.collections.builder.ListAdd;
import twg2.collections.builder.ListUtil;
import twg2.io.files.FileFilterUtil;
import twg2.text.stringUtils.StringJoin;

/**
 * @author TeamworkGuy2
 * @since 2016-08-07
 */
public class MainFindGit {


	private static FileFilterUtil.Cache filterDirs(List<? extends Path> dirs) {
		val filter = new FileFilterUtil.Builder()
				.addFilter((f) -> {
					val gitDir = f.resolve(".git");
					return Files.exists(gitDir, LinkOption.NOFOLLOW_LINKS);
				})
				.build(true, true);
		dirs.stream().filter(filter.getFileFilter()).collect(() -> new ArrayList<Path>(), (list, f) -> list.add(f), (a, b) -> ListAdd.addListToList(b, a, AddCondition.ADD_ALL));
		return filter;
	}


	private static List<Path> getProjectDirs(List<? extends Path> parentDirs) {
		val dst = new ArrayList<Path>();
		for(int i = 0, size = parentDirs.size(); i < size; i++) {
			val dir = parentDirs.get(i);
			try(val dirStream = Files.newDirectoryStream(dir, (f) -> Files.isDirectory(f, LinkOption.NOFOLLOW_LINKS))) {
				for(val file : dirStream) {
					if(!file.getFileName().toString().startsWith(".")) {
						dst.add(file);
					}
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		return dst;
	}


	@SafeVarargs
	private static <T> List<T> list(T... args) {
		return Arrays.asList(args);
	}


	public static void main(String[] args) {
		val gitPath = "\"C:/Program Files (x86)/Git/bin/git.exe\"";
		val path = Paths.get("C:/Users/TeamworkGuy2/Documents/Java/Projects/");

		val modifiedGitFiles = GitStatus.runGitStatus(new GitStatus.Options(gitPath, path.resolve(Paths.get("DependencyManagement")).toString()));

		val filesOfInterest = ListUtil.filter(modifiedGitFiles, (fi) -> !fi.path.endsWith("CHANGELOG.md"));
		System.out.println("git modified files:\n\t" + StringJoin.Objects.join(filesOfInterest, "\n\t") + "\n");

		if(3.1/1.4 > 1.6) { return; }

		val projects = getProjectDirs(list(path));
		val res = filterDirs(projects);

		System.out.println("With:\n" + StringJoin.Objects.join(res.getMatches().stream().sorted().toArray((s) -> new Path[s]), "\n") + "\n");
		System.out.println("Without:\n" + StringJoin.Objects.join(res.getFailedMatches().stream().sorted().toArray((s) -> new Path[s]), "\n"));
	}

}
