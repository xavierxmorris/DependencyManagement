package twg2.dependency.test;

import java.util.HashMap;
import java.util.Map;

import lombok.val;
import twg2.collections.builder.MapBuilder;
import twg2.dependency.eclipseProject.ClassPathEntry;
import twg2.io.files.FileUtil;
import twg2.text.stringUtils.StringSplit;
import twg2.tuple.Entries;

public final class ClasspathExampleFiles {
	private static String nwln = System.getProperty("line.separator");

	public static String indentation = "\t";

	public static final Map<String, Object> dependencyTreeLibs = MapBuilder.of(
		Entries.of("jcli", MapBuilder.of(
			Entries.of("jarray-util", null),
			Entries.of("jcollection-util", MapBuilder.of(
				Entries.of("jcollection-interfaces", null)
			)),
			Entries.of("jfunc", null),
			Entries.of("jtext-util", null)
		))
	);


	public static ClassPathEntry getJCollectionInterfacesEntry() {
		return new ClassPathEntry("lib", "C:/Users/TeamworkGuy2/Documents/Java/Libraries/jcollection-interfaces/jar/jcollection_interfaces.jar", "/JCollectionInterfaces");
	}


	/** root */
	public static String getJParameter(String standalone, ClassPathEntry... additionalLibs) {
		return header(nwln, standalone) +
		"<classpath>" + nwln +
		"	<classpathentry kind=\"src\" path=\"src\"/>" + nwln +
		"	<classpathentry kind=\"src\" path=\"test\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.8\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.junit.JUNIT_CONTAINER/4\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.USER_LIBRARY/TestChecks\"/>" + nwln +
		"	<classpathentry kind=\"output\" path=\"bin\"/>" + nwln +
		sortAndStringifyLibs("\t", nwln, additionalLibs,
			"<classpathentry kind=\"lib\" path=\"C:/Users/TeamworkGuy2/Documents/Java/Libraries/jarray-util/jar/jarray_util.jar\" sourcepath=\"/JArrayUtil\"/>",
			"<classpathentry kind=\"lib\" path=\"C:/Users/TeamworkGuy2/Documents/Java/Libraries/jcollection-util/jar/jcollection_util.jar\" sourcepath=\"/JCollectionUtility\"/>",
			"<classpathentry kind=\"lib\" path=\"C:/Users/TeamworkGuy2/Documents/Java/Libraries/jfunc/jar/jfunc.jar\" sourcepath=\"/JFunc\"/>",
			"<classpathentry kind=\"lib\" path=\"C:/Users/TeamworkGuy2/Documents/Java/Libraries/jtext-util/jar/jtext_util.jar\" sourcepath=\"/JTextUtil\"/>"
		) +
		"</classpath>";
	}


	/** level 2 */
	public static String getJArrayUtil(String standalone, ClassPathEntry... additionalLibs) {
		return header(nwln, standalone) +
		"<classpath>" + nwln +
		"	<classpathentry kind=\"src\" path=\"src\"/>" + nwln +
		"	<classpathentry kind=\"src\" path=\"test\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.8\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.junit.JUNIT_CONTAINER/4\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.USER_LIBRARY/ANTLR\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.USER_LIBRARY/TemplateUtil\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.USER_LIBRARY/TestChecks\"/>" + nwln +
		"	<classpathentry kind=\"output\" path=\"bin\"/>" + nwln +
		"</classpath>";
	}

	public static String getJCollectionUtil(String standalone, ClassPathEntry... additionalLibs) {
		return header(nwln, standalone) +
		"<classpath>" + nwln +
		"	<classpathentry kind=\"src\" path=\"src\"/>" + nwln +
		"	<classpathentry kind=\"src\" path=\"test\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.8\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.junit.JUNIT_CONTAINER/4\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.USER_LIBRARY/ANTLR\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.USER_LIBRARY/TemplateUtil\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.USER_LIBRARY/TestChecks\"/>" + nwln +
		sortAndStringifyLibs("\t", nwln, additionalLibs,
			"<classpathentry kind=\"lib\" path=\"C:/Users/TeamworkGuy2/Documents/Java/Libraries/jarray-util/jar/jarray_util.jar\" sourcepath=\"/JArrayUtil\"/>"
		) +
		"	<classpathentry kind=\"output\" path=\"bin\"/>" + nwln +
		"</classpath>";
	}

	public static String getJFunc(String standalone, ClassPathEntry... additionalLibs) {
		return header(nwln, standalone) +
		"<classpath>" + nwln +
		"	<classpathentry kind=\"src\" path=\"src\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.8\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.junit.JUNIT_CONTAINER/4\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.USER_LIBRARY/ANTLR\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.USER_LIBRARY/TemplateUtil\"/>" + nwln +
		"	<classpathentry kind=\"output\" path=\"bin\"/>" + nwln +
		"</classpath>";
	}

	public static String getJTextUtil(String standalone, ClassPathEntry... additionalLibs) {
		return header(nwln, standalone) +
		"<classpath>" + nwln +
		"	<classpathentry kind=\"src\" path=\"src\"/>" + nwln +
		"	<classpathentry kind=\"src\" path=\"test\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.8\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.junit.JUNIT_CONTAINER/4\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.USER_LIBRARY/ANTLR\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.USER_LIBRARY/TemplateUtil\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.USER_LIBRARY/TestChecks\"/>" + nwln +
		"	<classpathentry kind=\"output\" path=\"bin\"/>" + nwln +
		"</classpath>";
	}


	/** level 3 */
	public static String getJCollectionInterfaces(String standalone, ClassPathEntry... additionalLibs) {
		return header(nwln, standalone) +
		"<classpath>" + nwln +
		"	<classpathentry kind=\"src\" path=\"src\"/>" + nwln +
		"	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.8\"/>" + nwln +
		"	<classpathentry kind=\"output\" path=\"bin\"/>" + nwln +
		"</classpath>";
	}


	private static String header(String newline, String standalone) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"" + (standalone != null ? " standalone=\"" + standalone + "\"" : "") + "?>" + newline;
	}

	private static String sortAndStringifyLibs(String indentation, String newline, ClassPathEntry[] additionalLibs, String... existingLibs) {
		int totalSize = 0;
		int indentationLen = indentation.length();
		int nwlnLen = newline.length();
		val libs = new HashMap<String, String>();

		if(additionalLibs != null) {
			for(val lib : additionalLibs) {
				val libName = FileUtil.getFileNameWithoutExtension(lib.getPath()).replace('_', '-');
				val str = lib.toXml();
				totalSize += indentationLen + str.length() + nwlnLen;
				libs.put(libName, str);
			}
		}

		for(val lib : existingLibs) {
			val libName = FileUtil.getFileNameWithoutExtension(StringSplit.substring(lib, "path=\"", "\"")).replace('_', '-');
			totalSize += indentationLen + lib.length() + nwlnLen;
			libs.put(libName, lib);
		}

		val sb = new StringBuilder(totalSize);
		libs.entrySet().stream().sorted((a,b) -> a.getValue().compareTo(b.getValue())).forEach((a) -> { sb.append(indentation); sb.append(a.getValue()); sb.append(nwln); });
		return sb.toString();
	}

}
