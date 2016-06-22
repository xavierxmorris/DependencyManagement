package twg2.dependency.eclipseProject;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import lombok.Getter;
import lombok.val;
import twg2.dependency.jar.PackageJson;
import twg2.io.serialize.xml.XmlAttributes;
import twg2.io.serialize.xml.XmlInput;
import twg2.io.serialize.xml.XmlOutput;
import twg2.io.serialize.xml.Xmlable;
import twg2.text.stringUtils.StringCompare;

/**
 * @author TeamworkGuy2
 * @since 2015-5-25
 */
public class ClassPathEntry implements Xmlable {
	private static String CLASS_PATH_ENTRY_KEY = "classpathentry";
	// package-private
	@Getter String kind;
	@Getter String path;
	@Getter String sourcePath;


	public ClassPathEntry() {
	}


	public ClassPathEntry(String kind, String path, String sourcePath) {
		this.kind = kind;
		this.path = path;
		this.sourcePath = sourcePath;
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
	public String toString() {
		return CLASS_PATH_ENTRY_KEY + " " + kind + ": " + path + (sourcePath != null ? (", " + sourcePath) : "");
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


	public static ClassPathEntry fromPackageLib(PackageJson pkg, Path basePackagesPath) {
		try {
			val kind = "lib";
			// TODO add support for CSS-dash-case
			val projName = EclipseClasspathUtils.libNameToProjectName(pkg.getName().replace('-', '_'));
			val sourcePath = '/' + projName;
			val path = basePackagesPath.resolve(pkg.getName() + '/' + pkg.getPropString("main")).toRealPath().toString().replace('\\', '/');
			return new ClassPathEntry(kind, path, sourcePath);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}