package de.jaylawl.chronos.time.override;

import de.jaylawl.chronos.Chronos;
import de.jaylawl.chronos.util.InvalidBuilderException;
import de.jaylawl.chronos.util.WorldAdapter;
import io.papermc.paper.event.world.WorldGameRuleChangeEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class WorldTimeOverride {

    public static short MINECRAFT_DAYLIGHT_CYCLE_TICKS = 24000;

    public static class Builder {

        public WorldTimeOverrideType type = null;
        public String worldName = null;

        // CustomSpeed:
        public Integer ticksPerCycle = null;

        // Frozen:
        public Integer freezeTime = null;

        // SyncedRealTime

        public Builder() {
        }

        //

        public @NotNull WorldTimeOverride build() throws InvalidBuilderException {
            final List<String> problems = new ArrayList<>();

            InvalidBuilderException.validate(this.worldName != null, "World must not be null", problems);

            if (this.type == null) {
                problems.add("Type must not be null");
            } else {
                switch (this.type) {
                    case CUSTOM_SPEED -> {
                        if (this.ticksPerCycle == null) {
                            problems.add("Ticks per cycle must not be null");
                        } else {
                            InvalidBuilderException.validate(this.ticksPerCycle > 0, "Ticks per cycle must be >0", problems);
                        }
                    }
                    case FROZEN -> {
                        if (this.freezeTime == null) {
                            problems.add("Freeze time must not be null");
                        } else {
                            InvalidBuilderException.validate(this.freezeTime >= 0, "Freeze time must be >= 0", problems);
                            InvalidBuilderException.validate(this.freezeTime < WorldTimeOverride.MINECRAFT_DAYLIGHT_CYCLE_TICKS, "Freeze time must be < " + WorldTimeOverride.MINECRAFT_DAYLIGHT_CYCLE_TICKS, problems);
                        }
                    }
                    case SYNCED_REAL_TIME -> {
                        problems.add(this.type + " is not yet implemented");
                    }
                    default -> {
                        problems.add("Unhandled case of " + this.type.getClass().getSimpleName() + " in #build()");
                    }
                }
            }

            if (problems.isEmpty()) {
                try {
                    return this.type.getClazz().getDeclaredConstructor(WorldTimeOverride.Builder.class).newInstance(this);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                    problems.add(exception.getMessage());
                }
            }
            throw new InvalidBuilderException("Unable to instantiate " + WorldTimeOverride.class.getSimpleName() + " from builder", problems);
        }

    }

    private final WorldTimeOverrideType type;
    private final WorldAdapter worldAdapter;
    private boolean worldLoaded;

    public WorldTimeOverride(@NotNull Builder builder) {
        this.type = builder.type;
        this.worldAdapter = new WorldAdapter(builder.worldName);
        this.worldLoaded = this.worldAdapter.getWorld() != null;
    }

    //

    public final @NotNull WorldTimeOverrideType getType() {
        return this.type;
    }

    public @NotNull WorldAdapter getWorldAdapter() {
        return this.worldAdapter;
    }

    public void worldLoadCheck() {
        this.worldLoaded = this.worldAdapter.getWorld() != null;
    }

    public boolean isWorldLoaded() {
        return this.worldLoaded;
    }

    public void onWorldLoad(@NotNull WorldLoadEvent event) {
        this.worldLoaded = true;
    }

    public void onWorldUnload(@NotNull WorldUnloadEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                worldLoadCheck();
            }
        }.runTaskLater(Chronos.getInstance(), 1L);
    }

    public void onWorldGameRuleChange(@NotNull WorldGameRuleChangeEvent event) {
    }

    public void onTimeSkip(@NotNull TimeSkipEvent event) {
    }

    public abstract void applyToWorld();

}
