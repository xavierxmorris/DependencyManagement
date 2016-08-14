package twg2.dependency.eclipseProject;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import lombok.Getter;
import lombok.val;
import twg2.collections.dataStructures.PairList;
import twg2.dependency.models.DependencyInfo;
import twg2.io.serialize.xml.XmlAttributes;
import twg2.io.serialize.xml.XmlInput;
import twg2.io.serialize.xml.XmlOutput;
import twg2.io.serialize.xml.Xmlable;
import twg2.text.stringSearch.StringCompare;

/**
 * @author TeamworkGuy2
 * @since 2015-5-25
 */
public class ClassPathEntry implements Comparable<ClassPathEntry>, Xmlable {
	public static String CLASS_PATH_ENTRY_KEY = "classpathentry";
	public static String inbetweenElementText = "\n\t";

	// package-private
	@Getter String kind;
	@Getter String path;
	@Getter String sourcePath;
	private PairList<String, String> attributes;


	public ClassPathEntry() {
	}


	public ClassPathEntry(String kind, String path, String sourcePath) {
		this.kind = kind;
		this.path = path;
		this.sourcePath = sourcePath;
	}


	public PairList<String, String> getAttributes() {
		if(this.attributes == null) {
			this.attributes = new PairList<>(4);
		}
		return this.attributes;
	}


	public boolean contains(String text) {
		return StringCompare.containsIgnoreCase(new String[] { kind, path, sourcePath }, text);
	}


	@Override
	public void readXML(XmlInput in) throws IOException, XMLStreamException {
		in.readStartBlock(CLASS_PATH_ENTRY_KEY);
		XmlAttributes attrs = in.getCurrentElementAttributes();
		List<String> attrNames = attrs.getAttributeNames();

		int kindIdx = attrNames.indexOf("kind");
		this.kind = kindIdx > -1 ? attrs.getAttributeString(kindIdx) : null;

		int pathIdx = attrNames.indexOf("path");
		this.path = pathIdx > -1 ? attrs.getAttributeString(pathIdx) : null;

		int sourcePathIdx = attrNames.indexOf("sourcepath");
		this.sourcePath = sourcePathIdx > -1 ? attrs.getAttributeString(sourcePathIdx) : null;

		in.readEndBlock();
	}


	@Override
	public void writeXML(XmlOutput out) throws IOException, XMLStreamException {
		throw new IllegalStateException("unimplemented");
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		//result = prime * result + ((sourcePath == null) ? 0 : sourcePath.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (!(obj instanceof ClassPathEntry)) { return false; }

		ClassPathEntry other = (ClassPathEntry) obj;
		if (kind == null) {
			if (other.kind != null) { return false; }
		} else if (!kind.equals(other.kind)) {
			return false;
		}

		if (path == null) {
			if (other.path != null) { return false; }
		} else if (!path.equals(other.path)) {
			return false;
		}

		//if (sourcePath == null) {
		//	if (other.sourcePath != null) { return false; }
		//} else if (!sourcePath.equals(other.sourcePath)) {
		//	return false;
		//}
		return true;
	}


	@Override
	public int compareTo(ClassPathEntry other) {
		// 'src' entries come first
		boolean thisSrc = "src".equals(this.kind);
		boolean otherSrc = "src".equals(other.kind);
		if(thisSrc || otherSrc) {
			int diff = (thisSrc && otherSrc ? this.path.compareTo(other.path) : thisSrc ? -1 : 1);
			return diff;
		}
		int diff = this.kind.compareTo(other.kind);
		if(diff == 0) { diff = this.path.compareTo(other.path); }
		return diff;
	}


	@Override
	public String toString() {
		return CLASS_PATH_ENTRY_KEY + " " + kind + ": " + path + (sourcePath != null ? (", " + sourcePath) : "");
	}


	public String toXml() {
		return "<" + CLASS_PATH_ENTRY_KEY + " kind=\"" + kind + "\" path=\"" + path + "\"" + (sourcePath != null ? " sourcepath=\"" + sourcePath + "\"" : "") + "/>";
	}


	/**
	 * @return true if the class path entry is a library resource, false if not
	 */
	public static final boolean isLib(ClassPathEntry cpEntry) {
		return cpEntry.kind != null && "lib".equals(cpEntry.kind);
	}


	public static final boolean isLib(String kind) {
		return kind != null && "lib".equals(kind);
	}


	public static ClassPathEntry fromDependency(DependencyInfo dep, Path basePackagesPath) {
		try {
			val kind = "lib";
			// TODO add support for CSS-dash-case
			val projName = MainEclipseClasspathUtils.libNameToProjectName(dep.getName().replace('-', '_'));

			val path = dependencyPath(basePackagesPath, dep, dep.getMain());

			String sourcePath = dep.getPropStringOptional("sourcepath", null);
			sourcePath = sourcePath != null ?
					(absFile(new File(sourcePath)) ? sourcePath : dependencyPath(basePackagesPath, dep, sourcePath)) :
					(dep.isPackage() ? '/' + projName : null);

			val cpe = new ClassPathEntry(kind, path, sourcePath);

			String javadoc = dep.getPropStringOptional("javadoc", null);
			if(javadoc != null) {
				val javadocFile = new File(javadoc);
				javadoc = "file:/" + (absFile(javadocFile) ? javadoc : dependencyPath(basePackagesPath, dep, javadoc));
				if(javadoc.endsWith("jar")) {
					javadoc = "jar:" + javadoc + "!/";
				}
				cpe.getAttributes().add("javadoc_location", javadoc);
			}

			return cpe;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}


	private static boolean absFile(File f) {
		return f.isFile() && f.canRead() && f.isAbsolute();
	}


	private static String dependencyPath(Path basePath, DependencyInfo dependency, String relativePath) throws IOException {
		return basePath.resolve(dependency.getName() + '/' + relativePath).toRealPath().toString().replace('\\', '/');
	}

}