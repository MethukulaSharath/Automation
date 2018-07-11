package com.trinet.mobile.automation.core;

	import java.sql.Connection;
	import java.sql.DriverManager;
	import java.sql.ResultSet;
	import java.sql.ResultSetMetaData;
	import java.sql.SQLException;
	import java.sql.Statement;
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.List;

	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.testng.Assert;

	/**
	 * @author gkuntilla Database Utility to create database connection, close the
	 *         connection and execute sql
	 */
	public class DatabaseUtil {

		public Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		private Logger logger = LoggerFactory.getLogger(this.getClass().toString());

		/**
		 * Creates the database connection based on the properties and env read from
		 * test.properties and main.properties
		 * 
		 * @param drivers
		 * @param connectionUrl
		 * @param username
		 * @param password
		 */
		public void createConn(String drivers, String connectionUrl, String username, String password) {

			//.info("Executing :: {}", BaseInitializer.getMethodName(Thread.currentThread().getStackTrace()));
			try {
				Class.forName(drivers);
				con = DriverManager.getConnection(connectionUrl, username, password);
				 System.out.println("Successfully Connected to : " +connectionUrl);
				logger.info("Successfully Connected to : " + connectionUrl);
			} catch (NullPointerException | ClassNotFoundException | SQLException e) {
				logger.error("Failed in createConn in the Database Util: {}", e.toString());
				logger.error("Exception in createConn :: {} ", e.getMessage());
			}
		}

		/**
		 * Close the Database connection
		 */
		public void closeConn() {

			//logger.info("Executing :: {}", BaseInitializer.getMethodName(Thread.currentThread().getStackTrace()));
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					logger.error("Failed in closeConn in the Database Util", e.toString());
					logger.error("Exception in closeConn :: {} ", e.getMessage());
				}
			}
		}

		/**
		 * @param query
		 *            Execute the sql query and print the result
		 * @return
		 */
		public ResultSet executeSQL(String query, long resultWait) {

			//logger.info(BaseInitializer.getMethodName(Thread.currentThread().getStackTrace()));
			try {
				Thread.sleep(resultWait);
				stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(query);
				if (rs == null) {
					Assert.fail("No records found in DataBase");
				} else {

				}
			} catch (SQLException e) {
				logger.error("Failed in executeSQL in the Database Util", e.toString());
				Assert.fail("Failed in executeSQL in the Database Util" + e.toString());
			} catch (Exception e) {
				logger.error("Failed in executeUpdateSQL in the Database Util", e.toString());
				logger.error("Exception in executeUpdateSQL :: {} ", e.getMessage());
			}
			return rs;
		}

		public ResultSet executeSQL(String query) {

			//logger.info(BaseInitializer.getMethodName(Thread.currentThread().getStackTrace()));
			try {
				stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(query);
				if (rs == null) {
					Assert.fail("No records found in DataBase");
				}
			} catch (SQLException e) {
				logger.error("Failed in executeSQL in the Database Util", e.toString());
				logger.error("Exception in executeSQL :: {} ", e.getMessage());
			}
			return rs;
		}

		/*
		 * this method just returns rs , Condition:: where if you don;t have any
		 * record then go and do some action to add one for example : if there is no
		 * EPTAG user in Db then add a user with that role , in this case we don;t
		 * want to fail the test
		 */
		public ResultSet executeSQLnoRSNullCheck(String query, long resultWait) {

			//logger.info(BaseInitializer.getMethodName(Thread.currentThread().getStackTrace()));
			try {
				stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(query);

			} catch (SQLException e) {
				logger.error("Failed in executeSQL in the Database Util", e.toString());
				logger.error("Exception in executeSQL :: {} ", e.getMessage());
			}
			return rs;
		}

		public List<HashMap<String, Object>> convertResultSetToList(ResultSet rs) {

			List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
			try {
				ResultSetMetaData rmd = rs.getMetaData();
				int nColumns = rmd.getColumnCount();
				while (rs.next()) {
					HashMap<String, Object> records = new HashMap<String, Object>(nColumns);
					for (int i = 1; i <= nColumns; ++i) {
						records.put(rmd.getColumnName(i), rs.getObject(i));
					}
					list.add(records);
				}
			} catch (SQLException e) {
				logger.error("Failed in convertResultSetToList in the Database Util", e.getMessage());
			}
			return list;
		}

		public void executeUpdateSQL(String query, long resultWait) {
			//logger.info(BaseInitializer.getMethodName(Thread.currentThread().getStackTrace()));
			try {
				stmt = con.createStatement();
				stmt.executeQuery(query);
				Thread.sleep(resultWait);
			} catch (SQLException e) {
				logger.error("Failed in executeUpdateSQL in the Database Util", e.toString());
				logger.error("Exception in executeUpdateSQL :: {} ", e.getMessage());
			} catch (Exception e) {
				logger.error("Failed in executeUpdateSQL in the Database Util", e.toString());
				logger.error("Exception in executeUpdateSQL :: {} ", e.getMessage());
			}

		}

		/* Added by Jothsna */
		/**
		 * @param query
		 *            Execute the sql query and return the result set
		 * @return
		 */
		public ResultSet executeSQLDefault(String query, long resultWait) {

			//logger.info(BaseInitializer.getMethodName(Thread.currentThread().getStackTrace()));
			try {
				Thread.sleep(resultWait);
				stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(query);
				if (rs == null) {
					logger.info("No records found in DataBase");
				} else {
					System.out.println(rs.first());
				}

			} catch (SQLException e) {
				logger.error("Failed in executeSQL in the Database Util", e.toString());

			} catch (Exception e) {
				logger.error("Failed in executeUpdateSQL in the Database Util", e.toString());
				logger.error("Exception in executeUpdateSQL :: {} ", e.getMessage());
			}
			return rs;
		}

		/* Added by Venkat */
		/**
		 * @param query
		 * @return
		 * @throws SQLException
		 */
		public String getDBData(String query) throws SQLException {
			String result = "";
			try {
				stmt = con.createStatement();

				logger.info("Executing the data base query :: {}", query);
				rs = stmt.executeQuery(query);
				ResultSetMetaData resultSetMetaData = rs.getMetaData();

				int numberOfColums = resultSetMetaData.getColumnCount();
				logger.info("Number of  columns count in the query :: {}", numberOfColums);

				while (rs.next()) {
					for (int i = 1; i <= numberOfColums; i++) {
						String value = rs.getString(i);
						if (value == null) {
							value = "";
						}
						result = result + value + ",";
					}
				}
				result = result.substring(0, result.length() - 1);
				rs.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
			return result;
		}

		// Added by Imran
		/**
		 * @param query
		 * @return
		 * @throws SQLException
		 */
		public List<String> getDBRecords(String query) throws SQLException {
			List<String> dbRecords = new ArrayList<String>();
			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery(query);
				rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();

				while (rs.next()) {
					for (int i = 1; i <= columnCount; i++) {
						dbRecords.add(rs.getString(i));
					}

				}

			} catch (Exception e) {
				e.printStackTrace();

			}
			return dbRecords;

		}

	}



