package twg2.dependency.models;

import lombok.EqualsAndHashCode;

import com.github.zafarkhaja.semver.Version;

/** Uniquely identify a module
 * @author TeamworkGuy2
 * @since 2016-2-3
 */
@EqualsAndHashCode
public class NameVersion {
	public final String name;
	public final Version version;


	public NameVersion(String name, Version version) {
		this.name = name;
		this.version = version;
	}


	@Override
	public String toString() {
		return name + "@" + version;
	}

}
