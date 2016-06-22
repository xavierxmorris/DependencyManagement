package twg2.dependency.eclipseProject;

import java.io.File;
import java.util.List;

public interface EclipseClasspathEntries {

	public File getFile();

	public List<ClassPathEntry> getLibClassPathEntries();

	/**
	 * @param type the type of imports to parse (i.e. 'lib', 'con', 'src', 'output')
	 * @return a list of the {@link ClassPathEntry}'s that contain the specified type
	 */
	public List<ClassPathEntry> getClassPathEntries(String type);

}