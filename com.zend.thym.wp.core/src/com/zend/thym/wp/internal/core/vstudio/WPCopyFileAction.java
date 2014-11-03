package com.zend.thym.wp.internal.core.vstudio;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.thym.core.plugin.actions.CopyFileAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class WPCopyFileAction extends CopyFileAction {

	private static final String DOT_CS = ".cs";
	private static final String DOT_XAML = ".xaml";
	private File rootFolder;
	private Document document;

	public WPCopyFileAction(File source, File target, File rootFolder) {
		super(source, target);
		this.rootFolder = rootFolder;
	}

	@Override
	public void install() throws CoreException {
		super.install();
		File csprojFile = WPProjectUtils.getCsrojFile(rootFolder);
		if (csprojFile != null) {
			setDocument(WPProjectUtils.readXML(csprojFile));
			Element root = getDocument().getDocumentElement();
			Element itemGroup = getItemGroup();
			String name = target.getName();
			if (name.endsWith(DOT_CS)) {
				itemGroup.appendChild(getCompileInclude());
				if (name.endsWith(DOT_XAML + DOT_CS)) {
					itemGroup.appendChild(getPageInclude());
				}
			} else if (!name.endsWith(DOT_XAML)) {
				itemGroup.appendChild(getContentInclude());
			}
			root.appendChild(itemGroup);
			WPProjectUtils.writeXML(csprojFile, getDocument());
		}
	}

	private Document getDocument() {
		return document;
	}

	private void setDocument(Document document) {
		this.document = document;
	}

	private Element getItemGroup() {
		return getDocument().createElement("ItemGroup"); //$NON-NLS-1$
	}

	private Node getPageInclude() {
		Element pageInclude = getDocument().createElement("Page"); //$NON-NLS-1$
		String relativePath = getRelativePath(target);
		relativePath = relativePath.substring(0, relativePath.indexOf(DOT_CS));
		pageInclude.setAttribute("Include", relativePath); //$NON-NLS-1$
		Element subType = getDocument().createElement("SubType");
		subType.setTextContent("Designer");
		pageInclude.appendChild(subType);
		Element generator = getDocument().createElement("Generator");
		generator.setTextContent("MSBuild:Compile");
		pageInclude.appendChild(generator);
		return pageInclude;
	}

	private Element getCompileInclude() {
		Element compileInclude = getDocument().createElement("Compile"); //$NON-NLS-1$
		String relativePath = getRelativePath(target);
		compileInclude.setAttribute("Include", relativePath); //$NON-NLS-1$
		if (relativePath.endsWith(DOT_XAML + DOT_CS)) {
			compileInclude.appendChild(getDependetUpon(relativePath));
		}
		return compileInclude;
	}

	private Element getContentInclude() {
		Element contentInclude = getDocument().createElement("Content"); //$NON-NLS-1$
		String relativePath = getRelativePath(target);
		contentInclude.setAttribute("Include", relativePath); //$NON-NLS-1$
		return contentInclude;
	}

	private Element getDependetUpon(String fullPath) {
		Element dependUpon = getDocument().createElement("DependentUpon");
		IPath path = new Path(fullPath);
		String fileName = path.lastSegment();
		fileName = fileName.substring(0, fileName.indexOf(DOT_CS));
		dependUpon.setTextContent(fileName);
		return dependUpon;
	}

	private String getRelativePath(File target) {
		IPath targetPath = new Path(target.getAbsolutePath());
		IPath rootPath = new Path(rootFolder.getAbsolutePath());
		targetPath = targetPath.makeRelativeTo(rootPath);
		return targetPath.toString().replace('/', '\\');
	}

}
