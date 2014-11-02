package com.zend.thym.wp.internal.core;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.thym.core.HybridProjectLaunchConfigConstants;

import com.zend.thym.wp.internal.core.vstudio.MSBuild;


public class WPLaunchDelegate implements ILaunchConfigurationDelegate2 {

	private File buildArtifact;

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		return null;
	}

	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		MSBuild build = new MSBuild();
		build.setLaunchConfiguration(configuration);
		build.init(getProject(configuration), null);
		build.buildNow(monitor);
		buildArtifact = build.getBuildArtifact();
		return false;
	}

	@Override
	public boolean finalLaunchCheck(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		monitor.done();
		return true;
	}

	@Override
	public boolean preLaunchCheck(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		return true;
	}
	
	private IProject getProject(ILaunchConfiguration configuration){
		try{
			String projectName = configuration.getAttribute(HybridProjectLaunchConfigConstants.ATTR_BUILD_SCOPE, (String)null);
			if(projectName != null ){
				 return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			}
		}catch(CoreException e){
			return null;
		}
		return null;
	}

}
