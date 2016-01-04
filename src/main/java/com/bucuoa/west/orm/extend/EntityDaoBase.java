package com.bucuoa.west.orm.extend;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bucuoa.west.orm.core.ExcetueManager;
import com.bucuoa.west.orm.core.uitls.AnnoationUtil;
import com.bucuoa.west.orm.core.SQLConverter;
/**
 * 非spring环境使用接口
 * @author jake
 *
 * @param <T>
 * @param <PK>
 */

public class EntityDaoBase<T, PK extends Serializable> {

	private ExcetueManager excetueManager = new ExcetueManager();

	private Class<T> classz;

	@SuppressWarnings("unchecked")
	protected EntityDaoBase() {
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
		long start2 = System.currentTimeMillis();
		String insertSql = SQLConverter.insertSql(entity);
		long end2 = System.currentTimeMillis();
		
		System.out.print("生成sql耗时 : " + (end2 - start2)+ "ms");
		
		long executeUpdateSql = (Long) excetueManager.executeUpdateSql(insertSql);

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
		
		System.out.println("updateSql===>"+updateSql);
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
		try {
			String tableName = AnnoationUtil.getTablename(classz);

			return (T) excetueManager.queryOne("select * from " + tableName
					+ " where id=" + id, classz);
		} finally {
			String sql = "select * from " + classz.getSimpleName();
			try {
			} catch (Exception e) {
				// logger.error("Send Log Error:["+sql +"]", e);
			}
		}
	}
	
	public T findEntityBy(String filed,String value) throws Exception {
		try {
			String tableName = AnnoationUtil.getTablename(classz);

			return (T) excetueManager.queryOne("select * from " + tableName
					+ " where "+filed+"=" + value, classz);
		} finally {
			String sql = "select * from " + classz.getSimpleName();
			try {
			} catch (Exception e) {
				// logger.error("Send Log Error:["+sql +"]", e);
			}
		}
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

		try {
			String selectSql = "";
			if(orderby != null && orderby.length > 0)
			{
				 selectSql = SQLConverter.selectSql(classz, filedk, wheres, valuek,orderby,patterns,pageNo,  pageSize);
			}else
			{
				 selectSql = SQLConverter.selectSql(classz, filedk, wheres, valuek,pageNo,  pageSize);
			}
			

			return excetueManager.queryList(selectSql, class1);
		} finally {
		}
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

	// /**
	// * 修改指定id的实体对象的属性
	// * @param id 实体对象的id
	// * @param valusMap 需要修改的实体对象的属性名称的映射
	// * @return 实体对象的Id
	// */
	// public PK updateEntityById(PK id, Map<String, Object> valuesMap) throws
	// Exception {
	// StringBuffer updateStatement = new StringBuffer();
	// boolean isFirst = true;
	// for (Iterator<String> it = valuesMap.keySet().iterator(); it.hasNext();)
	// {
	// String fieldName = it.next();
	// Object fieldValue = valuesMap.get(fieldName);
	// if (!isFirst) {
	// updateStatement.append(",");
	// }
	// updateStatement.append("`" + fieldName + "` = '" + fieldValue + "'");
	// isFirst = false;
	// }
	// SqlInjectUtils.filterSql(updateStatement.toString());
	// return id;
	// }

	/**
	 * 根据id删除实体对象（物理删除）
	 * 
	 * @param id
	 *            实体对象的id
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

	/*
	 * public List<T> findEntityListMap(String[] column, String[] keyName,
	 * String[] operate, Object[] keyValue, String[] orderByField, String[]
	 * pattern, int pageSize, int pageNo, Object... ext) throws Exception {
	 * 
	 * StringBuffer sql = new StringBuffer(""); // where条件子句 StringBuffer
	 * queryColumns = null; // 要查询哪些字段的名称 String queryStr = ""; if (null !=
	 * column && column.length > 0) { queryColumns = new StringBuffer(" "); for
	 * (int i = 0, length = column.length; i < length; i++) {
	 * queryColumns.append(column[i]); queryColumns.append(", "); } queryStr =
	 * queryColumns.substring(0, queryColumns.length() - 2); } else {
	 * queryColumns = new StringBuffer(" * "); queryStr =
	 * queryColumns.toString(); }
	 * 
	 * // 添加查询条件的sql片段 this.processCondition(sql, keyName, operate, keyValue,
	 * ext);
	 * 
	 * String tableName = getTableName();
	 * 
	 * StringBuffer querySql = new StringBuffer(); querySql.append("select");
	 * querySql.append(queryStr); querySql.append(" from ");
	 * querySql.append(tableName);
	 * 
	 * if (sql != null && sql.length() > 0) { querySql.append(" where ");
	 * querySql.append(" " + sql + " "); }
	 * 
	 * if (null != orderByField && orderByField.length > 0 && null != pattern &&
	 * pattern.length > 0 && orderByField.length == pattern.length) {
	 * querySql.append(" order by "); for (int i = 0, length =
	 * orderByField.length; i < length; i++) { querySql.append(orderByField[i] +
	 * " " + pattern[i]); if (i < orderByField.length - 1) {
	 * querySql.append(", "); } } }
	 * 
	 * querySql.append(" limit ?, ? ");
	 * 
	 * Object[] param = new Object[keyValue.length + 2];
	 * System.arraycopy(keyValue, 0, param, 0, keyValue.length);
	 * param[keyValue.length] = (pageNo - 1) * pageSize; param[keyValue.length +
	 * 1] = pageSize;
	 * 
	 * List<T> resultList = select(querySql.toString(), param);
	 * 
	 * if (resultList == null) { resultList = new ArrayList<T>(); }
	 * 
	 * return resultList; }
	 */

	/*
	 * public List<T> findEntityList21(String[] column, String[] keyName,
	 * String[] operate, Object[] keyValue, String[] orderByField, String[]
	 * pattern, int pageSize, int pageNo, Object... ext) throws Exception {
	 * 
	 * StringBuffer sql = new StringBuffer(""); // where条件子句 StringBuffer
	 * queryColumns = null; // 要查询哪些字段的名称 String queryStr = ""; if (null !=
	 * column && column.length > 0) { queryColumns = new StringBuffer(" "); for
	 * (int i = 0, length = column.length; i < length; i++) {
	 * queryColumns.append(column[i]); queryColumns.append(", "); } queryStr =
	 * queryColumns.substring(0, queryColumns.length() - 2); } else {
	 * queryColumns = new StringBuffer(" * "); queryStr =
	 * queryColumns.toString(); }
	 * 
	 * // 添加查询条件的sql片段 this.processCondition(sql, keyName, operate, keyValue,
	 * ext);
	 * 
	 * String tableName = getTableName();
	 * 
	 * StringBuffer querySql = new StringBuffer(); querySql.append("select");
	 * querySql.append(queryStr); querySql.append(" from ");
	 * querySql.append(tableName);
	 * 
	 * if (sql != null && sql.length() > 0) { querySql.append(" where ");
	 * querySql.append(" " + sql + " "); }
	 * 
	 * if (null != orderByField && orderByField.length > 0 && null != pattern &&
	 * pattern.length > 0 && orderByField.length == pattern.length) {
	 * querySql.append(" order by "); for (int i = 0, length =
	 * orderByField.length; i < length; i++) { querySql.append(orderByField[i] +
	 * " " + pattern[i]); if (i < orderByField.length - 1) {
	 * querySql.append(", "); } } }
	 * 
	 * querySql.append(" limit ?, ? ");
	 * 
	 * Object[] param = new Object[keyValue.length + 2];
	 * System.arraycopy(keyValue, 0, param, 0, keyValue.length);
	 * param[keyValue.length] = (pageNo - 1) * pageSize; param[keyValue.length +
	 * 1] = pageSize;
	 * 
	 * List<T> resultList = select(querySql.toString(), param);
	 * 
	 * if (resultList == null) { resultList = new ArrayList<T>(); }
	 * 
	 * return resultList; }
	 */

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

	/*
	 * private String getTableName() { if
	 * (classz.isAnnotationPresent(Table.class)) { Table table = (Table)
	 * classz.getAnnotation(Table.class); if
	 * (!table.name().equalsIgnoreCase("className")) { return table.name(); } }
	 * String name = classz.getName(); return
	 * name.substring(name.lastIndexOf(".") + 1); }
	 */

	/*
	 * public void execWithPara(String sql, final List<Object> values) { try {
	 * DBHelper.getDAOHelperWriter().execWithPara(sql, new
	 * IPreparedStatementHandler() {
	 * 
	 * public Object exec(PreparedStatement ps) throws SQLException { int index
	 * = 0;
	 * 
	 * if (values != null && values.size() > 0) { for (int i = 0, size =
	 * values.size(); i < size; i++) { Object value = values.get(i); if (value
	 * instanceof List) { List<?> list1 = (List<?>) value; for (Object obj :
	 * list1) { ps.setObject(++index, obj); } } else { ps.setObject(++index,
	 * value); } } }
	 * 
	 * int count = ps.executeUpdate(); return count; } }); } catch (Exception e)
	 * { // TODO Auto-generated catch block logger.error("根据数据源更新sql语句出现异常", e);
	 * } }
	 */
	/*
	 * @SuppressWarnings("unchecked") public <V> V execQueryWithPara(String sql,
	 * final List<Object> values, final IRowCallbackHandler handler) { try {
	 * return (V)DBHelper.getDAOHelperWriter().execWithPara(sql, new
	 * IPreparedStatementHandler() {
	 * 
	 * public Object exec(PreparedStatement ps) throws SQLException { int index
	 * = 0;
	 * 
	 * if (values != null && values.size() > 0) { for (int i = 0, size =
	 * values.size(); i < size; i++) { Object value = values.get(i); if (value
	 * instanceof Collection) { Collection<?> list1 = (Collection<?>) value; for
	 * (Object obj : list1) { ps.setObject(++index, obj); } } else {
	 * ps.setObject(++index, value); } } }
	 * 
	 * ResultSet rs = ps.executeQuery(); return handler.exec(rs); } }); } catch
	 * (Exception e) { // TODO Auto-generated catch block e.printStackTrace();
	 * logger.error("根据数据源更新sql语句出现异常", e); } return null; }
	 */

	public static void main(String[] args) {
		// ISubOrderDao subOrderDao = new SubOrderDaoImpl();
		//
		// Date afterBeginTime = new Date(DateUtil.addDays(new Date(),
		// 1).getTime() + 1000L);
		// Date afterEndTime = new Date(DateUtil.addDays(new Date(),
		// 10).getTime());
		//
		// String[] columnArr = new String[]{"id", "product_id", "user_id",
		// "version_id", "service_end_time"};
		// String[] keyNameArr = new String[]{"id", "service_end_time",
		// "service_end_time", "order_state"};
		// String[] operateArr = new String[]{"<>", ">=", "<=", "="};
		// Object[] keyValueArr = new Object[]{ProductData.CATE_G_ID,
		// afterBeginTime, afterEndTime, OrderState.FUWUZHONG};
		//
		// try{
		// IOrderDao orderDao=new OrderDaoImpl();
		// List<Object> param = new ArrayList<Object>();
		// param.add("0");
		// orderDao.selectCount("select count(*) from t_sub_order where delete_flag=?"
		// ,param);
		// }catch(Exception e){
		// e.printStackTrace();
		// }

	}

	public List<T> findEntityList(String columns, String condition,
			String orderBy) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<T> findEntityList(String columns, String condition, int pageNo,
			int pageSize, String orderBy) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 根据sql查询（sql语句中必须包含通配符?,防止sql注入）（多表查询）
	 * 
	 * @param sql
	 * @param param
	 * @return
	 * @throws Exception
	 */
	/*
	 * public Object execWithPara(String sql, IPreparedStatementHandler handler)
	 * throws Exception { if (sql == null) { throw new
	 * IllegalArgumentException("The sql statement is null."); } if
	 * (sql.indexOf("?") == -1) { throw new IllegalArgumentException(
	 * "The sql statement is Illegal. The sql statement must contain ?."); }
	 * 
	 * return DBHelper.getDAOHelperWriter().execWithPara(sql, handler); }
	 */

	/*
	 * public List<T> findEntityListByIndex(String indexName,String[] column,
	 * String[] keyName, String[] operate, Object[] keyValue, String[]
	 * orderByField, String[] pattern, int pageSize, int pageNo, Object... ext)
	 * throws Exception { StringBuffer sql = new StringBuffer(""); //where条件子句
	 * StringBuffer queryColumns = null; //要查询哪些字段的名称 String queryStr = ""; if
	 * (null != column && column.length > 0) { queryColumns = new
	 * StringBuffer(" "); for (int i = 0, length = column.length; i < length;
	 * i++) { queryColumns.append(column[i]); queryColumns.append(", "); }
	 * queryStr = queryColumns.substring(0, queryColumns.length() - 2); } else {
	 * queryColumns = new StringBuffer(" * "); queryStr =
	 * queryColumns.toString(); }
	 * 
	 * //添加查询条件的sql片段 this.processCondition(sql, keyName, operate, keyValue,
	 * ext);
	 * 
	 * String tableName = getTableName();
	 * 
	 * StringBuffer querySql = new StringBuffer(); querySql.append("select");
	 * querySql.append(queryStr); querySql.append(" from ");
	 * querySql.append(tableName);
	 * 
	 * if (sql != null && sql.length() > 0) { querySql.append(" force index(" +
	 * indexName + ") "); querySql.append(" where "); querySql.append(" " + sql
	 * + " "); }
	 * 
	 * if (null != orderByField && orderByField.length > 0 && null != pattern &&
	 * pattern.length > 0 && orderByField.length == pattern.length) {
	 * querySql.append(" order by "); for (int i = 0, length =
	 * orderByField.length; i < length; i++) { querySql.append(orderByField[i] +
	 * " " + pattern[i]); if (i < orderByField.length - 1) {
	 * querySql.append(", "); } } }
	 * 
	 * querySql.append(" limit ?, ? ");
	 * 
	 * Object[] param = new Object[keyValue.length + 2];
	 * System.arraycopy(keyValue, 0, param, 0, keyValue.length);
	 * param[keyValue.length] = (pageNo - 1) * pageSize; param[keyValue.length +
	 * 1] = pageSize;
	 * 
	 * List<T> resultList = select(querySql.toString(), param);
	 * 
	 * if (resultList == null) { resultList = new ArrayList<T>(); }
	 * 
	 * return resultList; }
	 */
	public   List<Map<String, String>> queryListMap(String sql) 
	{
		List<Map<String, String>> queryListMap = excetueManager.queryListMap(sql);
		return  queryListMap;
	}
}
