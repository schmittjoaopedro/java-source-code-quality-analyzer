package com.github.schmittjoaopedro.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OracleETL {

	@Value("${oracle.database.url}")
	private String url;

	@Value("${oracle.database.user}")
	private String user;

	@Value("${oracle.database.pass}")
	private String pass;
	
	public String getSourceCode(Long ruleActionId) {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = getConnection();
			String selectSQL = "SELECT JAVA_SOURCE FROM RULE_ACTION WHERE ID = " + ruleActionId;
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(selectSQL);
			rs.next();
			return rs.getString(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnections(connection, statement);
		}
		return null;
	}

	private void closeConnections(Connection connection, Statement statement) {
		try {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	public List<Long> getRuleActionsId() {
		Connection connection = null;
		Statement statement = null;
		try {
			List<Long> ids = new ArrayList<>();
			connection = getConnection();
			String selectIdsSQL = "SELECT DISTINCT ID FROM RULE_ACTION WHERE JAVA_SOURCE IS NOT NULL";
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(selectIdsSQL);
			while(rs.next()) {
				ids.add(rs.getLong(1));
			}
			return ids;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnections(connection, statement);
		}
		return null;
	}

	public Connection getConnection() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (Exception e) {
			e.printStackTrace();
		}
        try {
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
	}
	
}
