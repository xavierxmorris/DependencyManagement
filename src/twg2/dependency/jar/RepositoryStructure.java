package twg2.dependency.jar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import twg2.functions.IoFunc;
import twg2.functions.IoFunc.FunctionIo;

/**
 * @author TeamworkGuy2
 * @since 2016-1-28
 */
public class RepositoryStructure {
	private final IoFunc.FunctionIo<Path, Path> projToPkgFile;


	/**
	 * @param pkgFileName a relative file name or path to resolve against project directories, this is used to construct a project definition file to attempt to load
	 */
	public RepositoryStructure(String pkgFileName) {
		this.projToPkgFile = (p) -> p.resolve(pkgFileName);
	}


	/**
	 * @param projToPkgFile a function given a project directory, returns the resolved project definition file path to attempt to load
	 */
	public RepositoryStructure(FunctionIo<Path, Path> projToPkgFile) {
		this.projToPkgFile = projToPkgFile;
	}


	public Path getProjectPackageFile(Path proj) throws IOException {
		return projToPkgFile.apply(proj);
	}


	public PackageJson loadProjectInfo(Path proj) throws IOException {
		Path pkgFile = projToPkgFile.apply(proj);
		if(Files.exists(pkgFile)) {
			return PackageJson.read(pkgFile.toString());
		}
		return null;
	}

}
