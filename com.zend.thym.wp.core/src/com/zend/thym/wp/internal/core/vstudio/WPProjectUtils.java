package com.zend.thym.wp.internal.core.vstudio;

import java.io.File;

public final class WPProjectUtils {
	
	public static final String CSPROJ_EXTENSION= ".csproj";
	
	public static File getPlatformWWWDirectory(File projectRoot){
		return new File(projectRoot, "www");
	}

}
