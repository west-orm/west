 package com.bucuoa.west.orm.core.config;

import java.io.File;

public class SystemInfoCache {
	private static SystemInfo systemInfo;
	SystemInfoUtils systemInfoUtils = new SystemInfoUtils();
	
	public	SystemInfoCache()
	{
		if(systemInfo == null)
		{
			systemInfo = new SystemInfo();
			
			systemInfo.setPath(systemInfoUtils.getProperty("name"));
			systemInfo.setRoot(systemInfoUtils.getConfigRoot());
			systemInfo.setModule(systemInfoUtils.getConfigRoot()+File.separatorChar+systemInfoUtils.getProperty("name"));
		}
	}
	
	public String getPath()
	{
		return systemInfo.getPath();
	}
	
	public String getRoot()
	{
		return systemInfo.getRoot();
	}
	
	public String getModule()
	{
		return systemInfo.getModule();
	}
}
