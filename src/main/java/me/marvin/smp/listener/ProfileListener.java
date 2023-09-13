package me.marvin.smp.listener;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import me.marvin.smp.Bootstrap;
import me.marvin.smp.Configuration;
import me.marvin.smp.Language;
import me.marvin.smp.profile.Profile;
import me.marvin.smp.profile.ProfileHandler;
import me.marvin.smp.punishment.Punishment;
import me.marvin.smp.punishment.PunishmentProvider;
import me.marvin.smp.punishment.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.stream.Collectors;

public class ProfileListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        PunishmentProvider provider = Bootstrap.INSTANCE.punishmentProvider();
        ProfileHandler profiles = Bootstrap.INSTANCE.profileHandler();

        try {
            Punishment ban = provider.findActive(event.getUniqueId(), PunishmentType.BAN);

            if (ban != null) {
                if (!ban.hasExpired()) {
                    if (ban.issuedUntil() != Punishment.PERMANENT) {
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ban.transform(Language.Punishments.TempBan.MESSAGE, Configuration.NAME_FUNCTION, Configuration.DATE_FUNCTION));
                    } else {
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ban.transform(Language.Punishments.Ban.MESSAGE, Configuration.NAME_FUNCTION, Configuration.DATE_FUNCTION));
                    }

                    return;
                } else {
                    ban.active(false);

                    Bukkit.getScheduler().runTaskAsynchronously(Bootstrap.INSTANCE, () -> {
                        try {
                            provider.liftPunishment(ban);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            }

            Profile profile = new Profile(
                event.getUniqueId(),
                provider.getPunishments(event.getUniqueId()).stream()
                    .collect(Collectors.<Punishment, PunishmentType>groupingBy(Punishment::type))
            );
            profiles.profiles().put(event.getUniqueId(), profile);
        } catch (Exception ex) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Language.General.FAILED_TO_LOAD_PROFILE);
            profiles.profiles().remove(event.getUniqueId());
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onClose(PlayerConnectionCloseEvent event) {
        Bootstrap.INSTANCE.profileHandler().profiles().remove(event.getPlayerUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Bootstrap.INSTANCE.profileHandler().profiles().remove(event.getPlayer().getUniqueId());
    }
}
