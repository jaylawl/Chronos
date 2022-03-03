package de.jaylawl.chronos.time.override;

import org.jetbrains.annotations.NotNull;

public enum WorldTimeOverrideType {

    CUSTOM_SPEED(CustomSpeed.class),
    FROZEN(Frozen.class),
    SYNCED_REAL_TIME(SyncedRealTime.class);

    private final Class<? extends WorldTimeOverride> clazz;

    WorldTimeOverrideType(@NotNull Class<? extends WorldTimeOverride> clazz) {
        this.clazz = clazz;
    }

    //

    public @NotNull Class<? extends WorldTimeOverride> getClazz() {
        return this.clazz;
    }

}
