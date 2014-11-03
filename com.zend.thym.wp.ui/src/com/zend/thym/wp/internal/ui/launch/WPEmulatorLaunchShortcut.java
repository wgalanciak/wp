package com.zend.thym.wp.internal.ui.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.thym.ui.launch.HybridProjectLaunchShortcut;

import com.zend.thym.wp.core.vstudio.WPLaunchConstants;

public class WPEmulatorLaunchShortcut extends HybridProjectLaunchShortcut {

	protected boolean validateBuildToolsReady() throws CoreException {
		// TODO Implement tools validation
		return true;
	}

	protected String getLaunchConfigurationTypeID() {
		return WPLaunchConstants.ID_LAUNCH_CONFIG_TYPE;
	}

	protected String getLaunchConfigurationNamePrefix(IProject project) {
		// TODO Auto-generated method stub
		return project.getName() + " (Windows Phone 8 Emulator)";
	}

}
