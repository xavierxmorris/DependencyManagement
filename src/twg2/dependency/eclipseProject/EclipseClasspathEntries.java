package twg2.dependency.eclipseProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.val;

/**
 * @author TeamworkGuy2
 * @since 2015-5-25
 */
public interface EclipseClasspathEntries {

	public File getFile();


	/**
	 * @param type the type of imports to parse (i.e. 'lib', 'con', 'src', 'output')
	 * @return a list of the {@link ClassPathEntry}'s that contain the specified type
	 */
	public List<ClassPathEntry> getClassPathEntries(String type);


	@Override
	public String toString();


	public default List<ClassPathEntry> getLibClassPathEntries() {
		return getClassPathEntries("lib");
	}


	/**
	 * @param type the type of imports to parse (i.e. 'lib', 'con', 'src', 'output')
	 * @return a list of the {@link ClassPathEntry}'s that contain the specified type
	 */
	public static List<ClassPathEntry> getClassPathEntries(List<ClassPathEntry> entries, String type) {
		val dst = new ArrayList<ClassPathEntry>();
		for(val entry : entries) {
			if(entry.kind != null && (type == null || entry.kind.contains(type))) {
				dst.add(entry);
			}
		}
		return dst;
	}

}
