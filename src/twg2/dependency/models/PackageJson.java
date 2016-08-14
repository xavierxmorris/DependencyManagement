package twg2.dependency.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import twg2.collections.builder.MapBuilder;
import twg2.io.json.JsonInst;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.zafarkhaja.semver.Version;

/**
 * @author TeamworkGuy2
 * @since 2016-1-27
 */
public class PackageJson implements DependencyInfo, JsonObject<PackageJson> {
	private @Getter String version;
	private @Getter Version versionExpr;
	private @Getter String name;
	private @Getter String description;
	private @Getter String homepage;
	private @Getter Map<String, JsonNode> author;
	private JsonNode license;
	private @Getter List<String> keywords;
	private @Getter Map<String, String> repository;
	private @Getter Map<String, String> dependencies;
	private @Getter Map<String, String> devDependencies;
	private @Getter Map<String, JsonNode> props;

	@Override public boolean isLibrary() { return false; }

	@Override public boolean isPackage() { return true; }


	@Override
	public String getMain() {
		return this.getPropString("main");
	}


	public String getLicense() {
		if(license != null) {
			JsonNode type;
			if(license.isTextual()) {
				return license.asText();
			}
			else if((type = license.get("type")) != null) {
				if(!type.isTextual()) {
					throw new IllegalArgumentException("expected text node, '" + name + "' is a " + type.getNodeType() + " node");
				}
				return type.asText();
			}
		}
		return null;
	}


	public String getNameVersion() {
		return this.getName() + "@" + this.getVersion();
	}


	@Override
	public String getPropString(String name) {
		return JsonObject.getString(this.props, name);
	}


	@Override
	public String getPropStringOptional(String name, String defaultValue) {
		return JsonObject.getStringOptional(this.props, name, defaultValue);
	}


	@Override
	public PackageJson fromJson(JsonNode tree) {
		PackageJson pkgInfo = new PackageJson();
		Map<String, JsonNode> props = MapBuilder.mutable(tree.fields());

		pkgInfo.version = JsonObject.getString(props, "version");
		pkgInfo.versionExpr = Version.valueOf(pkgInfo.version);
		pkgInfo.name = JsonObject.getString(props, "name");
		pkgInfo.description = JsonObject.getString(props, "description");
		pkgInfo.homepage = JsonObject.getString(props, "homepage");
		pkgInfo.author = JsonObject.getMap(props, "name");
		pkgInfo.license = props.get("license");
		pkgInfo.keywords = JsonObject.getStringList(props, "keywords");
		pkgInfo.repository = JsonObject.getStringMap(props, "repository");
		pkgInfo.dependencies = JsonObject.getStringMap(props, "dependencies");
		pkgInfo.devDependencies = JsonObject.getStringMap(props, "devDependencies");
		pkgInfo.props = props;

		return pkgInfo;
	}


	@Override
	public void toJson(JsonInst in, Appendable dst) {
		PackageJson pkgInfo = this;
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("version", pkgInfo.version);
		map.put("name", pkgInfo.name);
		map.put("description", pkgInfo.description);
		map.put("homepage", pkgInfo.homepage);
		map.put("author", pkgInfo.author);
		map.put("license", pkgInfo.license);
		map.put("keywords", pkgInfo.keywords);
		map.put("repository", pkgInfo.repository);
		map.put("dependencies", pkgInfo.dependencies);
		map.put("devDependencies", pkgInfo.devDependencies);
		map.putAll(props);

		in.stringify(map, dst);
	}


	@Override
	public String toString() {
		return this.getNameVersion();
	}

}
