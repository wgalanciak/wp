package com.zend.thym.wp.internal.ui.launch;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.thym.ui.launch.HybridProjectLaunchShortcut;

import com.zend.thym.wp.core.vstudio.MSBuild;
import com.zend.thym.wp.core.vstudio.WPLaunchConstants;
import com.zend.thym.wp.ui.WPThymUI;

public class WPEmulatorLaunchShortcut extends HybridProjectLaunchShortcut {

	protected boolean validateBuildToolsReady() throws CoreException {
		MSBuild msbuild = new MSBuild();
		String msBuildPath = msbuild.getMSBuildPath();
		if (msBuildPath != null) {
			File msBuildFile = new File(msBuildPath);
			if (!msBuildFile.exists()) {
				throw new CoreException(
						getErrorStatus("Could not find MSBuild executable. Please install Windows Phone SDK and try again."));
			}
		} else {
			throw new CoreException(
					getErrorStatus("Could not find MSBuild executable. Please install Windows Phone SDK and try again."));
		}
		return true;
	}

	protected String getLaunchConfigurationTypeID() {
		return WPLaunchConstants.ID_LAUNCH_CONFIG_TYPE;
	}

	protected String getLaunchConfigurationNamePrefix(IProject project) {
		return project.getName() + " (Windows Phone 8 Emulator)";
	}

	private IStatus getErrorStatus(String message) {
		return new Status(IStatus.ERROR, WPThymUI.PLUGIN_ID, message);
	}

}
