package com.zend.thym.wp.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.thym.core.HybridCore;
import org.eclipse.thym.core.engine.HybridMobileLibraryResolver;
import org.eclipse.thym.core.internal.util.FileUtils;

public class WPLibraryResolver extends HybridMobileLibraryResolver {

	public static final String VERSION = "VERSION";
	public static final String DEFAULTS_XML = "defaults.xml";
	public static final String WP_APP_MANIFEST_XML = "WMAppManifest.xml";
	public static final String APP_XAML = "App.xaml";
	public static final String APP_XAML_CS = "App.xaml.cs";
	public static final String MAIN_PAGE_XAML = "MainPage.xaml";
	public static final String MAIN_PAGE_XAML_CS = "MainPage.xaml.cs";
	public static final String DEFAULT_APP_NAME = "CordovaWP8AppProj";
	public static final String DEFAULT_APP_NAME_CSPROJ = DEFAULT_APP_NAME + ".csproj";
	public static final String DEFAULT_SLN_NAME = "CordovaWP8Solution.sln";
	
	private HashMap<IPath, URL> files = new HashMap<IPath, URL>();

	@Override
	public URL getTemplateFile(IPath destination) {
		if (files.isEmpty())
			initFiles();
		Assert.isNotNull(destination);
		Assert.isTrue(!destination.isAbsolute());
		return files.get(destination);
	}

	@Override
	public IStatus isLibraryConsistent() {
		if (version == null) {
			return new Status(
					IStatus.ERROR,
					HybridCore.PLUGIN_ID,
					"Library for Windows Phone 8 platform is not compatible with this tool. VERSION file is missing.");
		}
		return libraryRoot.lastSegment().equals("wp8") ? Status.OK_STATUS
				: Status.CANCEL_STATUS;
	}

	@Override
	public void preCompile(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean needsPreCompilation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String detectVersion() {
		File versionFile = this.libraryRoot.append("VERSION").toFile();
		if (versionFile.exists()) {
			BufferedReader reader = null;
			try {
				try {
					reader = new BufferedReader(new FileReader(versionFile));
					String version = reader.readLine();
					if (version != null) {
						return version.trim();
					}
				} finally {
					if (reader != null)
						reader.close();
				}
			} catch (IOException e) {
				WPCore.log(IStatus.ERROR, "Can not detect version on library",
						e);
			}
		} else {
			WPCore.log(IStatus.ERROR, NLS.bind(
					"Can not detect version. VERSION file {0} is missing",
					versionFile.toString()), null);
		}
		return null;
	}

	private void initFiles() {
		IPath templatePrjRoot = libraryRoot.append("template");
		files.put(new Path(VAR_APP_NAME), getEngineFile(templatePrjRoot));
		files.put(new Path(VERSION), getEngineFile(libraryRoot.append(VERSION)));
		files.put(
				new Path(DEFAULTS_XML),
				getEngineFile(templatePrjRoot.append("cordova").append(
						DEFAULTS_XML)));
		files.put(new Path(WP_APP_MANIFEST_XML), getEngineFile(templatePrjRoot
				.append("Properties").append(WP_APP_MANIFEST_XML)));
		files.put(new Path(APP_XAML),
				getEngineFile(templatePrjRoot.append(APP_XAML)));
		files.put(new Path(APP_XAML_CS),
				getEngineFile(templatePrjRoot.append(APP_XAML_CS)));
		files.put(new Path(MAIN_PAGE_XAML),
				getEngineFile(templatePrjRoot.append(MAIN_PAGE_XAML)));
		files.put(new Path(MAIN_PAGE_XAML_CS),
				getEngineFile(templatePrjRoot.append(MAIN_PAGE_XAML_CS)));
		files.put(new Path(DEFAULT_APP_NAME_CSPROJ),
				getEngineFile(templatePrjRoot.append(DEFAULT_APP_NAME_CSPROJ)));
		files.put(new Path(DEFAULT_SLN_NAME),
				getEngineFile(templatePrjRoot.append(DEFAULT_SLN_NAME)));
	}

	private URL getEngineFile(IPath path) {
		File file = path.toFile();
		if (!file.exists()) {
			WPCore.log(
					IStatus.WARNING,
					NLS.bind("Missing Windows Phone 8 engine file {0}",
							file.toString()), null);
		}
		return FileUtils.toURL(file);
	}

}
