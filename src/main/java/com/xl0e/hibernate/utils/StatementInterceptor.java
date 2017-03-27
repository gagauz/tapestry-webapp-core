package com.xl0e.hibernate.utils;

import java.io.PrintStream;
import java.io.PrintWriter;
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
            initialized = true;
            connId = "Connection [" + connectionId + "] ";
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(connId + "created");
        }
    }

    private static String formatSQL(String sql) {
        return sql.replaceAll("select ", "\nselect\n\t")
                .replaceAll("delete ", "\ndelete\n\t")
                .replaceAll("update ", "\nupdate\n\t")
                .replaceAll(" from ", "\nfrom ")
                .replaceAll(" left ", "\nleft ")
                .replaceAll(" right ", "\nright ")
                .replaceAll(" where ", "\nwhere ").replaceAll(", ", ",\n\t");
    }

    static PrintWriter PW = new PrintWriter(System.out) {
        @Override
        public void print(Object obj) {
            System.out.println(1);
        };

        @Override
        public void println(Object x) {
            System.out.println(1);
        };

        @Override
        public void println(String x) {
            System.out.println(1);
        };

        @Override
        public String toString() {
            return "MyPrintWriter";
        }
    };

    @Override
    public ResultSetInternalMethods preProcess(String s, Statement statement, Connection connection)
            throws SQLException {
        if (!initialized) {
            init(connection, properties);
        }
        if (LOGGER.isDebugEnabled()) {
            if (s != null) {
                LOGGER.debug(connId + formatSQL(s));
            } else {
                String content = statement.toString();
                LOGGER.debug(connId + formatSQL(content.substring(content.indexOf(':'))));
                if (LOGGER.isTraceEnabled()) {
                    new Exception().printStackTrace(new PrintStream(System.out, true) {
                        @Override
                        public void println(Object x) {
                            String s = String.valueOf(x);
                            if (s.startsWith("\tat org.gagauz")) {
                                super.println(s);
                            }
                        }
                    });
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
            LOGGER.debug(connId + "destroyed");
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
