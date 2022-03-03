package de.jaylawl.chronos.time;

import de.jaylawl.chronos.time.override.WorldTimeOverride;
import de.jaylawl.chronos.util.Tickable;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class WorldTimeManager implements Tickable {

    private final ConcurrentHashMap<String, WorldTimeOverride> worldTimeOverrides = new ConcurrentHashMap<>();

    public WorldTimeManager() {
    }

    //

    public void clear() {
        this.worldTimeOverrides.clear();
    }

    public void reload() {
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
