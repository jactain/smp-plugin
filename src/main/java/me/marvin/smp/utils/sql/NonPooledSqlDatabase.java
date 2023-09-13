package me.marvin.smp.utils.sql;

import me.marvin.smp.utils.ThrowableConsumer;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a non-pooled database connection.
 *
 * @param <C> the type of the connection
 */
public abstract class NonPooledSqlDatabase<C extends Connection> extends SqlDatabase<C> {
    /**
     * {@inheritDoc}
     *
     * @param consumer the consumer
     */
    @Override
    public void use(ThrowableConsumer<Connection, SQLException> consumer) {
        try {
            consumer.accept(getSource());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
