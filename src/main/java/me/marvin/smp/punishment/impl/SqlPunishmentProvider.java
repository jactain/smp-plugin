package me.marvin.smp.punishment.impl;

import me.marvin.smp.punishment.Punishment;
import me.marvin.smp.punishment.PunishmentProvider;
import me.marvin.smp.punishment.PunishmentType;
import me.marvin.smp.utils.sql.SqlDatabase;
import me.marvin.smp.utils.sql.impl.H2Database;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.RowSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class SqlPunishmentProvider implements PunishmentProvider {
    private final SqlDatabase<?> database;
    private final String table;

    @Language("GenericSQL")
    private static final String TABLE_CREATION_QUERY =
        "CREATE TABLE IF NOT EXISTS `%s` (" +
            "`id` int PRIMARY KEY NOT NULL AUTO_INCREMENT," +
            "`active` tinyint(1) NOT NULL," +
            "`type` varchar(32) NOT NULL," +
            "`target` varchar(36) NOT NULL," +
            "`issuer` varchar(36) NOT NULL," +
            "`issuedOn` bigint NOT NULL," +
            "`issueReason` varchar(255) NOT NULL," +
            "`issuedUntil` bigint NOT NULL," +
            "`liftedBy` varchar(36)," +
            "`liftedOn` bigint," +
            "`liftReason` varchar(255))";

    @Language("GenericSQL")
    private static final String TABLE_CREATION_QUERY_H2 = TABLE_CREATION_QUERY + ";";

    @Language("GenericSQL")
    private static final String TABLE_CREATION_QUERY_SQL = TABLE_CREATION_QUERY + " DEFAULT CHARSET=utf8;";

    @Language("GenericSQL")
    private static final String ISSUE_QUERY =
        "INSERT INTO `%s` (" +
            "`active`," +
            "`type`," +
            "`target`," +
            "`issuer`," +
            "`issuedOn`," +
            "`issueReason`," +
            "`issuedUntil`" +
        ") VALUES (?, ?, ?, ?, ?, ?, ?);";

    @Language("GenericSQL")
    private static final String LIFT_QUERY =
        "UPDATE `%s` SET " +
            "`active` = ?," +
            "`liftedBy` = ?," +
            "`liftedOn` = ?," +
            "`liftReason` = ?" +
            " WHERE `id` = ?;";

    @Language("GenericSQL")
    private static final String ACTIVE_QUERY =
        "SELECT * FROM `%s` WHERE " +
            "`active` = ? AND " +
            "`type` = ? AND " +
            "`target` = ?" +
            " LIMIT 1;";

    @Language("GenericSQL")
    private static final String ALL_QUERY =
        "SELECT * FROM `%s` WHERE " +
            "`target` = ? " +
            "ORDER BY `issuedOn` DESC;";

    public SqlPunishmentProvider(SqlDatabase<?> database, String table) {
        this.database = database;
        this.table = table;

        database.update((database instanceof H2Database ? TABLE_CREATION_QUERY_H2 : TABLE_CREATION_QUERY_SQL).formatted(table), i -> {});
    }

    @Override
    public void issuePunishment(@NotNull Punishment punishment) {
        database.use(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(ISSUE_QUERY.formatted(table), Statement.RETURN_GENERATED_KEYS)) {
                statement.setObject(1, punishment.active());
                statement.setObject(2, punishment.type().name());
                statement.setObject(3, punishment.target().toString());
                statement.setObject(4, punishment.issuer().toString());
                statement.setObject(5, punishment.issuedOn());
                statement.setObject(6, punishment.issueReason());
                statement.setObject(7, punishment.issuedUntil());

                if (statement.executeUpdate() != 0) {
                    try (ResultSet insertionResult = statement.getGeneratedKeys()) {
                        if (insertionResult.next()) {
                            punishment.id(insertionResult.getInt(1));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void liftPunishment(@NotNull Punishment punishment) {
        if (!punishment.type().isLiftable()) {
            return;
        }

        if (punishment.id() == Punishment.NOT_SET) {
            throw new IllegalArgumentException("Punishment#id was not set (returned Punishment.NOT_SET)");
        }

        UUID liftedBy = punishment.liftedBy();

        database.update(
            LIFT_QUERY.formatted(table),
            i -> {},
            punishment.active(),
            liftedBy != null ? liftedBy.toString() : null,
            punishment.liftedOn(),
            punishment.liftReason(),
            punishment.id()
        );
    }

    @Override
    @Nullable
    public Punishment findActive(@NotNull UUID target, @NotNull PunishmentType type) throws Exception {
        try (RowSet result = database.query(ACTIVE_QUERY.formatted(table), true, type.name(), target.toString())) {
            if (result.next()) {
                return punishmentFromRowSet(result);
            }
        }

        return null;
    }

    @Override
    @NotNull
    public Collection<Punishment> getPunishments(@NotNull UUID uuid) throws Exception {
        try (RowSet result = database.query(ALL_QUERY.formatted(table), uuid.toString())) {
            List<Punishment> punishments = new ArrayList<>();

            while (result.next()) {
                punishments.add(punishmentFromRowSet(result));
            }

            return punishments;
        }
    }

    @NotNull
    private static Punishment punishmentFromRowSet(RowSet result) throws SQLException {
        String liftedBy = result.getString("liftedBy");

        return new Punishment()
            .id(result.getInt("id"))
            .active(result.getBoolean("active"))
            .type(PunishmentType.valueOf(result.getString("type")))
            .target(UUID.fromString(result.getString("target")))
            .issuer(UUID.fromString(result.getString("issuer")))
            .issuedOn(result.getLong("issuedOn"))
            .issueReason(result.getString("issueReason"))
            .issuedUntil(result.getLong("issuedUntil"))
            .liftedBy(liftedBy != null ? UUID.fromString(liftedBy) : null)
            .liftedOn(result.getLong("liftedOn"))
            .liftReason(result.getString("liftReason"));
    }
}
