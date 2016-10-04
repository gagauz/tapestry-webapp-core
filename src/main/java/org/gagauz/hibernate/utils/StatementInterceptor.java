package org.gagauz.hibernate.utils;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.JDBC4Connection;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.Statement;

public class StatementInterceptor implements com.mysql.jdbc.StatementInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatementInterceptor.class);

	private Properties properties;

	private String connId;

	private boolean initialized = false;

	@Override
	public void init(Connection connection, Properties properties) throws SQLException {
		this.properties = properties;
		long connectionId = ((JDBC4Connection) connection).getId();

		if (connectionId != 0) {
			this.initialized = true;
			this.connId = "Connection [" + connectionId + "] ";
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(this.connId + "created");
		}
	}

	@Override
	public ResultSetInternalMethods preProcess(String s, Statement statement, Connection connection)
			throws SQLException {
		if (!this.initialized) {
			init(connection, this.properties);
		}
		if (LOGGER.isDebugEnabled()) {
			if (s != null) {
				LOGGER.debug(this.connId + s);
			} else {
				String content = statement.toString();
				LOGGER.debug(this.connId + content.substring(content.indexOf(':')));
				if (LOGGER.isTraceEnabled()) {
					Thread.dumpStack();
				}
			}
		}

		return null;
	}

	@Override
	public ResultSetInternalMethods postProcess(String s, Statement statement, ResultSetInternalMethods resultSetInternalMethods,
			Connection connection)
			throws SQLException {
		SQLWarning warning;
		if (resultSetInternalMethods != null
				&& (warning = resultSetInternalMethods.getWarnings()) != null) {
			log(warning);
		} else if ((warning = connection.getWarnings()) != null) {
			log(warning);
		}
		return resultSetInternalMethods;
	}

	@Override
	public boolean executeTopLevelOnly() {
		return false;
	}

	@Override
	public void destroy() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(this.connId + "destroyed");
		}
	}

	private void log(SQLWarning warning) {
		LOGGER.warn("ATTENSION!!!");
		while (warning != null) {
			LOGGER.warn(warning.getMessage());
			warning = warning.getNextWarning();
		}
	}
}
