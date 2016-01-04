package com.bucuoa.west.orm.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 * 会话工厂类
 * @author jake
 *
 */
public class SessionFactory {

	private ConnectionManager connectionManager;

	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public void setConnectionManager(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	public  Session getSession() {
		Session session = new Session();
		Connection conn = null;
		try {

			long start2 = System.currentTimeMillis();
			conn = connectionManager.getConnection();// DriverManager.getConnection(url, user, password);
			long end2 = System.currentTimeMillis();
			long l = end2 - start2;
			
			session.setCreateUseTime(l); //统计创建connection时间
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.setConnection(conn);
		session.setCreateTime(new Date());
		
		return session;
	}


	public  void closeObject(Object o) {
		if (o != null) {
			if (o instanceof Connection) {
				try {
//					((Connection) o).close();
					connectionManager.closeConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (o instanceof Statement) {
				try {
					((Statement) o).close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (o instanceof PreparedStatement) {
				try {
					((PreparedStatement) o).close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (o instanceof ResultSet) {
				try {
					((ResultSet) o).close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
