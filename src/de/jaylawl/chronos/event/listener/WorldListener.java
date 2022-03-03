package de.jaylawl.chronos.event.listener;

import de.jaylawl.chronos.time.WorldTimeManager;
import io.papermc.paper.event.world.WorldGameRuleChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jetbrains.annotations.NotNull;

public class WorldListener implements Listener {

    private final WorldTimeManager worldTimeManager;

    public WorldListener(@NotNull WorldTimeManager worldTimeManager) {
        this.worldTimeManager = worldTimeManager;
    }

    //

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldLoad(@NotNull WorldLoadEvent event) {
        this.worldTimeManager.getWorldTimeOverride(event.getWorld())
                .ifPresent(worldTimeOverride -> worldTimeOverride.onWorldLoad(event));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldUnload(@NotNull WorldUnloadEvent event) {
        this.worldTimeManager.getWorldTimeOverride(event.getWorld())
                .ifPresent(worldTimeOverride -> worldTimeOverride.onWorldUnload(event));

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldGameRuleChange(@NotNull WorldGameRuleChangeEvent event) {
        this.worldTimeManager.getWorldTimeOverride(event.getWorld())
                .ifPresent(worldTimeOverride -> worldTimeOverride.onWorldGameRuleChange(event));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTimeSkip(@NotNull TimeSkipEvent event) {
        this.worldTimeManager.getWorldTimeOverride(event.getWorld())
                .ifPresent(worldTimeOverride -> worldTimeOverride.onTimeSkip(event));
    }

}
