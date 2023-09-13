package me.marvin.smp.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.marvin.smp.Bootstrap;
import me.marvin.smp.Configuration;
import me.marvin.smp.Language;
import me.marvin.smp.profile.Profile;
import me.marvin.smp.punishment.Punishment;
import me.marvin.smp.punishment.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.regex.Pattern;

public class ChatListener implements Listener {
    private static final int MAX_REPLACED_EMOJIS = 10;
    private static final Pattern PATTERN = Pattern.compile(":([a-zA-Z0-9]+):"); // TODO: escape sequence support

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMute(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = Bootstrap.INSTANCE.profileHandler().profiles().get(player.getUniqueId());
        List<Punishment> punishments = profile.punishments().get(PunishmentType.MUTE);

        if (punishments != null) {
            punishments.stream().filter(Punishment::active).findFirst().ifPresent(mute -> {
                if (!mute.hasExpired()) {
                    if (mute.issuedUntil() != Punishment.PERMANENT) {
                        player.sendMessage(mute.transform(Language.Punishments.TempMute.MESSAGE, Configuration.NAME_FUNCTION, Configuration.DATE_FUNCTION));
                    } else {
                        player.sendMessage(mute.transform(Language.Punishments.Mute.MESSAGE, Configuration.NAME_FUNCTION, Configuration.DATE_FUNCTION));
                    }

                    event.setCancelled(true);
                } else {
                    mute.active(false);

                    Bukkit.getScheduler().runTaskAsynchronously(Bootstrap.INSTANCE, () -> {
                        try {
                            Bootstrap.INSTANCE.punishmentProvider().liftPunishment(mute);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            });
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        // TODO: emoji replacer
    }
}
