package me.marvin.smp.utils.sql.impl;

import me.marvin.smp.utils.sql.NonPooledSqlDatabase;

import java.sql.*;

/**
 * A simple SQL database implementation, which operates only using JDBC.
 *
 * @see Connection
 */
public class MySqlDatabase extends NonPooledSqlDatabase<Connection> {
    private final String host, user, db, pw;
    private final int port;

    public MySqlDatabase(String host, int port, String user, String pw, String db) {
        this.host = host;
        this.user = user;
        this.port = port;
        this.db = db;
        this.pw = pw;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws Exception {
        source = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8", user, pw);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        try {
            source.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
