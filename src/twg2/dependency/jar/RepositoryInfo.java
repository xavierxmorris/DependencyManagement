package twg2.dependency.jar;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.val;
import twg2.functions.IoFunc;

/**
 * @author TeamworkGuy2
 * @since 2016-1-28
 * @param <T> the type of data which can be read the projects in this repository
 */
public interface RepositoryInfo<T> {

	public String getName();

	public RepositoryStructure<T> getStructure();

	public List<Path> getProjects();




	/**
	 * @author TeamworkGuy2
	 * @since 2016-1-28
	 * @param <U> the type of data which can be read from the projects in the repository info instance being constructed by this builder
	 */
	public static class Builder<U> {
		private static Path gitDir = Paths.get(".git");
		private static Path hgDir = Paths.get(".hg");
		private static Path svnDir = Paths.get(".svn");
		private static DirectoryStream.Filter<Path> dirFilter = (p) -> !p.endsWith(gitDir) && !p.endsWith(hgDir) && !p.endsWith(svnDir);

		private @Getter String name;
		private @Getter RepositoryStructure<U> structure;
		private @Getter List<Path> projects;


		public Builder(String name, RepositoryStructure<U> structure) {
			this.name = name;
			this.structure = structure;
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


		public int addRepository(Path repository, IoFunc.FunctionIo<Path, Boolean> isProjectDir) {
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


		public RepositoryInfo.Impl<U> build() {
			return new RepositoryInfo.Impl<>(name, structure, new ArrayList<>(projects));
		}

	}




	/**
	 * @author TeamworkGuy2
	 * @since 2016-1-28
	 * @param <V> the type of data which can be read from the projects in this repository
	 */
	public static class Impl<V> implements RepositoryInfo<V> {
		private final @Getter String name;
		private final @Getter RepositoryStructure<V> structure;
		private final @Getter List<Path> projects;


		public Impl(String name, RepositoryStructure<V> structure, List<Path> projects) {
			this.name = name;
			this.structure = structure;
			this.projects = projects;
		}


		@Override
		public String toString() {
			return name + ": " + projects;
		}

	}

}
