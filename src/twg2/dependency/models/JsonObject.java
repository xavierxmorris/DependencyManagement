package twg2.dependency.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import twg2.io.json.Json;
import twg2.io.json.JsonInst;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

/**
 * @author TeamworkGuy2
 * @since 2016-08-11
 */
public interface JsonObject<T extends JsonObject<T>> {


	public T fromJson(JsonNode tree);


	public void toJson(JsonInst in, Appendable dst);


	@Override
	public String toString();


	public default T fromJsonFile(String fileName) {
		try {
			FileInputStream in = new FileInputStream(new File(fileName));
			JsonNode tree = Json.getDefaultInst().getObjectMapper().readTree(in);
			return fromJson(tree);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}


	public default void toJsonFile(String fileName) {
		try {
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(new File(fileName)), Charset.forName("UTF-8"));
			this.toJson(Json.getDefaultInst(), out);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}


	public static String getString(Map<String, JsonNode> props, String name) {
		JsonNode node = props.get(name);
		if(!node.isTextual()) {
			throw new IllegalArgumentException("expected text node, '" + name + "' is a " + node.getNodeType() + " node");
		}
		props.remove(name);
		return node.asText();
	}


	public static String getStringOptional(Map<String, JsonNode> props, String name, String defaultValue) {
		JsonNode node = props.get(name);
		if(node == null) {
			return defaultValue;
		}
		if(!node.isTextual()) {
			throw new IllegalArgumentException("expected text node, '" + name + "' is a " + node.getNodeType() + " node");
		}
		props.remove(name);
		return node.asText();
	}


	public static List<String> getStringList(Map<String, JsonNode> props, String name) {
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


	public static Map<String, String> getStringMap(Map<String, JsonNode> props, String name) {
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


	public static Map<String, JsonNode> getMap(Map<String, JsonNode> props, String name) {
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
