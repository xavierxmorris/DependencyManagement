package twg2.dependency.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.TransformerException;

import lombok.val;

import org.junit.Assert;
import org.junit.Test;

import twg2.dependency.eclipseProject.EclipseClasspathDoc;

public class ClasspathReplaceTest {

	@Test
	public void addDependencyTest() throws UnsupportedEncodingException, TransformerException {
		val jcollInterfacesCp = ClasspathExampleFiles.getJCollectionUtil("no", ClasspathExampleFiles.getJCollectionInterfacesEntry());

		val jcollInterfacesCpPlain = ClasspathExampleFiles.getJCollectionUtil("no");
		val doc = EclipseClasspathDoc.fromXml(new File(""), new ByteArrayInputStream(jcollInterfacesCpPlain.getBytes("utf-8")));

		doc.addClassPathEntry(ClasspathExampleFiles.getJCollectionInterfacesEntry());

		val output = new ByteArrayOutputStream();
		doc.toXml(output);
		val resStr = new String(output.toByteArray(), "utf-8");

		Assert.assertEquals(jcollInterfacesCp.trim(), resStr.trim());
	}

}
