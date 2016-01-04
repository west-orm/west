package com.bucuoa.west.orm.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bucuoa.west.orm.core.converter.ObjectConverter;
import com.bucuoa.west.orm.core.uitls.AnnoationUtil;
/**
 * sql执行器
 * @author jake
 *
 */
public class ExcetueManager {
	
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public int doDML(String sql, Object[] objects) {
		Connection conn = null;
		PreparedStatement pst = null;
		
		int flag = 0;
		Session session = sessionFactory.getSession();
		
		try {
			
			conn = session.getConnection();

			pst = conn.prepareStatement(sql);
			
			if (objects != null) {
				for (int i = 0; i < objects.length; i++) {
					pst.setObject(i + 1, objects[i]);
				}
			}
			flag = pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sessionFactory.closeObject(pst);
			sessionFactory.closeObject(conn);
			sessionFactory.closeObject(session);
		}
		return flag;
	}
	
	/**
	 * 生成有返回值的更新对象的sql
	 * @param sql
	 * @return
	 */
	public Object executeUpdateSql(String sql) {
		
		Connection conn = null;
		PreparedStatement pst = null;
	
		Session session = sessionFactory.getSession();
		
		Long id = 0L;
		
		try {
			long start2 = System.currentTimeMillis();
			conn = session.getConnection();

			pst = conn.prepareStatement(sql.toString(),Statement.RETURN_GENERATED_KEYS);
			
			pst.executeUpdate();
			
			conn.commit();
			long end2 = System.currentTimeMillis();
			
			System.out.println("\t\t执行sql{" + (end2 - start2)+ "}ms,创建链接["+session.getCreateUseTime()+"]ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ResultSet rs = null;
		try {
			rs = pst.getGeneratedKeys();  
			if (rs.next()) {
			    id = rs.getLong(1);   
			}
		} catch (Exception e) {
			System.out.println("id return is error");
		}finally
		{
			sessionFactory.closeObject(rs);
			sessionFactory.closeObject(pst);
			sessionFactory.closeObject(conn);
			sessionFactory.closeObject(session);
		}
		return id;
	}
	
	/**
	 * 生成没有返回值的更新对象的sql
	 * @param sql
	 * @return
	 */
	public boolean executeUpdateSqlNoID(String sql) {
		
		Connection conn = null;
		PreparedStatement pst = null;
	
		Session session = sessionFactory.getSession();
		
		try {
			long start2 = System.currentTimeMillis();
			conn = session.getConnection();

			pst = conn.prepareStatement(sql.toString(),Statement.RETURN_GENERATED_KEYS);
			
			pst.executeUpdate();
			
			conn.commit();
			long end2 = System.currentTimeMillis();
			
			System.out.println("\t\t执行sql{" + (end2 - start2)+ "}ms,创建链接["+session.getCreateUseTime()+"]ms");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally
		{
			sessionFactory.closeObject(pst);
			sessionFactory.closeObject(conn);
			sessionFactory.closeObject(session);
		}
		return true;
	}
	/**
	 * 用户单一值结果集查询
	 * @param sql
	 * @return
	 */
	public int queryCount(String sql) {
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		int result = 0;
		
		Session session = sessionFactory.getSession();
		
		try {
			conn = session.getConnection();

			pst = conn.prepareStatement(sql);
			rst = pst.executeQuery();
			if (rst.next()) {
				
				result = rst.getInt(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sessionFactory.closeObject(rst);
			sessionFactory.closeObject(pst);
			sessionFactory.closeObject(conn);
			sessionFactory.closeObject(session);
		}
		
		return result;
	}
	
	/**
	 * 用于唯一的对象查询
	 * 如果有重复取第一个值
	 * @param sql
	 * @param t
	 * @return
	 */
	public <T> T queryOne(String sql, Class<T> t) {
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		T obj = null;
		
		Session session = sessionFactory.getSession();
		
		try {
			conn = session.getConnection();

			pst = conn.prepareStatement(sql);
			rst = pst.executeQuery();
			
			obj = ObjectConverter.getOneBean(t, rst);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sessionFactory.closeObject(rst);
			sessionFactory.closeObject(pst);
			sessionFactory.closeObject(conn);
			sessionFactory.closeObject(session);
		}
		return obj;
	}

	
	
	/**
	 * 对象列表查询
	 * @param sql
	 * @param t
	 * @return
	 */
	public <T> List<T> queryList(String sql, Class<T> t) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		List<T> list = new ArrayList<T>();
		Session session = sessionFactory.getSession();
		try {
			conn = session.getConnection();

			pst = conn.prepareStatement(sql);
		
			rst = pst.executeQuery();
			
			list = ObjectConverter.getListBean(t, rst);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sessionFactory.closeObject(rst);
			sessionFactory.closeObject(pst);
			sessionFactory.closeObject(conn);
			sessionFactory.closeObject(session);
		}
		return list;
	}

	
	
	/**
	 * 对象结果用MAP封装的方法
	 * 用户多表查询
	 * @param sql
	 * @return
	 */
	public <T> List<Map<String,String>> queryListMap(String sql) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		Session session = sessionFactory.getSession();
		try {
			conn = session.getConnection();
			
			pst = conn.prepareStatement(sql);
			rst = pst.executeQuery();
			
			List<String> columns = ObjectConverter.getColumns(rst);
				
			list = ObjectConverter.getListMap(rst,  columns);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sessionFactory.closeObject(rst);
			sessionFactory.closeObject(pst);
			sessionFactory.closeObject(conn);
			sessionFactory.closeObject(session);
		}
		return list;
	}



	

	/**
	 * 批量执行
	 * @param sql
	 * @param argsList
	 */
	public void addBatch(String sql, List<String[]> argsList) {
		Connection conn = null;
		PreparedStatement prest = null;
		
		Session session = sessionFactory.getSession();
		
		try {
			conn = session.getConnection();
			
			conn.setAutoCommit(false);
			
			prest = conn.prepareStatement(sql);

			for (String[] strings : argsList) {
				for (int i = 0; i < strings.length; i++) {
					prest.setString(i + 1, strings[i]);
				}
				prest.addBatch();
			}

			prest.executeBatch();
			conn.commit();
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			sessionFactory.closeObject(prest);
			sessionFactory.closeObject(conn);
			sessionFactory.closeObject(session);
		}
	}
	
	/**
	 * 根据对象主键删除对象
	 * @param classk
	 * @param id
	 */
	public void deleteById(Class<?> classk,Object id) {
		Connection conn = null;
		PreparedStatement prest = null;
		
		String tableName = AnnoationUtil.getTablename(classk);
		
		String sql = "delete from "+tableName+" where id="+id;
		
		Session session = sessionFactory.getSession();
		try {
			
			conn = session.getConnection();

			conn.setAutoCommit(false);
			prest = conn.prepareStatement(sql);
			prest.executeUpdate();
			conn.commit();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			sessionFactory.closeObject(prest);
			sessionFactory.closeObject(conn);
			sessionFactory.closeObject(session);
		}
	}
	
	/**
	 * 根据条件删除对象
	 * @param classk
	 * @param condtion
	 */
	public void deleteBy(Class<?> classk,String condtion) {
		Connection conn = null;
		PreparedStatement prest = null;
		
		String tableName = AnnoationUtil.getTablename(classk);
		String sql = "delete from "+tableName+" where "+condtion;
		
		Session session = sessionFactory.getSession();
		try {
			conn = session.getConnection();
			
			conn.setAutoCommit(false);
			prest = conn.prepareStatement(sql);
			prest.executeUpdate();
			conn.commit();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			sessionFactory.closeObject(prest);
			sessionFactory.closeObject(conn);
			sessionFactory.closeObject(session);
		}
	}
}
