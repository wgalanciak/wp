package com.zend.thym.wp.internal.core.vstudio;

import java.io.File;

import org.eclipse.thym.core.platform.AbstractPluginInstallationActionsFactory;
import org.eclipse.thym.core.platform.IPluginInstallationAction;
import org.eclipse.thym.core.platform.PlatformConstants;
import org.eclipse.thym.core.plugin.actions.CopyFileAction;
import org.eclipse.thym.core.plugin.actions.CreateFileAction;
import org.eclipse.thym.core.plugin.actions.JSModuleAction;

public class WPPluginInstallationActionsFactory extends
		AbstractPluginInstallationActionsFactory {

	public WPPluginInstallationActionsFactory() {
	}

	@Override
	public IPluginInstallationAction getSourceFileAction(String src,
			String targetDir, String framework, String pluginId,
			String compilerFlags) {
		File source = new File(getPluginDirectory(), src);
		StringBuilder targetPath = calculateTargetPath("Plugins", targetDir,
				pluginId, source);
		File target = new File(getProjectDirectory(), targetPath.toString());
		return new WPCopyFileAction(source, target, getProjectDirectory());
	}

	@Override
	public IPluginInstallationAction getResourceFileAction(String src,
			String target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPluginInstallationAction getHeaderFileAction(String src,
			String targetDir, String pluginId) {
		throw new UnsupportedOperationException(
				"Not implemented for Windows Phone 8");
	}

	@Override
	public IPluginInstallationAction getAssetAction(String src, String target) {
		File source = new File(getPluginDirectory(), src);
		File targetDir = new File(
				WPProjectUtils.getPlatformWWWDirectory(getProjectDirectory()),
				target);
		return new CopyFileAction(source, targetDir);
	}

	@Override
	public IPluginInstallationAction getConfigFileAction(String target,
			String parent, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPluginInstallationAction getLibFileAction(String src, String arch) {
		throw new UnsupportedOperationException(
				"Not implemented for Windows Phone 8");
	}

	@Override
	public IPluginInstallationAction getFrameworkAction(String src,
			String weak, String pluginId, String custom, String type,
			String parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPluginInstallationAction getJSModuleAction(String src,
			String pluginId, String jsModuleName) {
		File sourceFile = new File(getPluginDirectory(), src);
		File targetFile = new File(
				WPProjectUtils.getPlatformWWWDirectory(getProjectDirectory()),
				PlatformConstants.DIR_PLUGINS + File.separator + pluginId
						+ File.separator + src);
		return new JSModuleAction(sourceFile, targetFile, jsModuleName);
	}

	@Override
	public IPluginInstallationAction getCreatePluginJSAction(String content) {
		File pluginJs = new File(
				WPProjectUtils.getPlatformWWWDirectory(getProjectDirectory()),
				PlatformConstants.FILE_JS_CORDOVA_PLUGIN);
		return new CreateFileAction(content, pluginJs);
	}

	private StringBuilder calculateTargetPath(String groupDir,
			String targetDir, String pluginId, File source) {
		StringBuilder targetPath = new StringBuilder();
		if (groupDir != null) {
			targetPath.append(groupDir).append(File.separator);
		}
		if (pluginId != null) {
			targetPath.append(pluginId).append(File.separator);
		}
		if (targetDir != null) {
			targetPath.append(targetDir).append(File.separator);
		}
		targetPath.append(source.getName());
		return targetPath;
	}

}
