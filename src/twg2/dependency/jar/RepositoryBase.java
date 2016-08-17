package twg2.dependency.jar;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author TeamworkGuy2
 * @since 2016-08-17
 * @param <T>
 */
public interface RepositoryBase<T> {

	public Path getProjectFile(Path proj) throws IOException;


	/**
	 * @author TeamworkGuy2
	 * @since 2016-08-17
	 * @param <T>
	 */
	public static interface RepositoryLoad<T> extends RepositoryBase<T> {

		public T loadProjectInfo(Path proj) throws IOException;

	}


	/**
	 * @author TeamworkGuy2
	 * @since 2016-08-17
	 * @param <T>
	 */
	public static interface RepositorySave<T> extends RepositoryBase<T> {

		public boolean saveProjectInfo(Path proj, T data) throws IOException;

	}

}
