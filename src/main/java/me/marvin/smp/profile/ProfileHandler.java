package me.marvin.smp.profile;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a profile handler.
 */
public class ProfileHandler {
    private final Map<UUID, Profile> profiles;

    public ProfileHandler() {
        this.profiles = new HashMap<>();
    }

    /**
     * Returns the profiles held by this handler.
     *
     * @return the profiles
     */
    @NotNull
    public Map<UUID, Profile> profiles() {
        return profiles;
    }
}
