package com.bucuoa.west.orm.extend.spring;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bucuoa.west.orm.core.ExcetueManager;
import com.bucuoa.west.orm.core.uitls.AnnoationUtil;
import com.bucuoa.west.orm.core.SQLConverter;
/**
 * west和应用的接口类
 * 项目只需要继承本类就可以实现west提供的功能
 * 用户也可以自己实现本类
 * 只需要注入ExcetueManager即可
 * @author jake
 *
 * @param <T>
 * @param <PK>
 */
@Component
public class SpringEntityDaoBase<T, PK extends Serializable> {
	@Autowired
	private ExcetueManager excetueManager;

	private Class<T> classz;

	@SuppressWarnings("unchecked")
	protected SpringEntityDaoBase() {
		classz = (Class<T>) ((ParameterizedType) this.getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 * 保存实体对象
	 * 
	 * @param entity
	 *            实体对象
	 * @return 实体对象的id
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Object saveEntity(T entity) throws Exception {
		String insertSql = SQLConverter.insertSql(entity);
		
		long end2 = System.currentTimeMillis();
		long executeUpdateSql = (Long) excetueManager.executeUpdateSql(insertSql);
		long end3 = System.currentTimeMillis();
		
//		System.out.println("\t执行sql耗时 : " + (end3 - end2)+ "ms");
		
		return executeUpdateSql;
	}

	/**
	 * 属性没有值的不修改
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void updateEntity(T entity) throws Exception {
		String updateSql = SQLConverter.updateSql(entity);
		
		excetueManager.executeUpdateSql(updateSql);
	}

	/**
	 * 根据实体Id查询指定实体对象
	 * 
	 * @param id
	 *            实体对象的id
	 * @return 返回指定实体对象
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public T findEntityById(PK id) throws Exception {
		
			String tableName = AnnoationUtil.getTablename(classz);

			return (T) excetueManager.queryOne("select * from " + tableName	+ " where id=" + id, classz);
		 
	}
	
	public T findEntityBy(String filed,String value) throws Exception {
			String tableName = AnnoationUtil.getTablename(classz);

			return (T) excetueManager.queryOne("select * from " + tableName	+ " where "+filed+"=" + value, classz);
	
	}
	
	public T findEntityBy(String[] filed,Object[] value) throws Exception {
		String sql = "";
		try {
			String tableName = AnnoationUtil.getTablename(classz);
			String wherex = "";
			for(int i = 0; i < filed.length; i ++)
			{
				wherex += " and "+filed[i]+"="+value[i];
			}
			 sql = "select * from " + tableName
					+ " where 1=1  "+wherex;
			return (T) excetueManager.queryOne(sql, classz);
		} finally {
			
			try {
			} catch (Exception e) {
				 System.out.println("Send Log Error:["+sql +"]");
			}
		}
	}

	/**
	 * 多条件单实体组合查询
	 * 
	 * @param class1
	 * @param filedk
	 * @param wheres
	 * @param valuek
	 * @return
	 * @throws Exception
	 */
	public List<T> select(Class<T> class1, String[] filedk, String[] wheres,
			Object[] valuek, int pageNo, int pageSize) throws Exception {

		try {
			String selectSql = SQLConverter
					.selectSql(classz, filedk, wheres, valuek);

			return excetueManager.queryList(selectSql, class1);
		} finally {
		}
	}
	
	public List<T> select(Class<T> class1, String[] filedk, String[] wheres,
			Object[] valuek,String[] orderby, String[] patterns, int pageNo, int pageSize) throws Exception {

		
			String selectSql = "";
			if(orderby != null && orderby.length > 0)
			{
				 selectSql = SQLConverter.selectSql(classz, filedk, wheres, valuek,orderby,patterns,pageNo,  pageSize);
			}else
			{
				 selectSql = SQLConverter.selectSql(classz, filedk, wheres, valuek,pageNo,  pageSize);
			}

			return excetueManager.queryList(selectSql, class1);
		
	}

	/**
	 * 根据sql语句查询所有符合条件的实体个数（sql语句中必须包含通配符?,防止sql注入） 可以运用到关联查询。
	 * 
	 * @param sql
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int selectCount(String sql, Object... param) throws Exception {
		if (sql == null) {
			throw new IllegalArgumentException("The sql statement is null.");
		}

		return excetueManager.queryCount(sql);
	}

	/**
	 * 根据sql语句查询所有符合条件的实体数（没有使用防注入的方法）
	 * 
	 * @param condition
	 *            传入的sql。
	 * @return
	 * @throws Exception
	 */
	public int getEntityCount(String sql) throws Exception {

		if (sql == null) {
			throw new IllegalArgumentException("The sql statement is null.");
		}

		return excetueManager.queryCount(sql);
	}

	private Object[] getObjectList(Object... params) throws Exception {
		List<Object> objList = new ArrayList<Object>();
		for (Object param : params) {
			if (param instanceof Collection) {
				Collection<?> c = (Collection<?>) param;
				Iterator<?> r = c.iterator();
				while (r.hasNext()) {
					Object obj = r.next();
					objList.add(obj);
				}
			} else if (param instanceof Object[]) {
				Object[] objects = (Object[]) param;
				int i = 0;
				while (i < objects.length) {
					Object obj = objects[i++];
					objList.add(obj);
				}
			} else {
				objList.add(param);
			}
		}
		return objList.toArray();
	}

	/**
	 * 获得符合查询条件的总记录数 分页时使用
	 * 
	 * @param keyName
	 *            查询条件的字段名称数组
	 * @param operate
	 *            查询条件的操作符数组
	 * @param keyValue
	 *            查询条件的值的数组
	 * @return 总记录数
	 * @throws Exception
	 */
	public int getEntityCount(String[] keyName, String[] operate,
			Object[] keyValue, Object... ext) throws Exception {
		StringBuffer sql = new StringBuffer(""); // where条件子句
		this.processCondition(sql, keyName, operate, keyValue, ext); // 添加查询条件的sql片段

		String tableName = AnnoationUtil.getTablename(classz);

		StringBuffer querySql = new StringBuffer();
		querySql.append("select");
		querySql.append(" count(*) ");
		querySql.append(" from ");
		querySql.append(tableName);

		if (sql != null && sql.length() > 0) {
			querySql.append(" where ");
			querySql.append(" " + sql + " ");
		}

		int countNum = selectCount(querySql.toString(), keyValue);
		return countNum;
	}

	/**
	 * 根据id删除实体对象（物理删除）
	 * 
	 * @param id
	 * 实体对象的id
	 * @return 是否删除成功; true：删除成功; false：删除失败
	 * @throws Exception
	 */
	public boolean deleteEntityById(PK id) throws Exception {
		excetueManager.deleteById(classz, id);
		return true;
	}

	public boolean deleteEntityByCondition(Class<T> clazz, String condition)
			throws Exception {
		
		excetueManager.deleteById(classz, condition);
		
		return true;
	}

	public List<T> findEntityList(String[] keyName, String[] operate,
			Object[] keyValue, String[] orderField, String[] pattern,
			int pageSize, int pageNo, Object... ext) throws Exception {
		return findEntityList(null, keyName, operate, keyValue, orderField,
				pattern, pageSize, pageNo, ext);
	}

	public List<T> findEntityList(String[] keyName, String[] operate,
			Object[] keyValue, String[] orderField, String[] pattern)
			throws Exception {
		return findEntityList(keyName, operate, keyValue, orderField, pattern,
				1000, 1);
	}

	public List<T> findEntityList(String[] keyName, String[] operate,
			Object[] keyValue, int pageSize, int pageNo) throws Exception {
		return findEntityList(keyName, operate, keyValue, null, null, pageSize,
				pageNo);
	}

	public List<T> findEntityList(String[] keyName, String[] operate,
			Object[] keyValue) throws Exception {
		return findEntityList(keyName, operate, keyValue, null, null, 1000, 1);
	}

	public List<T> findEntityList(String[] column, String[] keyName,
			String[] operate, Object[] keyValue, String[] orderField,
			String[] pattern) throws Exception {
		return findEntityList(column, keyName, operate, keyValue, orderField,
				pattern, 1000, 1);
	}

	public List<T> findEntityList(String[] column, String[] keyName,
			String[] operate, Object[] keyValue, int pageSize, int pageNo)
			throws Exception {
		return findEntityList(column, keyName, operate, keyValue, null, null,
				pageSize, pageNo);
	}

	public List<T> findEntityList(String[] column, String[] keyName,
			String[] operate, Object[] keyValue) throws Exception {
		return findEntityList(column, keyName, operate, keyValue, null, null,
				1000, 1);
	}

	public List<T> findEntityList(String[] column, String[] keyName,
			String[] operate, Object[] keyValue, String[] orderByField,
			String[] pattern, int pageSize, int pageNo, Object... ext)
			throws Exception {

		StringBuffer sql = new StringBuffer(""); // where条件子句
		StringBuffer queryColumns = null; // 要查询哪些字段的名称
		String queryStr = "";
		if (null != column && column.length > 0) {
			queryColumns = new StringBuffer(" ");
			for (int i = 0, length = column.length; i < length; i++) {
				queryColumns.append(column[i]);
				queryColumns.append(", ");
			}
			queryStr = queryColumns.substring(0, queryColumns.length() - 2);
		} else {
			queryColumns = new StringBuffer(" * ");
			queryStr = queryColumns.toString();
		}

		// 添加查询条件的sql片段
		this.processCondition(sql, keyName, operate, keyValue, ext);

		String tableName = AnnoationUtil.getTablename(classz);

		StringBuffer querySql = new StringBuffer();
		querySql.append("select");
		querySql.append(queryStr);
		querySql.append(" from ");
		querySql.append(tableName);

		if (sql != null && sql.length() > 0) {
			querySql.append(" where ");
			querySql.append(" " + sql + " ");
		}

		if (null != orderByField && orderByField.length > 0 && null != pattern
				&& pattern.length > 0 && orderByField.length == pattern.length) {
			querySql.append(" order by ");
			for (int i = 0, length = orderByField.length; i < length; i++) {
				querySql.append(orderByField[i] + " " + pattern[i]);
				if (i < orderByField.length - 1) {
					querySql.append(", ");
				}
			}
		}
		
		querySql.append(" limit ?, ? ");

		Object[] param = new Object[keyValue.length + 2];
		System.arraycopy(keyValue, 0, param, 0, keyValue.length);
		param[keyValue.length] = (pageNo - 1) * pageSize;
		param[keyValue.length + 1] = pageSize;

		List<T> resultList = select(classz, keyName, operate, keyValue,orderByField, pattern,pageNo,
				pageSize);

		if (resultList == null) {
			resultList = new ArrayList<T>();
		}

		return resultList;
	}



	/**
	 * 处理查询条件
	 * 
	 * @param keyName
	 *            字段名称的数组
	 * @param operate
	 *            操作符的数组
	 * @param keyValue
	 *            字段值的数组
	 * @param ext
	 *            扩展参数：ext[0]or查询条件字段名称的集合
	 * @return 处理之后的sql片段，例如：(k1 = v1 or k1 = v2) and k2=v3
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected void processCondition(StringBuffer sql, String[] keyName,
			String[] operate, Object[] keyValue, Object... ext)
			throws Exception {

		// or查询条件的个数
		int orConditionCount = 0;
		// or查询条件字段名称的集合
		List<String> orConditionList = null;
		if (ext != null && ext.length > 0) {
			orConditionList = (List<String>) ext[0];
		}
		if (orConditionList == null || orConditionList.isEmpty()) {
			orConditionList = new ArrayList<String>();
		}

		for (int i = 0, length = keyName.length, lastIndex = operate.length - 1; i < length; i++) {
			// 是否是or查询条件，默认是false
			boolean isOrCondition = false;
			// 是否是最后一个操作连接符
			boolean notLast = i < lastIndex;
			if (orConditionList.contains(keyName[i])) {
				isOrCondition = true;
				orConditionCount++;
				if (orConditionCount == 1) {
					sql.append(" ( ");
				}
			}

			sql.append(keyName[i]).append(" ").append(operate[i]).append(" ");

			if ("in".equalsIgnoreCase(operate[i].trim())
					|| "not in".equalsIgnoreCase(operate[i].trim())) {
				sql.append(" ( ");
				if (keyValue[i] instanceof Collection<?>) {
					for (int j = 0, size = ((Collection<?>) keyValue[i]).size(), jEnding = size - 1; j < size; j++) {
						sql.append("?");
						if (j < jEnding) {
							sql.append(", ");
						}
					}
				} else {
					sql.append("?");
				}
				sql.append(" ) ");
			} else if (keyName[i].toLowerCase().indexOf("match(") >= 0) {
				sql.append("against(?)");
			} else if (!"".equals(operate[i].trim())) {
				sql.append("?");
			}

			// 如果不是最后一个操作符，需要拼接or或者是and关键字
			if (notLast) {
				if (isOrCondition) {
					// 如果是or查询条件并且不是最后一个or查询条件，则sql语句拼接or关键字
					if (orConditionCount < orConditionList.size()) {
						sql.append(" or ");
					}
					// 否则sql语句拼接and关键字
				} else {
					sql.append(" and ");
				}
			}

			// 如果是最后一个or查询条件，需要加上反括号
			if (isOrCondition && orConditionCount == orConditionList.size()) {
				sql.append(" ) ");
				if (notLast) {
					sql.append(" and ");
				}
			}
		}
		


	}

	public List<T> findEntityList(String columns, String condition,
			String orderBy) throws Exception {
		return null;
	}

	public List<T> findEntityList(String columns, String condition, int pageNo,
			int pageSize, String orderBy) throws Exception {
		return null;
	}

	public   List<Map<String, String>> queryListMap(String sql) 
	{
		List<Map<String, String>> queryListMap = excetueManager.queryListMap(sql);
		return  queryListMap;
	}
	
	public  <T> List<T> queryListBean(String sql,Class<T> clazz) 
	{
		List<T> list  = excetueManager.queryList(sql,clazz);
		return  list;
	}
}
