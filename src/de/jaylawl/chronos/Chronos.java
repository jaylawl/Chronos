package de.jaylawl.chronos;

import de.jaylawl.chronos.time.WorldTimeManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Chronos extends JavaPlugin {

    private static Chronos INSTANCE;

    private WorldTimeManager worldTimeManager;

    public Chronos() {
    }

    //

    @Override
    public void onEnable() {

        INSTANCE = this;

        final Logger logger = getLogger();
        final PluginManager pluginManager = getServer().getPluginManager();

        this.worldTimeManager = new WorldTimeManager();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::clock, 1L, 1L);

    }

    //

    private void clock() {
        this.worldTimeManager.tick();
    }

    //

    public static Chronos getInstance() {
        return INSTANCE;
    }

    public WorldTimeManager getWorldTimeManager() {
        return this.worldTimeManager;
    }

}
