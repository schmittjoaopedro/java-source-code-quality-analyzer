package com.github.schmittjoaopedro.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.github.schmittjoaopedro.model.SourceCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.transform.Source;

@Service
public class OracleETL {

	@Value("${oracle.database.url}")
	private String url;

	@Value("${oracle.database.user}")
	private String user;

	@Value("${oracle.database.pass}")
	private String pass;
	
	public SourceCode getSourceCode(Long ruleActionId) {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = getConnection();
			String selectSQL = "SELECT RA.JAVA_SOURCE, R.USER_CREATED, R.USER_UPDATED FROM RULE_ACTION RA JOIN RULE R ON R.ID = RA.RULE_ID WHERE RA.ID = " + ruleActionId;
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(selectSQL);
			rs.next();
			String source = rs.getString(1);
			String userCreated = rs.getString(2);
			String userUpdated = rs.getString(3);
			SourceCode sourceCode = new SourceCode();
			sourceCode.setSourceCode(source);
			if(userUpdated == null) {
				sourceCode.setUser(userCreated);
			} else {
				sourceCode.setUser(userUpdated);
			}
			return sourceCode;
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
