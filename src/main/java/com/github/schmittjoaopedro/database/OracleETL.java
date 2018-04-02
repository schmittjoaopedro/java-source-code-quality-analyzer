package com.github.schmittjoaopedro.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.github.schmittjoaopedro.model.Metric;
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

	private String ETL_QUERY = "SELECT R.ID AS ID,\n" +
			"  R.RULE_VERSION_ID AS VERSION_ID,\n" +
			"  RA.ID AS ACTION_ID,\n" +
			"  R.DESCRIPTION AS DESCRIPTION,\n" +
			"  R.USER_CREATED AS USER_CREATED,\n" +
			"  R.USER_UPDATED AS USER_UPDATED,\n" +
			"  R.DATE_CREATED AS DATE_CREATED,\n" +
			"  R.DATE_CREATED AS DATE_UPDATED,\n" +
			"  RA.JAVA_SOURCE AS SOURCE\n" +
			"FROM RULE_ACTION RA\n" +
			"JOIN RULE R\n" +
			"ON R.ID     = RA.RULE_ID\n" +
			"WHERE RA.ID = ";
	
	public Metric getMetric(Long ruleActionId) {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = getConnection();
			String selectSQL = ETL_QUERY + ruleActionId;
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(selectSQL);
			rs.next();
			Metric metric = new Metric();
			metric.setRuleId(rs.getLong(1));
			metric.setRuleVersionId(rs.getLong(2));
			metric.setRuleActionId(rs.getLong(3));
			metric.setDescription(rs.getString(4));
			metric.setUserCreated(rs.getString(5));
			metric.setUserUpdated(rs.getString(6));
			metric.setDateCreated(rs.getTimestamp(7));
			metric.setDateUpdated(rs.getTimestamp(8));
			metric.setSourceCode(rs.getString(9));
			return metric;
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
