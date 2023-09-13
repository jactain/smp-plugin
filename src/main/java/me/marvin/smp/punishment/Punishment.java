package me.marvin.smp.punishment;

import me.marvin.smp.utils.DurationParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.regex.Pattern;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

/**
 * Represents a punishment.
 */
public class Punishment {
    public static final UUID CONSOLE = new UUID(0, 0);
    public static final int NOT_SET = -1;
    public static final long PERMANENT = -1;

    private int id;
    private boolean active;
    private PunishmentType type;

    private UUID target;
    private UUID issuer;
    private long issuedOn;
    private String issueReason;
    private long issuedUntil;

    private UUID liftedBy;
    private long liftedOn;
    private String liftReason;

    public Punishment() {
        this.id = NOT_SET;
        this.active = true;
        this.type = null;
        this.issuer = null;
        this.target = null;
        this.issuedOn = NOT_SET;
        this.issueReason = null;
        this.issuedUntil = NOT_SET;
        this.liftedOn = NOT_SET;
        this.liftReason = null;
        this.liftedBy = null;
    }

    public int id() {
        return id;
    }

    public Punishment id(int id) {
        this.id = id;
        return this;
    }

    public boolean active() {
        return active;
    }

    public Punishment active(boolean active) {
        this.active = active;
        return this;
    }

    public PunishmentType type() {
        return type;
    }

    public Punishment type(PunishmentType type) {
        this.type = type;
        return this;
    }

    public UUID target() {
        return target;
    }

    public Punishment target(UUID target) {
        this.target = target;
        return this;
    }

    public UUID issuer() {
        return issuer;
    }

    public Punishment issuer(UUID issuer) {
        this.issuer = issuer;
        return this;
    }

    public long issuedOn() {
        return issuedOn;
    }

    public Punishment issuedOn(long issuedOn) {
        this.issuedOn = issuedOn;
        return this;
    }

    public String issueReason() {
        return issueReason;
    }

    public Punishment issueReason(String issueReason) {
        this.issueReason = issueReason;
        return this;
    }

    public long issuedUntil() {
        return issuedUntil;
    }

    public Punishment issuedUntil(long issuedUntil) {
        this.issuedUntil = issuedUntil;
        return this;
    }

    public UUID liftedBy() {
        return liftedBy;
    }

    public Punishment liftedBy(UUID liftedBy) {
        this.liftedBy = liftedBy;
        return this;
    }

    public long liftedOn() {
        return liftedOn;
    }

    public Punishment liftedOn(long liftedOn) {
        this.liftedOn = liftedOn;
        return this;
    }

    public String liftReason() {
        return liftReason;
    }

    public Punishment liftReason(String liftReason) {
        this.liftReason = liftReason;
        return this;
    }

    public long remaining() {
        return issuedUntil == PERMANENT ? PERMANENT : Math.max(0, issuedUntil - System.currentTimeMillis());
    }

    public boolean hasExpired() {
        return type.canBeTemporary() && active && issuedUntil != PERMANENT && System.currentTimeMillis() >= issuedUntil;
    }

    public Component transform(Component component, Function<UUID, String> nameResolver, Function<Long, String> dateFormatter) {
        return component
            .replaceText(b -> b.matchLiteral("%id%").replacement(text(id)))
            .replaceText(b -> b.matchLiteral("%active%").replacement(text(active)))
            .replaceText(b -> b.matchLiteral("%type%").replacement(text(type.name())))
            .replaceText(b -> b.matchLiteral("%target%").replacement(text(nameResolver.apply(target))))
            .replaceText(b -> b.matchLiteral("%issuer%").replacement(text(nameResolver.apply(issuer))))
            .replaceText(b -> b.matchLiteral("%issuedOn%").replacement(text(dateFormatter.apply(issuedOn))))
            .replaceText(b -> b.matchLiteral("%issueReason%").replacement(text(issueReason)))
            .replaceText(b -> b.matchLiteral("%issuedUntil%").replacement(text(dateFormatter.apply(issuedUntil))))
            .replaceText(b -> b.matchLiteral("%liftedBy%").replacement(text(nameResolver.apply(liftedBy))))
            .replaceText(b -> b.matchLiteral("%liftedOn%").replacement(text(dateFormatter.apply(liftedOn))))
            .replaceText(b -> b.matchLiteral("%liftReason%").replacement(liftReason != null ? text(liftReason) : empty()))
            .replaceText(b -> b.matchLiteral("%remaining%").replacement(DurationParser.parse(remaining())));
    }
}
