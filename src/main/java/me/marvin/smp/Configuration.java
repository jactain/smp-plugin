package me.marvin.smp;

import me.marvin.smp.punishment.Punishment;
import me.marvin.smp.punishment.PunishmentType;
import me.marvin.smp.utils.DurationParser;
import me.marvin.smp.utils.config.Config;
import me.marvin.smp.utils.config.ConfigData;
import me.marvin.smp.utils.config.Initialize;
import me.marvin.smp.utils.config.Value;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

@Config("config")
public class Configuration {
    public static ConfigData DATA = null;

    public static final String KICK_PERMISSION = "smp.kick";

    public static final String BAN_PERMISSION = "smp.ban";
    public static final String TEMPBAN_PERMISSION = "smp.tempban";
    public static final String UNBAN_PERMISSION = "smp.unban";

    public static final String MUTE_PERMISSION = "smp.mute";
    public static final String TEMPMUTE_PERMISSION = "smp.tempmute";
    public static final String UNMUTE_PERMISSION = "smp.unmute";

    public static final Function<Long, String> DATE_FUNCTION = new Function<>() {
        private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm", new Locale("hu"));

        @Override
        public String apply(Long l) {
            if (l != null && l > 0) {
                return FORMAT.format(new Date(l));
            }

            return "-";
        }
    };

    public static final Function<UUID, String> NAME_FUNCTION = u -> {
        if (u != null) {
            if (Punishment.CONSOLE.equals(u)) {
                return "~CONSOLE";
            }

            Player player = Bukkit.getPlayer(u);

            if (player != null && player.isOnline()) {
                return player.getName();
            } else {
                return Bukkit.getOfflinePlayer(u).getName();
            }
        }

        return "-";
    };

    @Value("database")
    public static class Database {
        @Value("type")
        public static String TYPE = "H2";

        @Value("punishments-table")
        public static String PUNISHMENTS_TABLE = "punishments";

        @Value("sql")
        public static class Sql {
            @Value("host")
            public static String HOST = "127.0.0.1";
            @Value("port")
            public static int PORT = 3306;
            @Value("pool-size")
            public static int POOL_SIZE = 2;
            @Value("user")
            public static String USER = "root";
            @Value("password")
            public static String PASSWORD = "";
            @Value("database")
            public static String DATABASE = "database";
        }
    }

    @Value("temporary-limits.prefix")
    public static String PERMISSION_PREFIX = "smp.";

    private static final Map<String, Map<PunishmentType, Long>> LIMIT_MAP = new HashMap<>();
    private static final String PERMISSIONS = "temporary-limits.permissions";

    /**
     * Returns the max duration per punishment type for the given {@link Permissible permissible}.
     *
     * @param permissible the permissible
     * @param type the type
     * @param bypass the bypass permission
     * @return the max duration or {@link Punishment#PERMANENT}
     */
    public static long getMaxLimit(Permissible permissible, PunishmentType type, String bypass) {
        long max = Punishment.PERMANENT;

        if (permissible.hasPermission(bypass)) {
            return max;
        }

        for (Map.Entry<String, Map<PunishmentType, Long>> e : LIMIT_MAP.entrySet()) {
            if (permissible.hasPermission(e.getKey())) {
                max = Math.max(max, e.getValue().getOrDefault(type, Punishment.PERMANENT));
            }
        }

        return max;
    }

    @Initialize
    public static void loadLimits() {
        ConfigurationSection section = DATA.configuration().getConfigurationSection(PERMISSIONS);
        assert section != null;
        section.getKeys(false)
            .forEach(key -> {
                Map<PunishmentType, Long> limits = LIMIT_MAP.computeIfAbsent(PERMISSION_PREFIX + key, __ -> new EnumMap<>(PunishmentType.class));
                for (PunishmentType value : PunishmentType.values()) {
                    if (!value.canBeTemporary()) {
                        continue;
                    }

                    String path = "%s.%s".formatted(key, value.name());
                    if (section.contains(path)) {
                        //noinspection ConstantConditions - check above
                        limits.put(value, DurationParser.parse(section.getString(path)));
                    }
                }
            });
    }
}
