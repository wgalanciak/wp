package com.zend.thym.wp.internal.core.vstudio;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.thym.core.HybridProject;
import org.eclipse.thym.core.internal.util.ExternalProcessUtility;
import org.eclipse.thym.core.internal.util.TextDetectingStreamListener;
import org.eclipse.thym.core.platform.AbstractNativeBinaryBuildDelegate;

import com.ice.jni.registry.NoSuchKeyException;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;
import com.zend.thym.wp.core.WPCore;
import com.zend.thym.wp.internal.core.Version;

public class MSBuild extends AbstractNativeBinaryBuildDelegate {

	private static final String NODE_64_KEY = "Wow6432Node"; //$NON-NLS-1$
	private static final String INSTALL_ROOT = "InstallRoot"; //$NON-NLS-1$
	private static final String DOT_NET = ".NETFramework"; //$NON-NLS-1$
	private static final String MICROSOFT_KEY = "Microsoft"; //$NON-NLS-1$
	private static final String SOFTWARE_KEY = "SOFTWARE"; //$NON-NLS-1$

	private ILaunchConfiguration launchConfiguration;

	public void buildLibraryProject(File projectLocation,
			IProgressMonitor monitor) throws CoreException {
		doBuildProject(projectLocation, monitor);
		setBuildArtifact(new File(getBuildDir(projectLocation),
				"WPCordovaClassLib.dll"));
		if (!getBuildArtifact().exists()) {
			throw new CoreException(new Status(IStatus.ERROR, WPCore.PLUGIN_ID,
					"msbuild has failed: build artifact does not exist"));
		}
	}

	@Override
	public void buildNow(IProgressMonitor monitor) throws CoreException {
		if (monitor.isCanceled()) {
			return;
		}
		try {
			monitor.beginTask("Build Cordova project for Windows Phone 8", 10);
			// TODO: use extension point to create the generator.
			WPProjectGenerator creator = new WPProjectGenerator(getProject(),
					null, "wp8");
			SubProgressMonitor generateMonitor = new SubProgressMonitor(
					monitor, 1);
			File vstudioProjectDir = creator.generateNow(generateMonitor);

			monitor.worked(4);
			if (monitor.isCanceled()) {
				return;
			}
			doBuildProject(vstudioProjectDir, generateMonitor);
			HybridProject hybridProject = HybridProject.getHybridProject(this
					.getProject());
			if (hybridProject == null) {
				throw new CoreException(new Status(IStatus.ERROR,
						WPCore.PLUGIN_ID,
						"Not a hybrid mobile project, can not generate files"));
			}
			String name = hybridProject.getBuildArtifactAppName();
			if (isRelease()) {
				setBuildArtifact(new File(getBuildDir(vstudioProjectDir),
						"CordovaAppProj_Release_AnyCPU.xap"));
			} else {
				setBuildArtifact(new File(getBuildDir(vstudioProjectDir),
						"CordovaAppProj_Debug_AnyCPU.xap"));
			}
		} finally {
			monitor.done();
		}
	}

	public void setLaunchConfiguration(ILaunchConfiguration launchConfiguration) {
		this.launchConfiguration = launchConfiguration;
	}

	public ILaunchConfiguration getLaunchConfiguration() {
		return launchConfiguration;
	}

	/**
	 * Returns the actual folder where the build artifacts can be found.
	 * 
	 * @param vstudioProjectFolder
	 *            - Visual Studio project's root folder
	 * @return folder with the build artifacts
	 */
	private File getBuildDir(File vstudioProjectFolder) {
		File binFolder = new File(vstudioProjectFolder, WPProjectUtils.BIN);
		return new File(binFolder, isRelease() ? WPProjectUtils.RELEASE
				: WPProjectUtils.DEBUG);
	}

	private void doBuildProject(File projectLocation, IProgressMonitor monitor)
			throws CoreException {
		if (monitor.isCanceled()) {
			return;
		}
		try {
			monitor.beginTask("Build Cordova project for Windows Phone 8", 10);
			String msBuild = getMSBuildPath();
			if (msBuild != null) {
				File csprojFile = WPProjectUtils.getCsrojFile(projectLocation);
				// on this stage it cannot be null
				Assert.isNotNull(csprojFile);
				StringBuilder cmdString = new StringBuilder(msBuild);
				cmdString.append(" ");
				if (isRelease()) {
					cmdString.append("/p:Configuration=Release");
					cmdString.append(" ");
				}
				cmdString.append(csprojFile.getAbsolutePath());

				ExternalProcessUtility processUtility = new ExternalProcessUtility();
				if (monitor.isCanceled()) {
					return;
				}
				monitor.worked(1);
				TextDetectingStreamListener listener = new TextDetectingStreamListener(
						"Build succeeded.");
				processUtility.execSync(cmdString.toString(), projectLocation,
						listener, listener, monitor, null,
						getLaunchConfiguration());
				if (!listener.isTextDetected()) {
					throw new CoreException(new Status(IStatus.ERROR,
							WPCore.PLUGIN_ID, "msbuild has failed"));
				}
			}
		} finally {
			monitor.done();
		}
	}

	private String getMSBuildPath() {
		String installationRoot = getInstallationRoot();
		if (installationRoot != null) {
			File installationFile = new File(installationRoot);
			if (installationFile.exists()) {
				File[] versionFiles = installationFile
						.listFiles(new FileFilter() {

							@Override
							public boolean accept(File pathname) {
								return pathname.getName().startsWith("v");
							}
						});
				File highestVersion = null;
				for (File file : versionFiles) {
					if (highestVersion == null) {
						highestVersion = file;
					} else {
						Version current = Version.byName(highestVersion
								.getName());
						Version newOne = Version.byName(file.getName());
						if (newOne.compareTo(current) > 0) {
							highestVersion = file;
						}
					}
				}
				if (highestVersion != null) {
					return new File(highestVersion.getAbsolutePath(),
							"MSBuild.exe").getAbsolutePath();
				}
			}
		}
		return null;
	}

	private String getInstallationRoot() {
		RegistryKey msKey = getMicrosoftKey();
		if (msKey != null) {
			RegistryKey dotNetKey = null;
			try {
				dotNetKey = msKey.openSubKey(DOT_NET);
			} catch (NoSuchKeyException e) {
				// TODO
			} catch (RegistryException e) {
				// TODO
				// WPCore.log(status, message, throwable);
			}
			if (dotNetKey != null) {
				try {
					return dotNetKey.getStringValue(INSTALL_ROOT);
				} catch (NoSuchKeyException e) {
					// TODO
					// WPCore.log(status, message, throwable);
				} catch (RegistryException e) {
					// TODO
					// WPCore.log(status, message, throwable);
				}
			}
		}
		return null;
	}

	private RegistryKey getMicrosoftKey() {
		RegistryKey msKey = null;
		try {
			msKey = Registry.HKEY_LOCAL_MACHINE.openSubKey(SOFTWARE_KEY)
					.openSubKey(NODE_64_KEY).openSubKey(MICROSOFT_KEY);
		} catch (NoSuchKeyException e) {
			// do not log and try to get 32bit one
		} catch (RegistryException e) {
			// TODO
			// WPCore.log(status, message, throwable);
		}
		return msKey;
	}

}
