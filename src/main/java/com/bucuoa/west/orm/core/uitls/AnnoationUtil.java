package com.bucuoa.west.orm.core.uitls;

import javax.persistence.Table;

public class AnnoationUtil {

	public static String getTablename(Class<?> clazz) {
		String tablename = null;
		if (clazz.isAnnotationPresent(Table.class))
		{
			
			Table myAnnotation = (Table) clazz.getAnnotation(Table.class);
			tablename = myAnnotation.name();
		
		}
		
		return tablename;
	}
}
