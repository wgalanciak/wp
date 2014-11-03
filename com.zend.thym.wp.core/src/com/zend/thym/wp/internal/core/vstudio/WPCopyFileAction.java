package com.zend.thym.wp.internal.core.vstudio;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.thym.core.plugin.actions.CopyFileAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WPCopyFileAction extends CopyFileAction {

	private File rootFolder;

	public WPCopyFileAction(File source, File target, File rootFolder) {
		super(source, target);
		this.rootFolder = rootFolder;
	}

	@Override
	public void install() throws CoreException {
		super.install();
		File csprojFile = WPProjectUtils.getCsrojFile(rootFolder);
		if (csprojFile != null) {
			Document doc = WPProjectUtils.readXML(csprojFile);
			addCompileInclude(doc);
			WPProjectUtils.writeXML(csprojFile, doc);
		}
	}

	private void addCompileInclude(Document doc) {
		Element root = doc.getDocumentElement();
		Element itemGroup = doc.createElement("ItemGroup"); //$NON-NLS-1$
		Element compileInclude = doc.createElement("Compile"); //$NON-NLS-1$
		compileInclude.setAttribute("Include", getRelativePath(target)); //$NON-NLS-1$
		itemGroup.appendChild(compileInclude);
		root.appendChild(itemGroup);
	}

	private String getRelativePath(File target) {
		IPath targetPath = new Path(target.getAbsolutePath());
		IPath rootPath = new Path(rootFolder.getAbsolutePath());
		targetPath = targetPath.makeRelativeTo(rootPath);
		return targetPath.toString().replace('/', '\\');
	}

}
