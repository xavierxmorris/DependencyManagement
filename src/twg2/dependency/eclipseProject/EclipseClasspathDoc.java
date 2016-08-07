package twg2.dependency.eclipseProject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import lombok.val;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import twg2.collections.dataStructures.SortedList;
import twg2.text.stringUtils.StringCheck;

/**
 * @author TeamworkGuy2
 * @since 2016-2-3
 */
public class EclipseClasspathDoc implements EclipseClasspathEntries {
	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

	File file;
	Document doc;
	List<ClassPathEntry> classPathEntries = new ArrayList<>();


	public EclipseClasspathDoc(File file, Document doc, List<ClassPathEntry> classPathEntries) {
		this.file = file;
		this.doc = doc;
		this.classPathEntries = classPathEntries;
		Collections.sort(this.classPathEntries);
	}


	@Override
	public File getFile() {
		return file;
	}


	@Override
	public List<ClassPathEntry> getLibClassPathEntries() {
		return getClassPathEntries("lib");
	}


	/**
	 * @param type the type of imports to parse (i.e. 'lib', 'con', 'src', 'output')
	 * @return a list of the {@link ClassPathEntry}'s that contain the specified type
	 */
	@Override
	public List<ClassPathEntry> getClassPathEntries(String type) {
		return EclipseClasspathFile.getClassPathEntries(classPathEntries, type);
	}


	public void addClassPathEntry(ClassPathEntry entry) {
		int insertPoint = SortedList.addItem(this.classPathEntries, entry);
		Node cps = this.doc.getElementsByTagName("classpath").item(0);

		Element entryNode = this.doc.createElement("classpathentry");
		entryNode.setAttribute("kind", entry.getKind());
		entryNode.setAttribute("path", entry.getPath());

		if(!StringCheck.isNullOrEmpty(entry.getSourcePath())) { entryNode.setAttribute("sourcepath", entry.getSourcePath()); }
		val siblings = NodeUtil.filter(cps.getChildNodes(), (n) -> n.getNodeType() == Node.ELEMENT_NODE);
		val indentationText = this.doc.createTextNode(ClassPathEntry.inbetweenElementText);
		NodeUtil.insertAfter(cps, siblings, insertPoint, entryNode);
		NodeUtil.insertAfter(cps, siblings, insertPoint + 1, indentationText);
	}


	public void removeClassPathEntry(ClassPathEntry entry) {
		this.classPathEntries.remove(entry);
	}


	public void saveXmlDom(OutputStream out) throws TransformerException {
		val writeFactory = TransformerFactory.newInstance();
		val writer = writeFactory.newTransformer();
		writer.setOutputProperty(OutputKeys.INDENT, "yes");
		val src = new DOMSource(this.doc);
		val dst = new StreamResult(out);
		writer.transform(src, dst);
	}


	public static final EclipseClasspathDoc loadXmlDom(File srcFile, InputStream in) {
		try {
			val docBldr = docBuilderFactory.newDocumentBuilder();
			val doc = docBldr.parse(in);
			NodeList cps = doc.getElementsByTagName("classpathentry");
			List<ClassPathEntry> cpEntries = new ArrayList<>();

			for(int i = 0, size = cps.getLength(); i < size; i++) {
				val cp = cps.item(i);
				val cpAttrs = cp.getAttributes();
				val kind = NodeUtil.getAttr(cpAttrs, "kind");
				val path = NodeUtil.getAttr(cpAttrs, "path");
				val srcPath = NodeUtil.getAttr(cpAttrs, "sourcepath");

				cpEntries.add(new ClassPathEntry(kind, path, srcPath));
			}
			return new EclipseClasspathDoc(srcFile, doc, cpEntries);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch(SAXException | ParserConfigurationException se) {
			throw new RuntimeException(se);
		}
	}

}
