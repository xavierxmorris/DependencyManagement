package twg2.dependency.eclipseProject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import lombok.val;
import twg2.collections.builder.AddedRemoved;
import twg2.collections.builder.ListDiff;
import twg2.collections.builder.ListUtil;
import twg2.collections.dataStructures.PairList;
import twg2.dependency.jar.LibrarySet;
import twg2.dependency.jar.PackageSet;
import twg2.dependency.jar.RepositorySet;
import twg2.dependency.jar.RepositoryStructure;
import twg2.dependency.models.LibraryJson;
import twg2.dependency.models.PackageJson;
import twg2.io.fileLoading.ValidInvalid;
import twg2.io.json.Json;
import twg2.text.stringUtils.StringCase;
import twg2.text.stringUtils.StringSplit;

/**
 * @author TeamworkGuy2
 * @since 2015-5-31
 */
public class MainEclipseClasspathUtils {

	public static Map<String, EclipseClasspathEntries> loadProjectClasspathFiles(File projectsDir) throws FileNotFoundException, IOException, XMLStreamException {
		return ProjectsUtil.loadProjectFiles(projectsDir, ".classpath", (file) -> {
			return EclipseClasspathDoc.fromXml(file, new FileInputStream(file));
		});
	}


	public static Map<String, PackageJson> loadProjectPackageLibFiles(File projectsDir) throws FileNotFoundException, IOException, XMLStreamException {
		return ProjectsUtil.loadProjectFiles(projectsDir, "package-lib.json", (file) -> {
			return new PackageJson().fromJsonFile(file);
		});
	}


	public static Map<String, EclipseClasspathEntries> getProjectsContainingLibs(Map<String, EclipseClasspathEntries> projectFiles, String type, List<String> containsList, boolean containsAll) throws FileNotFoundException, IOException, XMLStreamException {
		Map<String, EclipseClasspathEntries> res = new HashMap<>();

		for(val cpFileEntry : projectFiles.entrySet()) {
			val cpFile = cpFileEntry.getValue();
			// the list of project imports
			List<String> importJars = ListUtil.map(cpFile.getClassPathEntries(type), (cpe) -> {
				String[] pathParts = cpe.path.split("/");
				return pathParts[pathParts.length - 1];
			});

			// the list of does contain imports
			if(containsAll ? importJars.containsAll(containsList) : containsAny(importJars, containsList)) {
				res.put(cpFile.getFile().getParentFile().getName(), cpFile);
			}
		}
		return res;
	}


	public static Map<String, PackageJson> getProjectsContainingPkgs(Map<String, PackageJson> projectFiles, String type, List<String> containsList, boolean containsAll) throws FileNotFoundException, IOException, XMLStreamException {
		Map<String, PackageJson> res = new HashMap<>();

		for(val pkgFileEntry : projectFiles.entrySet()) {
			val pkgFile = pkgFileEntry.getValue();
			// the list of project imports
			val importPkgs = pkgFile.getDependencies().keySet();

			// the list of does contain imports
			if(containsAll ? importPkgs.containsAll(containsList) : containsAny(importPkgs, containsList)) {
				res.put(pkgFile.getName(), pkgFile);
			}
		}
		return res;
	}


	/**
	 * @param type the type of class path entry imports to compare against, null to compare against all
	 * @return a map of project directories to list of imports from the {@code doesInclude} list that do not appear in that project's imports
	 * and a list of project names containing the {@code ifContainsList} but not the {@code doesInclude} list
	 */
	public static ValidInvalid<PairList<String, List<String>>, List<String>> getProjectsContainingLibsMissingLibs(Map<String, EclipseClasspathEntries> projectFiles,
			String type, List<String> ifContainsList, List<String> doesInclude) throws FileNotFoundException, IOException, XMLStreamException {
		PairList<String, List<String>> resultNotContain = new PairList<>();

		val containsButNotIncludes = new ArrayList<String>();

		for(val cpFileEntry : projectFiles.entrySet()) {
			val cpFile = cpFileEntry.getValue();
			val projectName = cpFile.getFile().getParentFile().getName();
			// the list of project imports
			List<String> importJars = ListUtil.map(cpFile.getClassPathEntries(type), (cpe) -> {
				String[] pathParts = cpe.path.split("/");
				return pathParts[pathParts.length - 1];
			});

			List<String> notContainList = new ArrayList<>();
			// the list of does contain imports
			if(importJars.containsAll(ifContainsList)) {
				for(String importStr : doesInclude) {
					if(!importJars.contains(importStr)) {
						notContainList.add(importStr);
					}
				}
				if(notContainList.size() > 0) {
					resultNotContain.add(projectName, notContainList);
				}
				containsButNotIncludes.add(projectName);
			}
		}
		return ValidInvalid.of(resultNotContain, containsButNotIncludes);
	}


	public static void printProjectDependencyTree(File projectsDir, String projName) throws FileNotFoundException, IOException, XMLStreamException {
		val projectFiles = loadProjectClasspathFiles(projectsDir);
		StringBuilder tmpSb = new StringBuilder();
		_printProjectDependencyTree(projectFiles, projName, tmpSb);
	}


	public static void _printProjectDependencyTree(Map<String, EclipseClasspathEntries> classPathFiles, String projName, StringBuilder indent) throws FileNotFoundException, IOException, XMLStreamException {
		boolean found = false;
		for(val cpFileEntry : classPathFiles.entrySet()) {
			val cpFile = cpFileEntry.getValue();

			if(cpFileEntry.getKey().equals(projName)) {
				found = true;
				for(ClassPathEntry cpEntry : cpFile.getLibClassPathEntries()) {
					if(ClassPathEntry.isLib(cpEntry)) {
						String fileName = StringSplit.firstMatch(StringSplit.lastMatch(cpEntry.path, '/'), '.');
						String possibleProjName = libNameToProjectName(fileName);

						System.out.println(indent.toString() + possibleProjName);

						indent.append("\t");
						_printProjectDependencyTree(classPathFiles, possibleProjName, indent);
						indent.setLength(indent.length() - 1);
					}
				}
			}
		}

		if(!found) {
			System.out.println("!could not find: '" + projName + "'");
		}
	}


	private static <T> boolean containsAny(Collection<T> src, Collection<T> containsAny) {
		for(T elem : containsAny) {
			if(src.contains(elem)) {
				return true;
			}
		}
		return false;
	}


	private static <T, R> List<R> printSorted(Collection<T> coll, Function<T, R> func, Comparator<R> compare) {
		List<R> res = ListUtil.map(coll, func);
		res.sort(compare);

		for(R r : res) {
			System.out.println(r);
		}

		return res;
	}


	public static final String libNameToProjectName(String libName) {
		String projName = StringCase.toTitleCase(libName);
		if(projName.startsWith("J") && !projName.startsWith("Json") && !projName.startsWith("Jackson")) {
			projName = "" + Character.toUpperCase(projName.charAt(0)) + Character.toUpperCase(projName.charAt(1)) + projName.substring(2);
		}
		return projName;
	}


	public static void printProjectsContainingLibs(File projects, String... expectedImports) throws FileNotFoundException, IOException, XMLStreamException {
		List<String> expectImports = Arrays.asList(expectedImports);
		boolean containsAll = true;

		val projectFiles = loadProjectClasspathFiles(projects);
		val projFiles = getProjectsContainingLibs(projectFiles, null, expectImports, containsAll);

		System.out.println("Projects containing packages " + expectImports.toString() + ":");

		val res = printSorted(projFiles.entrySet(), (p) -> p.getKey(), (a, b) -> a.compareTo(b));

		System.out.println("\ncount: " + res.size() + " (total: " + projectFiles.size() + ")\n");
	}


	public static void printProjectsContainingLibsMissingLibs(File projects, List<String> expectedImports, String doesContainAry) throws FileNotFoundException, IOException, XMLStreamException {
		List<String> doesContain = Arrays.asList(doesContainAry);

		val projectFiles = loadProjectClasspathFiles(projects);
		val resRes = getProjectsContainingLibsMissingLibs(projectFiles, null, expectedImports, doesContain);
		val res = resRes.valid;
		val filteredProjects = resRes.invalid;

		System.out.println("Projects containing packages " + expectedImports + " but missing " + doesContain.toString() + ":");
		List<String> missingImports = new ArrayList<String>();
		for(int i = 0, size = res.size(); i < size; i++) {
			for(String missingImport : res.getValue(i)) {
				missingImports.add(missingImport);
			}
			System.out.println(res.getKey(i) + " - " + missingImports.toString());
			missingImports.clear();
		}
		System.out.println("\ncount: " + res.size() + " of (" + filteredProjects.size() + " filtered) (" + projectFiles.size() + " total)\n");
	}


	public static void printProjectsContainingPkgs(File projects, String... expectedPkgImports) throws FileNotFoundException, IOException, XMLStreamException {
		List<String> expectPkgImports = Arrays.asList(expectedPkgImports);
		boolean containsAll = true;

		val projectFiles = loadProjectPackageLibFiles(projects);
		val projFiles = getProjectsContainingPkgs(projectFiles, null, expectPkgImports, containsAll);

		System.out.println("Projects containing packages " + expectPkgImports.toString() + ":");

		val res = printSorted(projFiles.entrySet(), (p) -> p.getKey(), (a, b) -> a.compareTo(b));

		System.out.println("\ncount: " + res.size() + " (total: " + projectFiles.size() + ")\n");
	}



	/** Load a classpath file, remove existing libs, load with libs from PackageJson, print before and after classpath files to System.out
	 */
	public static void checkAndOfferToReplaceLibs(File projsPath, File libsPath, String projName) throws IOException, TransformerException {
		// load .classpath dependencies
		val classPathFile = new File(projsPath, projName + "/.classpath");
		val doc = EclipseClasspathDoc.fromXml(classPathFile, new FileInputStream(classPathFile));
		//val cpEntries = doc.getLibClassPathEntries();

		// load all library package-lib.json files
		val structure = RepositoryStructure.forPackageJson("package-lib.json");
		val javaRepoBldr = new RepositorySet.Builder("java-projects");
		javaRepoBldr.addRepository(projsPath.toPath());
		val projSet = new PackageSet(structure, Arrays.asList(javaRepoBldr.build()), true);
		val libNodes = Json.getDefaultInst().getObjectMapper().readTree(new File("C:/Users/TeamworkGuy2/Documents/Java/Libraries/libraries.json")).get("libraries").iterator();
		val libs = new LibrarySet(ListUtil.map(libNodes, (node) -> new LibraryJson().fromJson(node)));

		// load target project package-lib.json dependencies
		val packageFile = new File(projsPath, projName + "/package-lib.json");
		val pkgInfo = new PackageJson().fromJsonFile(packageFile.toString());
		val pkgDependencies = projSet.loadDependencies(pkgInfo, libs);
		val pkgClassPathEntries = ListUtil.map(pkgDependencies.entrySet(), (e) -> {
			if(e.getValue().isPackage()) {
				return ClassPathEntry.fromDependency(e.getValue().getPackageInfo(), libsPath.toPath());
			}
			else if(e.getValue().isLibrary()) {
				return ClassPathEntry.fromDependency(e.getValue().getLibraryInfo(), libsPath.toPath());
			}
			else {
				throw new Error("dependency '" + e.getValue() + "' is not a package or library, no other known valid type");
			}
		});
		pkgClassPathEntries.sort((a, b) -> a.getPath().compareTo(b.getPath()));

		// print original .classpath dependencies
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.out.println("original:\n");
		doc.toXml(out);
		val originalXml = new String(out.toByteArray(), "UTF-8");
		System.out.println(originalXml);

		val oldLibs = doc.getLibClassPathEntries();
		val libsDiff = ListDiff.diff(oldLibs, pkgClassPathEntries);

		// remove existing libs from .classpath, add parsed package-lib.json dependencies
		EclipseClasspathXmlManipulator.removeAllLibs(doc.doc);
		EclipseClasspathXmlManipulator.addLibs(doc.doc, pkgClassPathEntries);

		// print new .classpath dependencies
		out = new ByteArrayOutputStream();
		System.out.println("\n\n====\n\nModified:\n");
		doc.toXml(out);
		val resultXml = new String(out.toByteArray(), "UTF-8");
		System.out.println(resultXml);

		// print difference between original and new .classpath dependencies
		printLibsDiff(libsDiff);

		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);

		System.out.println("would you like to overwrite '" + classPathFile.getCanonicalPath() + "' (y/n): ");
		String input = in.nextLine();

		if("Y".equals(input.toUpperCase())) {
			OutputStreamWriter writer = null;
			try {
				writer = new OutputStreamWriter(new FileOutputStream(classPathFile));
				writer.write(resultXml);
			} finally {
				if(writer != null) {
					try {
						writer.close();
					} catch(IOException e) {
						throw new UncheckedIOException(e);
					}
				}
			}
		}
	}


	/** Print difference returned from a call to {@link ListDiff#diff(List, List)}
	 * @return the sum of added and removed items in the {@code libsDiff}
	 */
	private static final <T> int printLibsDiff(AddedRemoved<T> libsDiff) {
		int added = 0, removed = 0;
		if((added = libsDiff.getAdded().size()) > 0 || (removed = libsDiff.getRemoved().size()) > 0) {
			System.out.println("\n\n====\n\nLib-diff:\n");
			if(added > 0) {
				System.out.println("Added:");
				libsDiff.getAdded().stream().forEach(System.out::println);
			}
			if(removed > 0) {
				System.out.println("\nRemoved:");
				libsDiff.getRemoved().stream().forEach(System.out::println);
			}
		}
		else {
			System.out.println("\nNo-diff");
		}
		return added + removed;
	}


	public static void main(String[] args) throws FileNotFoundException, IOException, XMLStreamException, TransformerException {
		File projects = new File("C:/Users/TeamworkGuy2/Documents/Java/Projects/");
		File libraries = new File("C:/Users/TeamworkGuy2/Documents/Java/Libraries/");

		//printProjectDependencyTree(projects, "ParserTools");
		//printProjectsContainingLibsMissingLibs(projects, Arrays.asList("jrange.jar"), "jcollection_interfaces.jar");
		printProjectsContainingLibs(projects, "data_transfer.jar");
		//printProjectsContainingPkgs(projects, "jparser-primitive");
		//checkAndOfferToReplaceLibs(projects, libraries, "JParseCode"); // DependencyManagement
	}

}
