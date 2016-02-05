package jarDependencies;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import twg2.collections.builder.MapBuilder;
import twg2.io.json.Json;
import twg2.io.json.JsonInst;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

/**
 * @author TeamworkGuy2
 * @since 2016-1-27
 */
public class PackageJson {
	private @Getter String version;
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


	public void write(String fileName) {
		try {
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(new File(fileName)), Charset.forName("UTF-8"));
			this.write(Json.getDefaultInst(), out);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
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


	public String getPropString(String name) {
		return PackageJson.toString(this.props, name);
	}


	public void write(JsonInst in, Appendable dst) {
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


	public static PackageJson read(String fileName) {
		try {
			FileInputStream in = new FileInputStream(new File(fileName));
			return read(Json.getDefaultInst(), in);
		} catch (FileNotFoundException e) {
			throw new UncheckedIOException(e);
		}
	}


	public static PackageJson read(JsonInst in, InputStream src) {
		PackageJson pkgInfo = new PackageJson();
		try {
			JsonNode tree = in.getObjectMapper().readTree(src);
			Map<String, JsonNode> props = MapBuilder.mutable(tree.fields());

			pkgInfo.version = toString(props, "version");
			pkgInfo.name = toString(props, "name");
			pkgInfo.description = toString(props, "description");
			pkgInfo.homepage = toString(props, "homepage");
			pkgInfo.author = toMap(props, "name");
			pkgInfo.license = props.get("license");
			pkgInfo.keywords = toStringList(props, "keywords");
			pkgInfo.repository = toStringMap(props, "repository");
			pkgInfo.dependencies = toStringMap(props, "dependencies");
			pkgInfo.devDependencies = toStringMap(props, "devDependencies");
			pkgInfo.props = props;

		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		return pkgInfo;
	}


	private static final String toString(Map<String, JsonNode> props, String name) {
		JsonNode node = props.get(name);
		if(!node.isTextual()) {
			throw new IllegalArgumentException("expected text node, '" + name + "' is a " + node.getNodeType() + " node");
		}
		props.remove(name);
		return node.asText();
	}


	private static final List<String> toStringList(Map<String, JsonNode> props, String name) {
		JsonNode node = props.get(name);
		if(node != null) {
			if(node.getNodeType() != JsonNodeType.ARRAY) {
				throw new IllegalArgumentException("expected array node, '" + name + "' is a " + node.getNodeType() + " node");
			}

			List<String> list = new ArrayList<>();
			Iterator<JsonNode> fields = node.elements();
			while(fields.hasNext()) {
				JsonNode field = fields.next();
				if(!field.isTextual()) {
					throw new IllegalArgumentException("array element is not text, expected array of text values");
				}
				list.add(field.asText());
			}

			props.remove(name);
			return list;
		}
		return Collections.emptyList();
	}


	private static final Map<String, String> toStringMap(Map<String, JsonNode> props, String name) {
		JsonNode node = props.get(name);
		if(node != null) {
			if(node.getNodeType() != JsonNodeType.OBJECT) {
				throw new IllegalArgumentException("expected object node, '" + name + "' is a " + node.getNodeType() + " node");
			}

			Map<String, String> map = new HashMap<>();
			Iterator<Entry<String, JsonNode>> fields = node.fields();
			while(fields.hasNext()) {
				Entry<String, JsonNode> field = fields.next();
				if(!field.getValue().isTextual()) {
					throw new IllegalStateException("field '" + field.getKey() + "' of object '" + name + "' is not text, expected map of text values");
				}
				map.put(field.getKey(), field.getValue().asText());
			}

			props.remove(name);
			return map;
		}
		return Collections.emptyMap();
	}


	private static final Map<String, JsonNode> toMap(Map<String, JsonNode> props, String name) {
		JsonNode node = props.get(name);
		if(node != null) {
			if(node.getNodeType() != JsonNodeType.OBJECT) {
				throw new IllegalArgumentException("expected object node, '" + name + "' is a " + node.getNodeType() + " node");
			}

			Map<String, JsonNode> map = new HashMap<>();
			Iterator<Entry<String, JsonNode>> fields = node.fields();
			while(fields.hasNext()) {
				Entry<String, JsonNode> field = fields.next();
				map.put(field.getKey(), field.getValue());
			}

			props.remove(name);
			return map;
		}
		return Collections.emptyMap();
	}

}
