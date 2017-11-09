package twg2.dependency.eclipseProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import lombok.val;
import twg2.collections.dataStructures.PairList;
import twg2.dependency.jar.RepositoryStructure;
import twg2.functions.FunctionIo;
import twg2.io.files.FileFilterUtil;

/** Utility methods for finding, filtering, and looking up directories and files inside the projects in a repository
 * @author TeamworkGuy2
 * @since 2016-08-15
 */
public class ProjectsUtil {

	/** Filter a list of directories and create a filter result containing the directories which are git repositories and those which are not
	 * @param dirs the list of directories
	 * @return a {@link twg2.io.files.FileFilterUtil.Cache FileFilterUtil.Cache}
	 * @see #findDirsContaining(List, String)
	 */
	public static FileFilterUtil.Cache findGitDirs(List<? extends Path> dirs) {
		return findDirsContaining(dirs, ".git");
	}


	/** Filter a list of directories and create a filter result containing the directories which contain a child file or direct specified by the {@code fileOrDirName} parameter
	 * @param dirs the list of directories
	 * @return a {@link twg2.io.files.FileFilterUtil.Cache FileFilterUtil.Cache}
	 */
	public static FileFilterUtil.Cache findDirsContaining(List<? extends Path> dirs, String fileOrDirName) {
		val filter = new FileFilterUtil.Builder().addFilter((f) -> {
			val gitDir = f.resolve(fileOrDirName);
			return Files.exists(gitDir, LinkOption.NOFOLLOW_LINKS);
		}).build(true, true);
		dirs.stream().filter(filter.getFileFilter()).count(); // we just run the filter over the values to populate the filter's cache, ignoring the result
		return filter;
	}


	/** Get child directories (except those starting with '.') of all of the specified directories 
	 * @param parentDirs the list of parent directories to search
	 * @return list of child directories
	 */
	public static List<Path> getProjectDirs(List<? extends Path> parentDirs) {
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


	public static <T> PairList<Path, T> getProjectInfos(List<? extends Path> parentDirs, RepositoryStructure<T> structure) throws IOException {
		val projs = findDirsContaining(getProjectDirs(parentDirs), ".gitignore").getMatches();

		val dst = new PairList<Path, T>();
		for(val proj : projs) {
			val res = structure.loadProjectInfo(proj);
			dst.add(proj, res);
		}
		return dst;
	}


	/** Load one project file per project
	 * @param projectsDir the directory containing the project directories (directories not starting with '.')
	 * @param fileName the name of the file to load from each project
	 * @param func a function which takes a file name returns loads project data
	 * @return a map of project names associated with parsed project data based on a file name
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public static <R> Map<String, R> loadProjectFiles(File projectsDir, String fileName, FunctionIo<File, R> func) throws FileNotFoundException, IOException, XMLStreamException {
		File[] dirs = projectsDir.listFiles((file) -> file.isDirectory() && !file.getName().startsWith("."));
		val projectFiles = new HashMap<String, R>();

		for(File proj : dirs) {
			File file = new File(proj, fileName);
			if(file.exists()) {
				val parsedFile = func.apply(file);
				projectFiles.put(proj.getName(), parsedFile);
			}
		}

		return projectFiles;
	}

}
