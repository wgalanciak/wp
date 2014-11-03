package com.zend.thym.wp.internal.core.vstudio;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public final class WPProjectUtils {

	public static final String CSPROJ_EXTENSION = ".csproj";
	public static final String BIN = "Bin";
	public static final String DEBUG = "Debug";
	public static final String RELEASE = "Release";

	public static File getPlatformWWWDirectory(File projectRoot) {
		return new File(projectRoot, "www");
	}

	public static File getCsrojFile(File rootFolder) {
		File[] csprojFiles = rootFolder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(WPProjectUtils.CSPROJ_EXTENSION);
			}
		});
		return csprojFiles != null && csprojFiles.length > 0 ? csprojFiles[0]
				: null;
	}

	/**
	 * Read XML file.
	 * 
	 * @param file
	 * @return
	 */
	public static Document readXML(File file) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(file.getAbsolutePath());
		} catch (ParserConfigurationException e) {
			// MobileCore.logError(e);
		} catch (SAXException e) {
			// MobileCore.logError(e);
		} catch (IOException e) {
			// MobileCore.logError(e);
		}
		return null;
	}

	/**
	 * Write XML file.
	 * 
	 * @param file
	 * @param doc
	 */
	public static void writeXML(File file, Document doc) {
		try {
			Result result = new StreamResult(file);
			Source source = new DOMSource(doc);
			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			xformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4"); //$NON-NLS-1$ //$NON-NLS-2$
			xformer.transform(source, result);
		} catch (TransformerFactoryConfigurationError e) {
			// MobileCore.logError(e);
		} catch (TransformerException e) {
			// MobileCore.logError(e);
		}
	}

}
