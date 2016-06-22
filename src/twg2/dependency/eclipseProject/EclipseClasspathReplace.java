package twg2.dependency.eclipseProject;

import lombok.val;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import twg2.collections.builder.ListBuilder;

/**
 * @author TeamworkGuy2
 * @since 2016-2-4
 */
public class EclipseClasspathReplace {

	public static final void removeLibs(Document cpFile) {
		val cpsRoot = cpFile.getElementsByTagName("classpath").item(0);

		Node child = cpsRoot.getLastChild();
		while(child != null) {
			Node tmp = child.getPreviousSibling();
			val kind = NodeUtil.getAttr(child.getAttributes(), "kind");
			if(ClassPathEntry.isLib(kind)) {
				cpsRoot.removeChild(child);
				if(tmp != null && tmp.getNodeType() == Node.TEXT_NODE) {
					child = tmp;
					tmp = child.getPreviousSibling();
					cpsRoot.removeChild(child);
				}
			}
			child = tmp;
		}
	}


	public static final void addLibs(Document cpFile, Iterable<ClassPathEntry> cpEntries) {
		val cpsRoot = cpFile.getElementsByTagName("classpath").item(0);
		val cpList = ListBuilder.mutable(cpEntries);
		cpList.sort((a, b) -> a.path.compareTo(b.path));

		int i = 0;
		for(val cp : cpEntries) {
			val cpElem = createClassPathEntryElement(cpFile, cp);
			val indent = i == 0 ? cpFile.createTextNode("\t") : cpFile.createTextNode("\n\t");
			cpsRoot.appendChild(indent);
			cpsRoot.appendChild(cpElem);
			i++;
		}
	}


	private static final Element createClassPathEntryElement(Document doc, ClassPathEntry cpEntry) {
		val cpElem = doc.createElement("classpathentry");

		if(cpEntry.kind != null) {
			val attr = doc.createAttribute("kind");
			attr.setNodeValue(cpEntry.kind);
			cpElem.getAttributes().setNamedItem(attr);
		}

		if(cpEntry.path != null) {
			val attr = doc.createAttribute("path");
			attr.setNodeValue(cpEntry.path);
			cpElem.getAttributes().setNamedItem(attr);
		}

		if(cpEntry.sourcePath != null) {
			val attr = doc.createAttribute("sourcepath");
			attr.setNodeValue(cpEntry.sourcePath);
			cpElem.getAttributes().setNamedItem(attr);
		}

		return cpElem;
	}

}
