package eclipseProject;

import lombok.val;

import org.w3c.dom.NamedNodeMap;

/**
 * @author TeamworkGuy2
 * @since 2016-2-4
 */
public class NodeUtil {

	public static final String getAttr(NamedNodeMap attrs, String attrName) {
		val attr = attrs != null ? attrs.getNamedItem(attrName) : null;
		return attr != null ? attr.getNodeValue() : null;
	}

}
