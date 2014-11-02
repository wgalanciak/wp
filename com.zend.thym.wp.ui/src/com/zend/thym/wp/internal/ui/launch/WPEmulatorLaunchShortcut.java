package com.zend.thym.wp.internal.ui.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.thym.ui.launch.HybridProjectLaunchShortcut;

public class WPEmulatorLaunchShortcut extends HybridProjectLaunchShortcut {

	public static final String WP_LAUNCH_ID = "com.zend.thym.wp.core.WPLaunchConfigurationType";

	@Override
	protected boolean validateBuildToolsReady() throws CoreException {
		// TODO Implement tools validation
		return true;
	}

	@Override
	protected String getLaunchConfigurationTypeID() {
		return WP_LAUNCH_ID;
	}

	@Override
	protected String getLaunchConfigurationNamePrefix(IProject project) {
		// TODO Auto-generated method stub
		return project.getName() + " (Windows Phone 8 Emulator)";
	}

}
