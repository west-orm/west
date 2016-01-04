package com.bucuoa.west.orm.core.converter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.bucuoa.west.orm.core.uitls.AnnoationUtil;

/**
 * sql生成类
 * @author jake
 * 把对象直接映射成sql
 */
public class MysqlSQLGenerater {

	/**
	 * 生成insert SQL
	 * 用反射技术把对象直接转换成SQL
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public static <T> String insertSql(T t) throws Exception {

		Class<? extends Object> class1 = t.getClass();
		
		String tableName = AnnoationUtil.getTablename(class1);

		StringBuffer sql = new StringBuffer();
		StringBuffer param = new StringBuffer();
		StringBuffer values = new StringBuffer();
		
		sql.append("insert into ").append(tableName).append(" (");

		try {
			
			Field[] fields = class1.getDeclaredFields();
			for (Field field : fields) {
				
				String key = field.getName();
				if(key.equals("serialVersionUID") || key.equals("isSerialVersionUID"))
				{
					continue;
				}
				
				boolean tran = field.isAnnotationPresent(Transient.class);
				if(tran)
				{
					continue;
				}
				
				//id生成策略
				
//				boolean isId = field.isAnnotationPresent(Id.class);
//				boolean isGeneratedValue = field.isAnnotationPresent(GeneratedValue.class);
//				if(isId && isGeneratedValue)
//				{
//					Id ida = (Id) field.getAnnotation(Id.class);
//					GeneratedValue generatedValue = (GeneratedValue) field.getAnnotation(GeneratedValue.class);
//					generatedValue.strategy();
//					generatedValue.generator();
//					
//				}
		
				PropertyDescriptor pd = new PropertyDescriptor(key, class1);
				Method getMethod = pd.getReadMethod();
				Object value = getMethod.invoke(t);
				
				if (value == null) // 属性的值为null equals比较会抛异常
				{
					continue;
				}
				
				Column coumn = (Column) field.getAnnotation(Column.class);

				if (!field.isAnnotationPresent(Id.class)) {

					param.append(coumn.name()).append(",");

					if (value instanceof Integer || value instanceof Double || value instanceof Long) {
						values.append("").append(value).append(",");
					} else if (value instanceof Date) {
						values.append("'")
								.append(((Date) value).toLocaleString())
								.append("',");
					} else {
						values.append("'").append(value).append("',");
					}
				}
			}

			String paramk = param.toString().substring(0,param.toString().lastIndexOf(","));
			String valuek = values.toString().substring(0,values.toString().lastIndexOf(","));
			
			sql.append(paramk).append(") values ( ").append(valuek).append(" )");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql.toString();

	}
	
	public static <T> String selectSql(Class<T> class1, String[] filedk,
			String[] wheres, Object[] valuek,int pageNo,int pageSize)
			throws Exception {

		String tableName = AnnoationUtil.getTablename(class1);

		StringBuffer sql = new StringBuffer();

		sql.append("select * from ").append(tableName);
		if(filedk != null && filedk.length > 0)
		{
			sql.append(" where ");
		
			for (int i = 0; i < wheres.length; i++) {
				
				String where = wheres[i];
				String filed = filedk[i];
				Object value = valuek[i];
				
				if (i != 0) {
					sql.append(" and ");
				}
				sql.append(" ").append(filed).append(where).append(value);
			}
		}
		int pstart = (pageNo - 1) * pageSize;
		int pend = pageSize;
		sql.append(" limit ").append(pstart).append(",").append(pend);

		return sql.toString();
	}
	
	public static <T> String selectSql(Class<T> class1, String[] filedk,
			String[] wheres, Object[] valuek)
			throws Exception {

		String tableName = AnnoationUtil.getTablename(class1);

		StringBuffer sql = new StringBuffer();

		sql.append("select * from ").append(tableName);
		if(filedk != null && filedk.length > 0)
		{
			sql.append(" where ");
			
			for (int i = 0; i < wheres.length; i++) {
				String where = wheres[i];
				String filed = filedk[i];
				Object value = valuek[i];
				
				if (i != 0) {
					sql.append(" and ");
				}

				sql.append(" ").append(filed).append(where).append(value);
			}
		}

		return sql.toString();
	}
	
	public static <T> String selectSql(Class<T> class1, String[] filedk,
			String[] wheres, Object[] valuek,String[] orderby,String[] patterns,int pageNo, int pageSize)
			throws Exception {

		String tableName = AnnoationUtil.getTablename(class1);

		StringBuffer sql = new StringBuffer();

		sql.append("select * from ").append(tableName);
		if(filedk != null && filedk.length > 0)
		{
			sql.append(" where ");
			for (int i = 0; i < wheres.length; i++) {
				String where = wheres[i];
				String filed = filedk[i];
				Object value = valuek[i];
				if (i != 0) {
					sql.append(" and ");
				}

					sql.append(" ").append(filed).append(where).append(value);
			}
		}
		
		if(orderby !=null && orderby.length > 0)
		{
			sql.append(" order by ");
			for(int i =0 ;i < orderby.length; i ++)
			{
				sql.append(orderby[i] + " " + patterns[i]);
				if (i < orderby.length - 1) {
					sql.append(", ");
				}
			}
		}
		
		int pstart = (pageNo - 1) * pageSize;
		int pend = pageSize;
		sql.append(" limit ").append(pstart).append(",").append(pend);

		return sql.toString();
	}

	public static <T> String updateSql(T t) throws Exception {

		Class<? extends Object> class1 = t.getClass();
	
		String tableName = AnnoationUtil.getTablename(class1);

		StringBuffer sql = new StringBuffer();
		StringBuffer sets = new StringBuffer();
		
		sql.append("update ").append(tableName).append(" set ");
		
		Object id = "";
		
		try {
			Field[] fields = class1.getDeclaredFields();
			
			for (Field field : fields) {
				String key = field.getName();

				PropertyDescriptor pd = new PropertyDescriptor(key, class1);
				Method getMethod = pd.getReadMethod();
				Object value = getMethod.invoke(t);

				Column coumn = (Column) field.getAnnotation(Column.class);

				if (value == null || value.equals("")) // 属性的值为null equals比较会抛异常
				{
					continue;
				}

				if (!field.isAnnotationPresent(Id.class)) {

					sets.append(coumn.name()).append("=");

					if (value instanceof Integer) {
						sets.append("").append(value).append(",");
						
					} else if (value instanceof Date) {
						sets.append("'")
								.append(((Date) value).toLocaleString())
								.append("',");
					} else {
						sets.append("'").append(value).append("',");
					}
				} else {
					id = value;
				}
			}

			if (Integer.valueOf(id + "") == 0) { //id=0的时候 新建该对象
				return insertSql(t);
			}

			String paramk = sets.toString().substring(0,sets.toString().lastIndexOf(","));

			sql.append(paramk).append(" where id=" + id);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sql.toString();
	}

}
