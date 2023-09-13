package me.marvin.smp.punishment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

/**
 * Represents a punishment provider.
 */
public interface PunishmentProvider {
    /**
     * Issues the given punishment, then gives back a result.
     *
     * @param punishment the punishment
     * @throws Exception when an error happens during execution
     */
    void issuePunishment(@NotNull Punishment punishment) throws Exception;

    /**
     * Lifts the given punishment, then gives back a result.
     *
     * @param punishment the punishment
     * @throws Exception when an error happens during execution
     */
    void liftPunishment(@NotNull Punishment punishment) throws Exception;

    /**
     * Returns an active punishment with the given type.
     *
     * @param target the target
     * @param type   the type
     * @return an active punishment or {@code null}
     * @throws Exception when an error happens during execution
     */
    @Nullable
    Punishment findActive(@NotNull UUID target, @NotNull PunishmentType type) throws Exception;

    /**
     * Returns all associated punishments for the given {@link UUID}.
     *
     * @param uuid the uuid
     * @return a collection of punishments
     * @throws Exception when an error happens during execution
     */
    @NotNull
    Collection<Punishment> getPunishments(@NotNull UUID uuid) throws Exception;
}
