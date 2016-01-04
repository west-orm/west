 package com.bucuoa.west.orm.core.config;


public class SystemInfo {
	
	private String path;
	private String root;
	private String module;
	
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String property = new SystemInfoUtils().getProperty("name");
		System.out.println(property);
		
	}
	
	public String getRoot() {
		return root;
	}


	public void setRoot(String root) {
		this.root = root;
	}


	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
