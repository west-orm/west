package com.bucuoa.west.orm.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
/**
 * 链接管理器
 * @author jake
 *
 */
public class ConnectionManager {
	// 用于多线程并发下保存本线程connection 用于一个请求多个service的事物管理
	private static ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>();

	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 获取连接
	 * @return
	 */
	public Connection getConnection() {
		Connection conn = connectionHolder.get();
		if (conn == null) {
			try {
				conn = dataSource.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			connectionHolder.set(conn);
		}

		return conn;
	}

	// 关闭连接
	public  void closeConnection() {
		Connection conn = connectionHolder.get();
		if (conn != null) {
			try {
				conn.close();
				// 从ThreadLocal中清除Connection
				connectionHolder.remove();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}