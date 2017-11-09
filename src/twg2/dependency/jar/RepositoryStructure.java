package twg2.dependency.jar;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import twg2.dependency.models.PackageJson;
import twg2.functions.BiConsumerIo;
import twg2.functions.FunctionIo;
import twg2.io.json.Json;

/**
 * @author TeamworkGuy2
 * @since 2016-1-28
 */
public class RepositoryStructure<T> implements RepositoryBase.RepositoryLoad<T>, RepositoryBase.RepositorySave<T> {
	private final String projFileName;
	private final FunctionIo<Path, Path> getProjFile;
	private FunctionIo<Path, T> loadProjFile;
	private BiConsumerIo<Path, T> saveProjFile;


	/**
	 * @param fileName a relative file name or path to resolve against project directories, this is used to construct a project definition file to attempt to load
	 * @param projFileLoader a function which takes a path and returns data related to that path, most likely by loading the file and returning some part of its contents
	 */
	public RepositoryStructure(String fileName, FunctionIo<Path, T> projFileLoader, BiConsumerIo<Path, T> projFileSaver) {
		this.projFileName = fileName;
		this.getProjFile = (p) -> p.resolve(fileName);
		this.loadProjFile = projFileLoader;
		this.saveProjFile = projFileSaver;
	}


	/**
	 * @param projFileGetter a function given a project directory, returns the resolved project definition file path to attempt to load
	 * @param projFileLoader a function which takes a path and returns data related to that path, most likely by loading the file and returning some part of its contents
	 */
	public RepositoryStructure(FunctionIo<Path, Path> projFileGetter, FunctionIo<Path, T> projFileLoader, BiConsumerIo<Path, T> projFileSaver) {
		this.projFileName = null;
		this.getProjFile = projFileGetter;
		this.loadProjFile = projFileLoader;
		this.saveProjFile = projFileSaver;
	}


	@Override
	public Path getProjectFile(Path proj) throws IOException {
		return getProjFile.apply(proj);
	}


	@Override
	public T loadProjectInfo(Path proj) throws IOException {
		Path file = getProjFile.apply(proj);
		if(Files.exists(file)) {
			return loadProjFile.apply(file);
		}
		return null;
	}


	@Override
	public boolean saveProjectInfo(Path proj, T data) throws IOException {
		Path file = getProjFile.apply(proj);
		if(Files.exists(file)) {
			saveProjFile.accept(file, data);
			return true;
		}
		return false;
	}


	@Override
	public String toString() {
		return "repository file '" + this.projFileName + "'";
	}


	/** Create a repository structure which reads a text file from a project using the specified text encoding and returns the file's contents as a string
	 */
	public static RepositoryStructure<String> forTextFile(String fileName, String encoding) {
		Charset cs = Charset.forName(encoding);
		return new RepositoryStructure<>(fileName, (path) -> new String(Files.readAllBytes(path), cs), (path, str) -> Files.write(path, str.getBytes(cs)));
	}


	/** Create a repository structure which reads a node.js style package.json file and returns it's contents as a {@link PackageJson} object
	 */
	public static RepositoryStructure<PackageJson> forPackageJson(String fileName) {
		return new RepositoryStructure<>(fileName, (path) -> new PackageJson().fromJsonFile(path.toString()), (path, pkg) -> {
			try (FileWriter dst = new FileWriter(path.toFile())) {
				pkg.toJson(Json.getDefaultInst(), dst);
			}
		});
	}

}
