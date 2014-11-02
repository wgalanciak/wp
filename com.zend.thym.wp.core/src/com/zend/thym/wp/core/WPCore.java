package com.zend.thym.wp.core;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class WPCore implements BundleActivator {

	public static final String WP_LAUNCH_ID= "com.zend.thym.wp.core.WPLaunchConfigurationType";
	
	public static final String PLUGIN_ID = "com.zend.thym.wp.core";

	private static BundleContext context;

	private static ILog logger;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		WPCore.context = bundleContext;
		logger = Platform.getLog(getContext().getBundle());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		WPCore.context = null;
	}

	public static void log(int status, String message, Throwable throwable) {
		logger.log(new Status(status, PLUGIN_ID, message, throwable));
	}

}
