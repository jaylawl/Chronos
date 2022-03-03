package de.jaylawl.chronos.time.override;

import de.jaylawl.chronos.Chronos;
import de.jaylawl.chronos.util.InvalidBuilderException;
import de.jaylawl.chronos.util.WorldAdapter;
import io.papermc.paper.event.world.WorldGameRuleChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class WorldTimeOverride {

    public static class Builder {

        public WorldTimeOverrideType type = null;
        public String worldName = null;

        // CustomTimePassage:
        public Integer ticksPerCycle = null;

        // FrozenTime:

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
                    }
                    default -> {
                        problems.add("Unhandled case of " + this.type.getClass().getSimpleName() + " in #build()");
                    }
                }
            }

            if (this.type != null) {
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
                WorldTimeOverride.this.worldLoaded = (Bukkit.getWorld(WorldTimeOverride.this.worldAdapter.worldName()) != null);
            }
        }.runTaskLater(Chronos.getInstance(), 1L);
    }

    public void onWorldGameRuleChange(@NotNull WorldGameRuleChangeEvent event) {
    }

    public void onTimeSkip(@NotNull TimeSkipEvent event) {
    }

}
