package com.bucuoa.west.orm.core.uitls;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.Table;

import com.bucuoa.west.orm.core.annotation.ShardKey;
import com.bucuoa.west.orm.core.annotation.ShardTable;
import com.bucuoa.west.orm.core.shard.ShardKeyInfo;

public class AnnoationUtil {

	public static <T> String getTablename2(T t) {
		String tablename = null;
		Class<?> clazz = t.getClass();
		
		if (clazz.isAnnotationPresent(Table.class))
		{
			
			Table myAnnotation = (Table) clazz.getAnnotation(Table.class);
			tablename = myAnnotation.name();
		
		}
		
		if (clazz.isAnnotationPresent(ShardTable.class))
		{
			
			ShardTable shardTable = (ShardTable) clazz.getAnnotation(ShardTable.class);
			String policy = shardTable.policy();
			int nums = shardTable.nums();
			
			Object shardKey = getShardKey(t);
			
			int hashCode = Math.abs(shardKey.hashCode());
			int k = hashCode % nums;
			System.out.println(hashCode+"---%2--->"+k);
			tablename+="_"+k;
		}
		
		return tablename;
	}
	
	public static <T> String getTablename(Class<?> clazz) {
		String tablename = null;
//		Class<?> clazz = t.getClass();
		
		if (clazz.isAnnotationPresent(Table.class))
		{
			
			Table myAnnotation = (Table) clazz.getAnnotation(Table.class);
			tablename = myAnnotation.name();
		
		}
		
//		if (clazz.isAnnotationPresent(ShardTable.class))
//		{
//			
//			ShardTable shardTable = (ShardTable) clazz.getAnnotation(ShardTable.class);
//			String policy = shardTable.policy();
//			int nums = shardTable.nums();
//			
//			Object shardKey = getShardKey(t);
//			
//			int k = shardKey.hashCode() % nums;
//			
//			tablename+="_"+k;
//		}
		
		return tablename;
	}
	
	public static <T> Object getShardKey(T t) {
		
		Class<?> clazz = t.getClass();
		Object obbb = new Object();

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			
			String key = field.getName();
			if(key.equals("serialVersionUID") || key.equals("isSerialVersionUID"))
			{
				continue;
			}
			
			boolean iskey = field.isAnnotationPresent(ShardKey.class);
			if(iskey)
			{
				try {
					PropertyDescriptor pd = new PropertyDescriptor(key, clazz);
					Method getMethod = pd.getReadMethod();
					obbb = getMethod.invoke(t);
					
					if (obbb == null) // 属性的值为null equals比较会抛异常
					{
						continue;
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (IntrospectionException e) {
					e.printStackTrace();
				}
				
				break;
			}
		
		}
		return obbb;
	}
}
