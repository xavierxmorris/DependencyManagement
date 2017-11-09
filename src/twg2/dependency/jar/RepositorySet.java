package twg2.dependency.jar;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.val;

import twg2.functions.FunctionIo;

/**
 * @author TeamworkGuy2
 * @since 2016-1-28
 */
public class RepositorySet {
	private final @Getter String name;
	private final @Getter List<Path> projects;


	public RepositorySet(String name, List<Path> projects) {
		this.name = name;
		this.projects = Collections.unmodifiableList(new ArrayList<>(projects));
	}


	@Override
	public String toString() {
		return name + ": " + projects;
	}




	/**
	 * @author TeamworkGuy2
	 * @since 2016-1-28
	 */
	public static class Builder {
		private static Path gitDir = Paths.get(".git");
		private static Path hgDir = Paths.get(".hg");
		private static Path svnDir = Paths.get(".svn");
		private static DirectoryStream.Filter<Path> dirFilter = (p) -> !p.endsWith(gitDir) && !p.endsWith(hgDir) && !p.endsWith(svnDir);

		private @Getter String name;
		private @Getter List<Path> projects;


		public Builder(String name) {
			this.name = name;
			this.projects = new ArrayList<>();
		}


		public int addRepository(Path repository) {
			int size = projects.size();
			try(val projs = Files.newDirectoryStream(repository, dirFilter)) {
				projs.forEach(projects::add);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
			return projects.size() - size;
		}


		public int addRepository(Path repository, FunctionIo<Path, Boolean> isProjectDir) {
			int size = projects.size();
			try(val projs = Files.newDirectoryStream(repository, (p) -> dirFilter.accept(p) && isProjectDir.apply(p))) {
				projs.forEach(projects::add);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
			return projects.size() - size;
		}


		public boolean addProject(Path proj) {
			try {
				if(dirFilter.accept(proj)) {
					projects.add(proj);
					return true;
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
			return false;
		}


		public RepositorySet build() {
			return new RepositorySet(name, new ArrayList<>(projects));
		}

	}

}
