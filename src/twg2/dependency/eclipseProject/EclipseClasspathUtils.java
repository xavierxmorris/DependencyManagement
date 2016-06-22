package twg2.dependency.eclipseProject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.file.Paths;
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
import twg2.collections.builder.ListDiff;
import twg2.collections.builder.ListDiff.AddedRemoved;
import twg2.collections.builder.ListUtil;
import twg2.collections.dataStructures.PairList;
import twg2.dependency.jar.PackageJson;
import twg2.dependency.jar.PackageSet;
import twg2.dependency.jar.RepositoryInfo;
import twg2.dependency.jar.RepositoryStructure;
import twg2.text.stringUtils.StringCase;
import twg2.text.stringUtils.StringSplit;

/**
 * @author TeamworkGuy2
 * @since 2015-5-31
 */
public class EclipseClasspathUtils {

	public static Map<String, EclipseClasspathEntries> loadProjectClasspathFiles(File projectsDir) throws FileNotFoundException, IOException, XMLStreamException {
		File[] dirs = projectsDir.listFiles((file) -> file.isDirectory() && !file.getName().startsWith("."));
		val projectFiles = new HashMap<String, EclipseClasspathEntries>();

		for(File proj : dirs) {
			File file = new File(proj, ".classpath");
			//val xmlReader = XmlHandler.createXMLReader(new FileReader(file), true, true, true);
			//val parsedCp = EclipseClasspathFile.readXml(file, xmlReader);
			val parsedCp = EclipseClasspathDoc.loadXmlDom(file, new FileInputStream(file));
			projectFiles.put(proj.getName(), parsedCp);
		}

		return projectFiles;
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


	/**
	 * @param type the type of class path entry imports to compare against, null to compare against all
	 * @return a map of project directories to list of imports from the {@code doesContain} list that do not appear in that project's imports
	 */
	public static PairList<String, List<String>> getProjectsContainingLibsMissingLibs(Map<String, EclipseClasspathEntries> projectFiles, String type, List<String> ifContainsList, List<String> doesContain) throws FileNotFoundException, IOException, XMLStreamException {
		PairList<String, List<String>> resultNotContain = new PairList<>();

		for(val cpFileEntry : projectFiles.entrySet()) {
			val cpFile = cpFileEntry.getValue();
			// the list of project imports
			List<String> importJars = ListUtil.map(cpFile.getClassPathEntries(type), (cpe) -> {
				String[] pathParts = cpe.path.split("/");
				return pathParts[pathParts.length - 1];
			});

			List<String> notContainList = new ArrayList<>();
			// the list of does contain imports
			if(importJars.containsAll(ifContainsList)) {
				for(String importStr : doesContain) {
					if(!importJars.contains(importStr)) {
						notContainList.add(importStr);
					}
				}
				if(notContainList.size() > 0) {
					resultNotContain.add(cpFile.getFile().getParentFile().getName(), notContainList);
				}
			}
		}
		return resultNotContain;
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


	public static void printProjectsContainingLibs(File projects) throws FileNotFoundException, IOException, XMLStreamException {
		List<String> expectImports = Arrays.asList("jmeta_access.jar");
		boolean containsAll = true;

		val projectFiles = loadProjectClasspathFiles(projects);
		val projFiles = getProjectsContainingLibs(projectFiles, null, expectImports, containsAll);
		val res = printSorted(projFiles.entrySet(), (p) -> p.getKey(), (a, b) -> a.compareTo(b));

		System.out.println("\ncount: " + res.size() + " (total: " + projectFiles.size() + ")");
	}


	public static void printProjectsContainingLibsMissingLibs(File projects) throws FileNotFoundException, IOException, XMLStreamException {
		List<String> expectImports = Arrays.asList("jrange.jar");
		List<String> doesContain = Arrays.asList("");
		//List<String> expectImports = Arrays.asList("jdata_util.jar", "jtext_util.jar", "type_util.jar", "jstream_util.jar", "ranges_util.jar", "jcollection_util.jar", "jfunction_util.jar", "parser_string.jar");
		//List<String> doesContain = Arrays.asList("io_util");

		val projectFiles = loadProjectClasspathFiles(projects);
		PairList<String, List<String>> res = getProjectsContainingLibsMissingLibs(projectFiles, null, expectImports, doesContain);

		for(int i = 0, size = res.size(); i < size; i++) {
			System.out.println("project: " + res.getKey(i));
			for(String missingImport : res.getValue(i)) {
				System.out.println("\tmissing: " + missingImport);
			}
		}
		System.out.println("\ncount: " + res.size());
	}


	/** Load a classpath file, remove existing libs, load with libs from PackageJson, print before and after classpath files to System.out
	 */
	public static void checkAndOfferToReplaceLibs() throws IOException, TransformerException {
		val projsPath = Paths.get("C:/Users/TeamworkGuy2/Documents/Java/Projects/");
		val libsPath = Paths.get("C:/Users/TeamworkGuy2/Documents/Java/Libraries/");

		// load .classpath dependencies
		val classPathFile = new File(projsPath.toFile(), "JParserDataTypeLike/.classpath");
		val doc = EclipseClasspathDoc.loadXmlDom(classPathFile, new FileInputStream(classPathFile));
		//val cpEntries = doc.getLibClassPathEntries();

		// load all library package-lib.json files
		val javaRepoBldr = new RepositoryInfo.Builder("java-projects", new RepositoryStructure("package-lib.json"));
		javaRepoBldr.addRepository(projsPath);
		val projSet = new PackageSet(Arrays.asList(javaRepoBldr.build()));

		// load target project package-lib.json dependencies
		val packageFile = new File(projsPath.toFile(), "JParserDataTypeLike/package-lib.json");
		val pkgInfo = PackageJson.read(packageFile.toString());
		val pkgDependencies = projSet.loadDependencies(pkgInfo);
		val pkgClassPathEntries = ListUtil.map(pkgDependencies.entrySet(), (e) -> ClassPathEntry.fromPackageLib(e.getValue().getKey(), libsPath));
		pkgClassPathEntries.sort((a, b) -> a.getPath().compareTo(b.getPath()));

		// print original .classpath dependencies
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.out.println("original:\n");
		doc.saveXmlDom(out);
		val originalXml = new String(out.toByteArray(), "UTF-8");
		System.out.println(originalXml);

		val oldLibs = doc.getLibClassPathEntries();
		val libsDiff = ListDiff.diff(oldLibs, pkgClassPathEntries);

		// remove existing libs from .classpath, add parsed package-lib.json dependencies
		EclipseClasspathReplace.removeLibs(doc.doc);
		EclipseClasspathReplace.addLibs(doc.doc, pkgClassPathEntries);

		// print new .classpath dependencies
		out = new ByteArrayOutputStream();
		System.out.println("\n\n====\n\nModified:\n");
		doc.saveXmlDom(out);
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
		File projects = new File("C:/Users/TeamworkGuy2/Documents/Java/Projects");

		//printProjectDependencyTree(projects, "ParserTools");
		//printProjectsContainingLibsMissingLibs(projects);
		printProjectsContainingLibs(projects);
		//checkAndOfferToReplaceLibs();
	}

}
