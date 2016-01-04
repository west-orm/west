package com.bucuoa.west.orm.core.converter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import org.apache.commons.beanutils.BeanUtils;

/**
 * 对象生成器
 * @author jake
 *
 */
public class ObjectConverter {
	public static <T> List<T> getListBean(Class<T> t, ResultSet rst)
			throws SQLException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		
		List<T> list = new ArrayList<T>();
		
		while (rst.next()) {
			
			T item = t.newInstance();
			Field[] fields = item.getClass().getDeclaredFields();
			
			for (Field field : fields) {
				
				String filedname = field.getName();
				if ("serialVersionUID".equalsIgnoreCase(filedname)) {
					continue;
				}
				
				 Column coumn = (Column) field.getAnnotation(Column.class);
				 String name = coumn.name();

				try {
					Class<?> typex = field.getType();
					
					if(typex.getName().equals("java.util.Date"))
					{
						Date tempDate = rst.getTimestamp(name);
						if(tempDate != null)
						{
							BeanUtils.setProperty(item, filedname, new Date());
						}
					}else
					{
						Object tempStr = rst.getString(name);
						BeanUtils.setProperty(item, filedname, tempStr);
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			list.add(item);
		}
		
		return list;
	}
	
	public static List<Map<String,String>> getListMap(ResultSet rst, List<String> columns) throws SQLException {
		
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		while (rst.next()) {
			
			Map<String,String> map = new HashMap<String,String>();
			for (String column : columns) {

				try {
					String temp = rst.getString(column);
					if(temp !=null)
					{
						map.put(column, temp);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
			
			list.add(map);
			
		}
		return list;
	}
	
	public static List<String> getColumns(ResultSet rst)
			throws SQLException {
		
		List<String> columns =  new ArrayList<String>();
		
		ResultSetMetaData metaData = rst.getMetaData();
		int numCols = metaData.getColumnCount(); 
		
		for(int i = 1; i <= numCols; i ++)
		{
			String columnName = metaData.getColumnName(i);
			columns.add(columnName);
		}
		
		return columns;
	}
	
	public static <T> T getOneBean(Class<T> t, ResultSet rst)
			throws SQLException, IllegalAccessException,
			InvocationTargetException {
		
		T obj = null;
		
		if (rst.next()) {
			
			try {
				obj = t.newInstance();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}
			
			Field[] fields = obj.getClass().getDeclaredFields();
			
			for (Field field : fields) {
				
				String filedname = field.getName();
				if ("serialVersionUID".equalsIgnoreCase(filedname)) {
					continue;
				}
				
				 Column coumn = (Column) field.getAnnotation(Column.class);
				 String name = coumn.name();

				try {
					Class<?> typex = field.getType();
					
					if(typex.getName().equals("java.util.Date"))
					{
						Date tempDate = rst.getTimestamp(name);
						if(tempDate != null)
						{
							BeanUtils.setProperty(obj, filedname, new Date());
						}
					}else
					{
						Object tempStr = null;
						try {
							tempStr = rst.getString(name);
							BeanUtils.setProperty(obj, filedname, tempStr);
						} catch (Exception e) {
							System.out.println(t.getCanonicalName()+"--> miss "+name);
						}
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
		return obj;
	}
}
