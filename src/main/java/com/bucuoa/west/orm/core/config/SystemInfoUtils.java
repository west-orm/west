 package com.bucuoa.west.orm.core.config;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class SystemInfoUtils {

	public static void main(String[] args) {
		System.out.println(OSinfo.getOSname());
		
		System.out.println(new SystemInfoUtils().getRoot2());
		System.out.println(new SystemInfoUtils().getProperty("name")+"\\");
	}

	public String getProperty(String name) {
		   Properties props = new Properties();
	        String file = "/META-INF/system.properties";
	      
	        URL fileURL = this.getClass().getClassLoader().getResource(file);
	        if (fileURL != null) {
	            try {
	                props.load(this.getClass().getResourceAsStream(file));
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return props.getProperty(name);
	    }
	
	public String getOSname()
	{
		return OSinfo.getOSname().toString();
	}
	
	public String getRoot2()
	{
		String os = OSinfo.getOSname().toString();
		if(os.toLowerCase().equals("windows"))
		{
			return "d:\\config\\wfr";
		}else
		{
			return "/opt/config/wfr";
		}
	}
	
	public String getConfigRoot()
	{
		String os = OSinfo.getOSname().toString();
		if(os.toLowerCase().equals("windows"))
		{
			return "d:\\config\\wfr";
		}else
		{
			return "/opt/config/wfr";
		}
	}
}
