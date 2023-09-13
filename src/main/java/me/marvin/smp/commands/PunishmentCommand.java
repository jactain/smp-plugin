package me.marvin.smp.commands;

import me.marvin.smp.Bootstrap;
import me.marvin.smp.Configuration;
import me.marvin.smp.Language;
import me.marvin.smp.profile.Profile;
import me.marvin.smp.punishment.Punishment;
import me.marvin.smp.punishment.PunishmentType;
import me.marvin.smp.utils.DurationParser;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PunishmentCommand extends Command {
    protected final PunishmentType type;
    public Component usage;
    public Component failedToLoadProfileMessage;
    public Component invalidTargetMessage;
    public Component invalidDurationMessage;
    public Component tooLongDurationMessage;
    public Component alreadyPunishedMessage;
    public Component notPunishedMessage;
    public Component broadcastMessage;
    public Component kickMessage;
    public String defaultReason;
    public String bypassPermission;

    public PunishmentCommand(@NotNull String name, @NotNull String permission, @NotNull PunishmentType type, @NotNull String... aliases) {
        super(name, "", "", List.of(aliases));
        this.type = type;
        setPermission(permission);
        permissionMessage(Language.General.NO_PERMISSION);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        boolean isTempCommand = invalidDurationMessage != null;
        boolean isLifting = notPunishedMessage != null;

        if (args.length < (isTempCommand ? 2 : 1)) {
            sender.sendMessage(usage);
            return false;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (invalidTargetMessage != null && !target.isOnline()) {
            sender.sendMessage(invalidTargetMessage);
            return false;
        }

        String reason;
        if (args.length > (isTempCommand ? 2 : 1)) {
            reason = String.join(" ", args).split(" ", (isTempCommand ? 3 : 2))[(isTempCommand ? 2 : 1)];
        } else {
            reason = defaultReason;
        }

        long duration = Punishment.PERMANENT;
        if (isTempCommand) {
            duration = DurationParser.parse(args[1]);

            if (duration == 0) {
                sender.sendMessage(invalidDurationMessage);
                return false;
            }

            long maxLimit = Configuration.getMaxLimit(sender, type, bypassPermission);
            if (maxLimit != Punishment.PERMANENT && duration > maxLimit) {
                sender.sendMessage(tooLongDurationMessage);
                return false;
            }

            duration += System.currentTimeMillis();
        }

        Punishment active = null;
        try {
            if (failedToLoadProfileMessage != null) {
                active = Bootstrap.INSTANCE.punishmentProvider().findActive(target.getUniqueId(), type);
            }
        } catch (Exception e) {
            sender.sendMessage(failedToLoadProfileMessage);
            return false;
        }

        if (!isLifting && active != null) { // TODO: should we do automatic punishment overriding?
            sender.sendMessage(alreadyPunishedMessage);
            return false;
        }

        if (isLifting && active == null) {
            sender.sendMessage(notPunishedMessage);
            return false;
        }

        Punishment punishment = isLifting ? active : new Punishment()
            .active(true)
            .type(type)
            .target(target.getUniqueId())
            .issuer(sender instanceof Player player ? player.getUniqueId() : Punishment.CONSOLE)
            .issuedOn(System.currentTimeMillis())
            .issueReason(reason)
            .issuedUntil(duration);

        Bukkit.getScheduler().runTaskAsynchronously(Bootstrap.INSTANCE, () -> {
            try {
                Profile profile = Bootstrap.INSTANCE.profileHandler().profiles().get(target.getUniqueId());

                if (!isLifting) {
                    Bootstrap.INSTANCE.punishmentProvider().issuePunishment(punishment);

                    if (profile != null) {
                        profile.punishments().computeIfAbsent(type, __ -> new ArrayList<>()).add(punishment);
                    }

                    Bukkit.getScheduler().runTask(Bootstrap.INSTANCE, () -> {
                        Player player = Bukkit.getPlayer(args[0]);
                        if (player != null && player.isOnline() && kickMessage != null) {
                            player.kick(punishment.transform(kickMessage, Configuration.NAME_FUNCTION, Configuration.DATE_FUNCTION));
                        }
                    });

                    Bukkit.broadcast(punishment.transform(broadcastMessage, Configuration.NAME_FUNCTION, Configuration.DATE_FUNCTION));
                } else {
                    punishment
                        .active(false)
                        .liftedBy(sender instanceof Player player ? player.getUniqueId() : Punishment.CONSOLE)
                        .liftReason(reason)
                        .liftedOn(System.currentTimeMillis());

                    Bootstrap.INSTANCE.punishmentProvider().liftPunishment(punishment);

                    if (profile != null) {
                        profile.punishments().getOrDefault(type, List.of()).stream()
                            .filter(p -> p.id() == punishment.id())
                            .findFirst()
                            .ifPresent(p -> p.active(false));
                    }

                    Bukkit.broadcast(punishment.transform(broadcastMessage, Configuration.NAME_FUNCTION, Configuration.DATE_FUNCTION));
                }
            } catch (Exception ex) {
                sender.sendMessage(ex.getMessage());
                ex.printStackTrace();
            }
        });

        return true;
    }

    public PunishmentCommand configure(Consumer<PunishmentCommand> consumer) {
        consumer.accept(this);
        return this;
    }
}
