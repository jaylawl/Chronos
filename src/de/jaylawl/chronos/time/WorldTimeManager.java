package de.jaylawl.chronos.time;

import de.jaylawl.chronos.Chronos;
import de.jaylawl.chronos.time.override.WorldTimeOverride;
import de.jaylawl.chronos.time.override.WorldTimeOverrideType;
import de.jaylawl.chronos.util.InvalidBuilderException;
import de.jaylawl.chronos.util.Tickable;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class WorldTimeManager implements Tickable {

    private final ConcurrentHashMap<String, WorldTimeOverride> worldTimeOverrides = new ConcurrentHashMap<>();

    public WorldTimeManager() {
    }

    //

    public void clear() {
        this.worldTimeOverrides.clear();
    }

    public int loadFrom(@NotNull YamlConfiguration yaml) {
        final List<String> warnings = new ArrayList<>();
        int totalWarnings = 0;
        final Logger logger = Chronos.getInstance().getLogger();

        for (final String worldName : yaml.getKeys(false)) {

            WorldTimeOverride worldTimeOverride = null;

            if (this.worldTimeOverrides.get(worldName) != null) {
                warnings.add("A world time override for world \"" + worldName + "\" was already configured");

            } else {
                WorldTimeOverride.Builder builder = new WorldTimeOverride.Builder();

                builder.worldName = worldName;
                String typeString = yaml.getString(worldName + ".Type");
                if (typeString != null) {
                    try {
                        builder.type = WorldTimeOverrideType.valueOf(typeString);
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                if (builder.type != null) {
                    switch (builder.type) {
                        case CUSTOM_SPEED -> {
                            builder.ticksPerCycle = yaml.get(worldName + ".TicksPerCycle") instanceof Integer integer ? integer : null;
                        }
                        case FROZEN -> {
                            builder.freezeTime = yaml.get(worldName + ".FreezeTime") instanceof Integer integer ? integer : null;
                        }
                        case SYNCED_REAL_TIME -> {
                        }
                    }
                }

                try {
                    worldTimeOverride = builder.build();
                } catch (InvalidBuilderException exception) {
                    warnings.addAll(Arrays.stream(exception.getProblems()).toList());
                }

            }

            if (worldTimeOverride == null) {
                logger.warning("Unable to load world time override for world \"" + worldName + "\":");
            } else {
                registerWorldTimeOverride(worldTimeOverride);
                if (!warnings.isEmpty()) {
                    logger.warning("Issue(s) occurred while loading world time override for world \"" + worldName + "\":");
                }
            }
            warnings.forEach(s -> logger.warning("- " + s));
            totalWarnings += warnings.size();
            warnings.clear();
        }

        return totalWarnings;
    }

    public void registerWorldTimeOverride(@NotNull WorldTimeOverride... worldTimeOverrides) {
        for (WorldTimeOverride worldTimeOverride : worldTimeOverrides) {
            this.worldTimeOverrides.put(worldTimeOverride.getWorldAdapter().worldName(), worldTimeOverride);
        }
    }

    public String[] getOverriddenWorldNames() {
        return this.worldTimeOverrides.keySet().toArray(new String[0]);
    }

    public WorldTimeOverride[] getWorldTimeOverrides() {
        return this.worldTimeOverrides.values().toArray(new WorldTimeOverride[0]);
    }

    public Optional<WorldTimeOverride> getWorldTimeOverride(final @NotNull World world) {
        return getWorldTimeOverride(world.getName());
    }

    public Optional<WorldTimeOverride> getWorldTimeOverride(final @NotNull String worldName) {
        return Optional.ofNullable(this.worldTimeOverrides.get(worldName));
    }

    @Override
    public void tick() {
        this.worldTimeOverrides.values().forEach(worldTimeOverride -> {
            if (worldTimeOverride instanceof Tickable tickable) {
                tickable.tick();
            }
        });
    }

}
