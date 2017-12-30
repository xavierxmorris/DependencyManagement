package twg2.dependency.eclipseProject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import lombok.val;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author TeamworkGuy2
 * @since 2016-2-4
 */
public class NodeUtil {

	public static final String getAttr(NamedNodeMap attrs, String attrName) {
		val attr = attrs != null ? attrs.getNamedItem(attrName) : null;
		return attr != null ? attr.getNodeValue() : null;
	}


	public static final List<Node> filter(NodeList list, Function<Node, Boolean> filter) {
		Object obj = new Object();
		return mapFilter(list, null, obj, (n) -> filter.apply(n) ? n : obj);
	}


	public static final <T> List<T> map(NodeList list, Function<Node, ? extends T> mapper) {
		return mapFilter(list, (List<T>)null, new Object(), mapper);
	}


	@SuppressWarnings("unchecked")
	public static final <T> List<T> mapFilter(NodeList list, List<T> dstList, Object skipVal, Function<Node, ? extends Object> mapper) {
		val res = dstList != null ? dstList : new ArrayList<T>();
		int len = list.getLength();
		for(int i = 0; i < len; i++) {
			val v = mapper.apply(list.item(i));
			if(v != skipVal) {
				res.add((T)v);
			}
		}
		return res;
	}


	public static final <T> List<T> map(NamedNodeMap srcMap, Function<Node, T> mapper) {
		return map(srcMap, null, mapper);
	}


	public static final <T> List<T> map(NamedNodeMap srcMap, List<T> dstList, Function<Node, T> mapper) {
		val res = dstList != null ? dstList : new ArrayList<T>();
		int len = srcMap.getLength();
		for(int i = 0; i < len; i++) {
			res.add(mapper.apply(srcMap.item(i)));
		}
		return res;
	}


	public static final void insertAfter(Node parent, List<Node> nodes, int prevSiblingIdx, Node newNode) {
		if(prevSiblingIdx > nodes.size()) { throw new IndexOutOfBoundsException(prevSiblingIdx + " of [0, " + nodes.size() + "]"); }
		nodes.add(prevSiblingIdx, newNode);

		if(prevSiblingIdx >= nodes.size() - 1) {
			parent.appendChild(newNode);
		}
		else {
			val nextNode = nodes.get(prevSiblingIdx + 1);
			parent.insertBefore(newNode, nextNode);
		}
	}

}
