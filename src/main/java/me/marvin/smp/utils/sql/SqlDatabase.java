package me.marvin.smp.utils.sql;

import me.marvin.smp.utils.IntArrayConsumer;
import me.marvin.smp.utils.ThrowableConsumer;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.UnknownNullability;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * An SQL-based database instance.
 *
 * @param <S> the type of the datasource
 */
public abstract class SqlDatabase<S> {
    protected static final RowSetFactory FACTORY;

    static {
        RowSetFactory tempVar = null;

        try {
            tempVar = RowSetProvider.newFactory();
        } catch (SQLException ex) {
            Logger.getLogger(SqlDatabase.class.getName()).warning("Failed to instantiate " + RowSetFactory.class);
        }

        FACTORY = tempVar;
    }

    /**
     * The database source instance.
     */
    protected S source;

    /**
     * Returns the database source.
     *
     * @return the database source
     */
    @UnknownNullability
    public final S getSource() {
        return source;
    }

    /**
     * Initializes a connection to the database.
     *
     * @throws Exception if an error happens while establishing a connection
     */
    public abstract void connect() throws Exception;

    /**
     * Disconnects from the database.
     */
    public abstract void disconnect();

    /**
     * Consumes a connection instance for "raw" database access.
     *
     * @param consumer the consumer
     */
    public abstract void use(ThrowableConsumer<Connection, SQLException> consumer);

    /**
     * Executes the given query for the database, and consumes the result.
     *
     * @param query  the query
     * @param result the consumer which gets invoked with the result
     * @param params the parameters of the query
     */
    public void query(@Language("GenericSQL") String query, Consumer<ResultSet> result, Object... params) {
        use(connection -> {
            Objects.requireNonNull(connection, "connection is null");
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                int index = 1;

                for (Object param : params) {
                    stmt.setObject(index++, param);
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    result.accept(rs);
                }
            }
        });
    }

    /**
     * Executes the given query for the database, and returns a result.
     *
     * @param query  the query
     * @param params the parameters of the query
     */
    public CachedRowSet query(@Language("GenericSQL") String query, Object... params) {
        if (FACTORY == null) {
            return null;
        }

        AtomicReference<CachedRowSet> ref = new AtomicReference<>();

        use(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                int index = 1;

                for (Object param : params) {
                    stmt.setObject(index++, param);
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    CachedRowSet set = FACTORY.createCachedRowSet();
                    set.populate(rs);
                    ref.set(set);
                }
            }
        });

        return ref.get();
    }

    /**
     * Executes the given update for the database, and returns the result.
     *
     * @param query  the query
     * @param result the consumer which gets invoked with the result
     * @param params the parameters of the query
     */
    public void update(@Language("GenericSQL") String query, IntConsumer result, Object... params) {
        use(connection -> {
            Objects.requireNonNull(connection, "connection is null");
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                int index = 1;

                for (Object param : params) {
                    stmt.setObject(index++, param);
                }

                result.accept(stmt.executeUpdate());
            }
        });
    }

    /**
     * Executes the given updates for the database, and consumes the result.
     *
     * @param query  the query
     * @param result the consumer which gets invoked with the result(s)
     * @param params the parameters of the query
     */
    public void batchUpdate(@Language("GenericSQL") String query, IntArrayConsumer result, Object[]... params) {
        use(connection -> {
            Objects.requireNonNull(connection, "connection is null");
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                for (Object[] container : params) {
                    int index = 1;

                    for (Object param : container) {
                        stmt.setObject(index++, param);
                    }

                    stmt.addBatch();
                }

                result.accept(IntStream.of(stmt.executeBatch()).toArray());
            }
        });
    }
}
