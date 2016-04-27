package com.bucuoa.west.orm.extend;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface BaseDao<T, PK extends Serializable> {

	public Object saveEntity(T entity) throws Exception;

	public void updateEntity(T entity) throws Exception;

	public T findEntityById(PK id) throws Exception;

	public T findEntityBy(String filed, String value) throws Exception;

	public T findEntityBy(String[] filed, Object[] value) throws Exception;

	public List<T> select(Class<T> class1, String[] filedk, String[] wheres,
			Object[] valuek, int pageNo, int pageSize) throws Exception;

	public List<T> select(Class<T> class1, String[] filedk, String[] wheres,
			Object[] valuek, String[] orderby, String[] patterns, int pageNo,
			int pageSize) throws Exception;

	public int selectCount(String sql, Object... param) throws Exception;

	public int getEntityCount(String sql) throws Exception;

	public int getEntityCount(String[] keyName, String[] operate,
			Object[] keyValue, Object... ext) throws Exception;

	public boolean deleteEntityById(PK id) throws Exception;

	public boolean deleteEntityByCondition(Class<T> clazz, String condition)
			throws Exception;

	public List<T> findEntityList(String[] keyName, String[] operate,
			Object[] keyValue, String[] orderField, String[] pattern,
			int pageSize, int pageNo, Object... ext) throws Exception;

	public List<T> findEntityList(String[] keyName, String[] operate,
			Object[] keyValue, String[] orderField, String[] pattern)
			throws Exception;

	public List<T> findEntityList(String[] keyName, String[] operate,
			Object[] keyValue, int pageSize, int pageNo) throws Exception;

	public List<T> findEntityList(String[] keyName, String[] operate,
			Object[] keyValue) throws Exception;

	public List<T> findEntityList(String[] column, String[] keyName,
			String[] operate, Object[] keyValue, String[] orderField,
			String[] pattern) throws Exception;

	public List<T> findEntityList(String[] column, String[] keyName,
			String[] operate, Object[] keyValue, int pageSize, int pageNo)
			throws Exception;

	public List<T> findEntityList(String[] column, String[] keyName,
			String[] operate, Object[] keyValue) throws Exception;

	public List<T> findEntityList(String[] column, String[] keyName,
			String[] operate, Object[] keyValue, String[] orderByField,
			String[] pattern, int pageSize, int pageNo, Object... ext)
			throws Exception;

	public List<Map<String, String>> queryListMap(String sql);

	public <T> List<T> queryListBean(String sql, Class<T> clazz);

}
