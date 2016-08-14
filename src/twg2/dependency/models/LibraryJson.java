package twg2.dependency.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.val;
import twg2.collections.builder.MapBuilder;
import twg2.io.json.JsonInst;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.zafarkhaja.semver.Version;

/**
 * @author TeamworkGuy2
 * @since 2016-08-11
 */
public class LibraryJson implements DependencyInfo, JsonObject<LibraryJson> {
	private @Getter String name;
	private @Getter String main;
	private @Getter String sourceDir;
	private @Getter String version;
	private @Getter Version versionExpr;
	private @Getter String date;
	private @Getter List<String> from;
	private @Getter String notes;
	private @Getter Map<String, JsonNode> props;

	@Override public boolean isLibrary() { return true; }

	@Override public boolean isPackage() { return false; }


	@Override
	public String getPropString(String name) {
		return JsonObject.getString(this.props, name);
	}


	@Override
	public String getPropStringOptional(String name, String defaultValue) {
		return JsonObject.getStringOptional(this.props, name, defaultValue);
	}


	@Override
	public void toJson(JsonInst in, Appendable dst) {
		val libInfo = this;
		val map = new LinkedHashMap<>();
		map.put("name", libInfo.name);
		if(libInfo.main != null) { map.put("main", libInfo.main); }
		if(libInfo.sourceDir != null) { map.put("sourceDir", libInfo.sourceDir); }
		map.put("version", libInfo.version);
		map.put("date", libInfo.date);
		map.put("from", libInfo.from);
		if(libInfo.notes != null) { map.put("notes", libInfo.notes); }

		in.stringify(map, dst);
	}


	@Override
	public LibraryJson fromJson(JsonNode tree) {
		LibraryJson libInfo = new LibraryJson();
		Map<String, JsonNode> props = MapBuilder.mutable(tree.fields());

		libInfo.name = JsonObject.getString(props, "name");
		libInfo.main = JsonObject.getStringOptional(props, "main", null);
		libInfo.sourceDir = JsonObject.getStringOptional(props, "sourceDir", null);
		libInfo.version = JsonObject.getString(props, "version");
		libInfo.versionExpr = Version.valueOf(libInfo.version);
		libInfo.date = JsonObject.getString(props, "date");
		libInfo.from = JsonObject.getStringList(props, "from");
		libInfo.notes = JsonObject.getStringOptional(props, "notes", null);
		libInfo.props = props;

		return libInfo;
	}


	@Override
	public String toString() {
		return this.getName() + "@" + this.getVersion();
	}

}
