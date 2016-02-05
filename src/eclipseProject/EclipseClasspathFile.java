package eclipseProject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import twg2.io.serialize.base.DataElement;
import twg2.io.serialize.xml.XmlInput;
import twg2.text.stringUtils.StringJoin;

/**
 * @author TeamworkGuy2
 * @since 2015-5-25
 */
public class EclipseClasspathFile implements EclipseClasspathEntries {
	private static String ENTRY_KEY = "classpath";
	// package-private
	File file;
	List<ClassPathEntry> classPathEntries = new ArrayList<>();


	public EclipseClasspathFile(File file, List<ClassPathEntry> cpEntries) {
		this.file = file;
		this.classPathEntries = cpEntries;
	}


	@Override
	public File getFile() {
		return file;
	}


	@Override
	public List<ClassPathEntry> getLibClassPathEntries() {
		return getClassPathEntries("lib");
	}


	/**
	 * @param type the type of imports to parse (i.e. 'lib', 'con', 'src', 'output')
	 * @return a list of the {@link ClassPathEntry}'s that contain the specified type
	 */
	@Override
	public List<ClassPathEntry> getClassPathEntries(String type) {
		return getClassPathEntries(classPathEntries, type);
	}


	@Override
	public String toString() {
		StringBuilder strB = new StringBuilder();
		strB.append(file);
		strB.append(": [\n");
		StringJoin.Objects.join(classPathEntries, "\n", strB);
		strB.append("]\n");
		return strB.toString();
	}


	/**
	 * @param type the type of imports to parse (i.e. 'lib', 'con', 'src', 'output')
	 * @return a list of the {@link ClassPathEntry}'s that contain the specified type
	 */
	public static final List<ClassPathEntry> getClassPathEntries(List<ClassPathEntry> entries, String type) {
		List<ClassPathEntry> cpe = new ArrayList<>();
		for(ClassPathEntry entry : entries) {
			if(entry.kind != null && (type == null || entry.kind.contains(type))) {
				cpe.add(entry);
			}
		}
		return cpe;
	}


	public static final EclipseClasspathFile readXml(File srcFile, XmlInput in) throws IOException, XMLStreamException {
		List<ClassPathEntry> cpEntries = new ArrayList<>();

		DataElement elem = in.readStartBlock(ENTRY_KEY);
		elem = in.peekNext();
		while(elem != null && !(elem.isEndBlock() && elem.getName() == ENTRY_KEY)) {
			ClassPathEntry entry = new ClassPathEntry();
			entry.readXML(in);

			cpEntries.add(entry);
			elem = in.peekNext();
		}
		
		//in.readEndBlock();
		return new EclipseClasspathFile(srcFile, cpEntries);
	}

}
