package me.marvin.smp.profile;

import me.marvin.smp.punishment.Punishment;
import me.marvin.smp.punishment.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a player profile.
 */
public class Profile {
    private final UUID uuid;
    private final Map<PunishmentType, List<Punishment>> punishments;

    public Profile(UUID uuid, Map<PunishmentType, List<Punishment>> punishments) {
        this.uuid = uuid;
        this.punishments = punishments;
    }

    /**
     * Returns the player of this profile.
     *
     * @return the player
     */
    @Nullable
    public Player player() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Returns the punishments of this profile.
     *
     * @return the punishments
     */
    public Map<PunishmentType, List<Punishment>> punishments() {
        return punishments;
    }
}
