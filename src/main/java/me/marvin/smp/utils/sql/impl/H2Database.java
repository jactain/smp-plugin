package me.marvin.smp.utils.sql.impl;

import me.marvin.smp.utils.sql.NonPooledSqlDatabase;

import java.nio.file.Path;
import java.sql.*;

/**
 * A simple H2 database implementation based on {@link MySqlDatabase}.
 *
 * @see MySqlDatabase
 */
public class H2Database extends NonPooledSqlDatabase<Connection> {
    private final Path path;

    public H2Database(Path path) {
        this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws Exception {
        Class.forName("org.h2.Driver");
        source = DriverManager.getConnection("jdbc:h2:file:" + path + ";MODE=MariaDB;DATABASE_TO_LOWER=TRUE");
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
