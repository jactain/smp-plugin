package me.marvin.smp.punishment;

/**
 * Represents a punishment type.
 */
public enum PunishmentType {
    MUTE(true, true, false, false),
    KICK(false, false, false, true),
    BAN(true, true, true, true);

    private final boolean liftable;
    private final boolean canBeTemporary;
    private final boolean blocksJoin;
    private final boolean disconnectOnIssue;

    PunishmentType(boolean liftable, boolean canBeTemporary, boolean blocksJoin, boolean disconnectOnIssue) {
        this.liftable = liftable;
        this.canBeTemporary = canBeTemporary;
        this.blocksJoin = blocksJoin;
        this.disconnectOnIssue = disconnectOnIssue;
    }

    /**
     * Returns if the punishment is liftable.
     *
     * @return {@code true} if liftable, {@code false} otherwise
     */
    public boolean isLiftable() {
        return liftable;
    }

    /**
     * Returns if the punishment can be temporary.
     *
     * @return {@code true} if can be temporary, {@code false} otherwise
     */
    public boolean canBeTemporary() {
        return canBeTemporary;
    }

    /**
     * Returns if the punishment blocks joining to the server.
     *
     * @return {@code true} if blocks joining, {@code false} otherwise
     */
    public boolean blocksJoin() {
        return blocksJoin;
    }

    /**
     * Returns if the player should be kicked when this punishment is issued.
     *
     * @return {@code true} if the player gets kicked, {@code false} otherwise
     */
    public boolean disconnectOnIssue() {
        return disconnectOnIssue;
    }
}
