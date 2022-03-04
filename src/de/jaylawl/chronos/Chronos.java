package de.jaylawl.chronos;

import de.jaylawl.chronos.command.CommandMaster;
import de.jaylawl.chronos.time.WorldTimeManager;
import de.jaylawl.chronos.util.ReloadScript;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class Chronos extends JavaPlugin {

    private static Chronos INSTANCE;

    private WorldTimeManager worldTimeManager;

    private ReloadScript latestReloadScript = null;

    public Chronos() {
    }

    //

    @Override
    public void onEnable() {

        INSTANCE = this;

        final Logger logger = getLogger();
        final PluginManager pluginManager = getServer().getPluginManager();

        PluginCommand masterCommand = getCommand("chronos");
        if (masterCommand == null) {
            logger.severe("Failed to enable plugins master command");
            logger.severe("Disabling plugin...");
            pluginManager.disablePlugin(this);
            return;
        } else {
            CommandMaster commandMaster = new CommandMaster();
            masterCommand.setExecutor(commandMaster);
            masterCommand.setTabCompleter(commandMaster);
        }

        this.worldTimeManager = new WorldTimeManager();

        if (getServer().getScheduler().scheduleSyncRepeatingTask(this, this::clock, 1L, 1L) == -1) {
            logger.severe("Failed to start the plugin's main heartbeat task");
            logger.severe("Disabling plugin...");
            pluginManager.disablePlugin(this);
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!reload()) {
                    logger.warning("Automated initial reload of plugin data failed. Try running the plugin's reload command manually");
                }
            }
        }.runTaskLater(this, 1L);

    }

    //

    private void clock() {
        this.worldTimeManager.tick();
    }

    public boolean reload() {
        return reload(getServer().getConsoleSender());
    }

    public boolean reload(@NotNull CommandSender issuer) {
        if (this.latestReloadScript != null && !this.latestReloadScript.isConcluded()) {
            if (issuer instanceof Player player) {
                this.latestReloadScript.addSubscriber(player.getUniqueId());
            }
            return false;
        }
        this.latestReloadScript = new ReloadScript();
        if (issuer instanceof Player player) {
            this.latestReloadScript.addSubscriber(player.getUniqueId());
        }
        this.latestReloadScript.run();
        return true;
    }

    //

    public static Chronos getInstance() {
        return INSTANCE;
    }

    public WorldTimeManager getWorldTimeManager() {
        return this.worldTimeManager;
    }

}
