package me.marvin.smp.utils.sql;

import me.marvin.smp.utils.ThrowableConsumer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a pooled database connection.
 *
 * @param <P> the type of the pool
 */
public abstract class PooledSqlDatabase<P extends DataSource> extends SqlDatabase<P> {
    /**
     * {@inheritDoc}
     *
     * @param consumer the consumer
     */
    @Override
    public void use(ThrowableConsumer<Connection, SQLException> consumer) {
        try (Connection connection = getSource().getConnection()) {
            consumer.accept(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
