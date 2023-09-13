package me.marvin.smp;

import me.marvin.smp.utils.config.Config;
import me.marvin.smp.utils.config.ConfigData;
import me.marvin.smp.utils.config.Value;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;

@Config(value = "language")
public class Language {
    public static ConfigData DATA = null;

    @Value("general")
    public static class General {
        @Value("no-permission")
        public static Component NO_PERMISSION = text("Ehhez nincs jogod.")
            .color(NamedTextColor.RED);

        @Value("failed-to-load-profile")
        public static Component FAILED_TO_LOAD_PROFILE = text("Nem sikerült betölteni a profilodat.").append(newline())
            .append(text("Kérlek próbálj meg belépni később!")).append(newline())
            .append(text("Ha a hiba továbbra is fennáll, keress fel egy admint!")).append(newline())
            .color(NamedTextColor.RED);
    }

    @Value("punishments")
    public static class Punishments {
        @Value("offline-target")
        public static Component OFFLINE_TARGET = text("Ez a játékos nem elérhető.")
            .color(NamedTextColor.RED);

        @Value("invalid-duration-format")
        public static Component INVALID_DURATION = text("A megadott időtartam helytelen. Példa: (3w3d = 3 hét és 3 nap).")
            .color(NamedTextColor.RED);

        @Value("too-long-duration")
        public static Component TOO_LONG_DURATION = text("A megadott időtartam túl hosszú.")
            .color(NamedTextColor.RED);

        @Value("failed-to-load-target-profile")
        public static Component FAILED_TO_LOAD_TARGET_PROFILE = text("Nem sikerült betölteni a játékos profilját.")
            .color(NamedTextColor.RED);

        @Value("kick")
        public static class Kick {
            @Value("default-reason")
            public static String DEFAULT_REASON = "Nincs megadva.";
            @Value("usage")
            public static Component USAGE = text("Használat: /kick <játékos> [indok]");
            @Value("broadcast")
            public static Component BROADCAST = text("%target% ki lett rúgva %issuer% által.");
            @Value("message")
            public static Component MESSAGE = text("Ki lettél rúgva %issuer% által!").append(newline())
                .append(text("Indok: %reason%"));
        }

        @Value("mute")
        public static class Mute {
            @Value("default-reason")
            public static String DEFAULT_REASON = "Nincs megadva.";
            @Value("usage")
            public static Component USAGE = text("Használat: /mute <játékos> [indok]");
            @Value("already-punished")
            public static Component ALREADY_PUNISHED = text("Ez a játékos már le van némítva.");
            @Value("broadcast")
            public static Component BROADCAST = text("%target% le lett némítva %issuer% által.");
            @Value("message")
            public static Component MESSAGE = text("Le lettél némítva!").append(newline())
                .append(text("Indok: %issueReason%")).append(newline())
                .append(text("Ez a némítás nem fog lejárni."));
        }

        @Value("unmute")
        public static class UnMute {
            @Value("default-reason")
            public static String DEFAULT_REASON = "Nincs megadva.";
            @Value("usage")
            public static Component USAGE = text("Használat: /unmute <játékos> [indok]");
            @Value("not-punished")
            public static Component NOT_PUNISHED = text("Ez a játékos nincs lenémítva.");
            @Value("broadcast")
            public static Component BROADCAST = text("%target% némítása fel lett oldva %liftedBy% által.");
        }

        @Value("tempmute")
        public static class TempMute {
            @Value("default-reason")
            public static String DEFAULT_REASON = "Nincs megadva.";
            @Value("usage")
            public static Component USAGE = text("Használat: /tempmute <játékos> <időtartam> [indok]");
            @Value("already-punished")
            public static Component ALREADY_PUNISHED = text("Ez a játékos már le van némítva.");
            @Value("broadcast")
            public static Component BROADCAST = text("%target% le lett némítva ideiglenesen %issuer% által.");
            @Value("message")
            public static Component MESSAGE = text("Le lettél némítva ideiglenesen!").append(newline())
                .append(text("Indok: %issueReason%")).append(newline())
                .append(text("Hátralévő idő: %remaining%"));
        }

        @Value("ban")
        public static class Ban {
            @Value("default-reason")
            public static String DEFAULT_REASON = "Nincs megadva.";
            @Value("usage")
            public static Component USAGE = text("Használat: /ban <játékos> [indok]");
            @Value("already-punished")
            public static Component ALREADY_PUNISHED = text("Ez a játékos már ki van tiltva.");
            @Value("broadcast")
            public static Component BROADCAST = text("%target% ki lett tiltva %issuer% által.");
            @Value("message")
            public static Component MESSAGE = text("Ki lettél tiltva!").append(newline())
                .append(text("Kitiltott: %issuer%")).append(newline())
                .append(text("Indok: %issueReason%")).append(newline())
                .append(text("Ez a kitiltás nem fog lejárni."));
        }

        @Value("unban")
        public static class UnBan {
            @Value("default-reason")
            public static String DEFAULT_REASON = "Nincs megadva.";
            @Value("usage")
            public static Component USAGE = text("Használat: /unban <játékos> [indok]");
            @Value("not-punished")
            public static Component NOT_PUNISHED = text("Ez a játékos nincs kitiltva.");
            @Value("broadcast")
            public static Component BROADCAST = text("%target% kitiltása fel lett oldva %liftedBy% által.");
        }

        @Value("tempban")
        public static class TempBan {
            @Value("default-reason")
            public static String DEFAULT_REASON = "Nincs megadva.";
            @Value("usage")
            public static Component USAGE = text("Használat: /tempban <játékos> <időtartam> [indok]");
            @Value("already-punished")
            public static Component ALREADY_PUNISHED = text("Ez a játékos már ki van tiltva.");
            @Value("broadcast")
            public static Component BROADCAST = text("%target% ki lett tiltva ideiglenesen %issuer% által.");
            @Value("message")
            public static Component MESSAGE = text("Ki lettél tiltva ideiglenesen!").append(newline())
                .append(text("Kitiltott: %issuer%")).append(newline())
                .append(text("Indok: %issueReason%")).append(newline())
                .append(text("Hátralévő idő: %remaining%"));
        }
    }
}
