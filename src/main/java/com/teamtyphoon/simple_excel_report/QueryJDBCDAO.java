package com.teamtyphoon.simple_excel_report;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryJDBCDAO implements QueryDAO {
	private Logger logger = LoggerFactory.getLogger(QueryJDBCDAO.class);

	private String dbUrl;
	private String dbUserName;
	private String dbPassword;

	public QueryJDBCDAO(String dbUrl, String dbUserName, String dbPassword) {
		this.dbUrl = dbUrl;
		this.dbUserName = dbUserName;
		this.dbPassword = dbPassword;
	}

	public List<Object[]> query(String sql, Object... params) {
		if (logger.isDebugEnabled()) {
			logger.debug("-SQL-");
			logger.debug(sql);
			logger.debug("-PARAMS-");
			if (params != null) {
				for (Object param : params) {
					logger.debug((String) param);
				}
			}
		}
		ResultSetHandler<List<Object[]>> h = new ResultSetHandler<List<Object[]>>() {
			public List<Object[]> handle(ResultSet rs) throws SQLException {
				List<Object[]> rsList = new ArrayList<Object[]>();
				ResultSetMetaData meta = rs.getMetaData();
				int cols = meta.getColumnCount();
				Object[] column = new Object[cols];
				for (int i = 0; i < cols; i++) {
					column[i] = meta.getColumnLabel(i + 1);
				}
				rsList.add(column);

				while (rs.next()) {
					Object[] result = new Object[cols];
					for (int i = 0; i < cols; i++) {
						result[i] = rs.getObject(i + 1);
					}
					rsList.add(result);
				}

				return rsList;
			}
		};
		List<Object[]> rsList = new ArrayList<Object[]>();
		// No DataSource so we must handle Connections manually
		QueryRunner run = new QueryRunner();
		// Create a QueryRunner that will use connections from
		// the given DataSource
		// QueryRunner run = new QueryRunner(dataSource);

		Connection c = null;
		try {
			c = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
			// DatabaseMetaData metaData = c.getMetaData();
			// Execute the query and get the results back from the handler
			// Object[] result = run.query(c, "SELECT id,nm,test_date FROM Test
			// WHERE id=?", h, 1);
			List<Object[]> query = run.query(c, sql, h, params);
			rsList.addAll(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (c != null) {
					c.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// try {
		// Object[] result = run.query(conn, "SELECT * FROM Person WHERE
		// name=?", h, "John Doe");
		// // do something with the result
		//
		// } finally {
		// // Use this helper method so we don't have to check for null
		// DbUtils.close(conn);
		// }
		return rsList;
	}

}
