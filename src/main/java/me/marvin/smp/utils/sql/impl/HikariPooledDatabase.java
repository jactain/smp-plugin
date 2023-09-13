package me.marvin.smp.utils.sql.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.marvin.smp.utils.sql.PooledSqlDatabase;

/**
 * An SQL database implementation what uses a connection pool provided by Hikari.
 *
 * @see HikariDataSource
 */
public class HikariPooledDatabase extends PooledSqlDatabase<HikariDataSource> {
    private final HikariConfig config;
    private HikariDataSource connection;

    public HikariPooledDatabase(HikariConfig config) {
        this.config = config;
    }

    public HikariPooledDatabase(String host, int port, int poolSize, String user, String pw, String db) {
        this(new HikariConfig());
        this.config.setDriverClassName("com.mysql.jdbc.Driver");
        this.config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8");
        this.config.setUsername(user);
        this.config.setPassword(pw);
        this.config.setMaximumPoolSize(poolSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() {
        connection = new HikariDataSource(config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}