package me.marvin.smp;

import me.marvin.smp.commands.PunishmentCommand;
import me.marvin.smp.listener.ChatListener;
import me.marvin.smp.listener.ProfileListener;
import me.marvin.smp.profile.ProfileHandler;
import me.marvin.smp.punishment.PunishmentProvider;
import me.marvin.smp.punishment.PunishmentType;
import me.marvin.smp.punishment.impl.SqlPunishmentProvider;
import me.marvin.smp.utils.config.ConfigurationLoader;
import me.marvin.smp.utils.sql.SqlDatabase;
import me.marvin.smp.utils.sql.impl.H2Database;
import me.marvin.smp.utils.sql.impl.HikariPooledDatabase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Bootstrap extends JavaPlugin {
    public static Bootstrap INSTANCE;
    private ProfileHandler profileHandler;
    private SqlDatabase<?> database;
    private PunishmentProvider punishmentProvider;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ConfigurationLoader.initializeConfig(this, Configuration.class);
        ConfigurationLoader.initializeConfig(this, Language.class);
        INSTANCE = this;
        profileHandler = new ProfileHandler();

        database = switch (Configuration.Database.TYPE.toLowerCase()) {
            case "sql" -> new HikariPooledDatabase(
                Configuration.Database.Sql.HOST,
                Configuration.Database.Sql.PORT,
                Configuration.Database.Sql.POOL_SIZE,
                Configuration.Database.Sql.USER,
                Configuration.Database.Sql.PASSWORD,
                Configuration.Database.Sql.DATABASE
            );
            case "h2" -> new H2Database(getDataFolder().toPath().resolve("database").toAbsolutePath());
            default -> throw new IllegalArgumentException("unknown database type: %s".formatted(Configuration.Database.TYPE));
        };

        try {
            database.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        punishmentProvider = new SqlPunishmentProvider(database, Configuration.Database.PUNISHMENTS_TABLE);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new ProfileListener(), this);

        registerCommands();
    }

    @Override
    public void onDisable() {
        database.disconnect();
    }

    /**
     * Returns the profile handler.
     *
     * @return the profile handler
     */
    @NotNull
    public ProfileHandler profileHandler() {
        return profileHandler;
    }

    /**
     * Returns the database connection instance.
     *
     * @return the database
     */
    public SqlDatabase<?> database() {
        return database;
    }

    /**
     * Returns the punishment provider.
     *
     * @return the punishment provider
     */
    public PunishmentProvider punishmentProvider() {
        return punishmentProvider;
    }

    private void registerCommands() {
        Bukkit.getCommandMap().register("smp",
            new PunishmentCommand("mute", Configuration.MUTE_PERMISSION, PunishmentType.MUTE).configure(c -> {
                c.usage = Language.Punishments.Mute.USAGE;
                c.failedToLoadProfileMessage = Language.Punishments.FAILED_TO_LOAD_TARGET_PROFILE;
                c.broadcastMessage = Language.Punishments.Mute.BROADCAST;
                c.defaultReason = Language.Punishments.Mute.DEFAULT_REASON;
                c.alreadyPunishedMessage = Language.Punishments.Mute.ALREADY_PUNISHED;
            })
        );
        Bukkit.getCommandMap().register("smp",
            new PunishmentCommand("ban", Configuration.BAN_PERMISSION, PunishmentType.BAN).configure(c -> {
                c.usage = Language.Punishments.Ban.USAGE;
                c.failedToLoadProfileMessage = Language.Punishments.FAILED_TO_LOAD_TARGET_PROFILE;
                c.broadcastMessage = Language.Punishments.Ban.BROADCAST;
                c.defaultReason = Language.Punishments.Ban.DEFAULT_REASON;
                c.alreadyPunishedMessage = Language.Punishments.Ban.ALREADY_PUNISHED;
                c.kickMessage = Language.Punishments.Ban.MESSAGE;
            })
        );
        Bukkit.getCommandMap().register("smp",
            new PunishmentCommand("kick", Configuration.KICK_PERMISSION, PunishmentType.KICK).configure(c -> {
                c.usage = Language.Punishments.Kick.USAGE;
                c.broadcastMessage = Language.Punishments.Kick.BROADCAST;
                c.defaultReason = Language.Punishments.Kick.DEFAULT_REASON;
                c.kickMessage = Language.Punishments.Kick.MESSAGE;
                c.invalidTargetMessage = Language.Punishments.OFFLINE_TARGET;
            })
        );

        Bukkit.getCommandMap().register("smp",
            new PunishmentCommand("tempmute", Configuration.TEMPMUTE_PERMISSION, PunishmentType.MUTE, "tmute", "tm").configure(c -> {
                c.usage = Language.Punishments.TempMute.USAGE;
                c.failedToLoadProfileMessage = Language.Punishments.FAILED_TO_LOAD_TARGET_PROFILE;
                c.broadcastMessage = Language.Punishments.TempMute.BROADCAST;
                c.defaultReason = Language.Punishments.TempMute.DEFAULT_REASON;
                c.bypassPermission = Configuration.MUTE_PERMISSION;
                c.alreadyPunishedMessage = Language.Punishments.TempMute.ALREADY_PUNISHED;
                c.invalidDurationMessage = Language.Punishments.INVALID_DURATION;
                c.tooLongDurationMessage = Language.Punishments.TOO_LONG_DURATION;
            })
        );
        Bukkit.getCommandMap().register("smp",
            new PunishmentCommand("tempban", Configuration.TEMPBAN_PERMISSION, PunishmentType.BAN, "tban", "tb").configure(c -> {
                c.usage = Language.Punishments.TempBan.USAGE;
                c.failedToLoadProfileMessage = Language.Punishments.FAILED_TO_LOAD_TARGET_PROFILE;
                c.broadcastMessage = Language.Punishments.TempBan.BROADCAST;
                c.defaultReason = Language.Punishments.TempBan.DEFAULT_REASON;
                c.bypassPermission = Configuration.BAN_PERMISSION;
                c.alreadyPunishedMessage = Language.Punishments.TempBan.ALREADY_PUNISHED;
                c.kickMessage = Language.Punishments.TempBan.MESSAGE;
                c.invalidDurationMessage = Language.Punishments.INVALID_DURATION;
                c.tooLongDurationMessage = Language.Punishments.TOO_LONG_DURATION;
            })
        );

        Bukkit.getCommandMap().register("smp",
            new PunishmentCommand("unmute", Configuration.UNMUTE_PERMISSION, PunishmentType.MUTE).configure(c -> {
                c.usage = Language.Punishments.UnMute.USAGE;
                c.failedToLoadProfileMessage = Language.Punishments.FAILED_TO_LOAD_TARGET_PROFILE;
                c.broadcastMessage = Language.Punishments.UnMute.BROADCAST;
                c.defaultReason = Language.Punishments.UnMute.DEFAULT_REASON;
                c.notPunishedMessage = Language.Punishments.UnMute.NOT_PUNISHED;
            })
        );
        Bukkit.getCommandMap().register("smp",
            new PunishmentCommand("unban", Configuration.UNBAN_PERMISSION, PunishmentType.BAN).configure(c -> {
                c.usage = Language.Punishments.UnBan.USAGE;
                c.failedToLoadProfileMessage = Language.Punishments.FAILED_TO_LOAD_TARGET_PROFILE;
                c.broadcastMessage = Language.Punishments.UnBan.BROADCAST;
                c.defaultReason = Language.Punishments.UnBan.DEFAULT_REASON;
                c.notPunishedMessage = Language.Punishments.UnBan.NOT_PUNISHED;
            })
        );
    }
}