package de.jaylawl.chronos.util;

import de.jaylawl.chronos.Chronos;
import de.jaylawl.chronos.time.WorldTimeManager;
import de.jaylawl.chronos.time.override.WorldTimeOverride;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ReloadScript extends IReloadScript {

    private final Chronos chronos;
    private final WorldTimeManager worldTimeManager;

    public ReloadScript() {
        super(Chronos.getInstance());
        this.chronos = (Chronos) this.pluginInstance;
        this.worldTimeManager = this.chronos.getWorldTimeManager();
    }

    //

    @Override
    public void initialSyncTasks() {
        this.worldTimeManager.clear();
    }

    @Override
    public void asyncTasks() {
    }

    @Override
    public void finalSyncTasks() {

        File worldTimeOverridesFile = new File(this.chronos.getDataFolder() + "/worldtimeoverrides.yml");
        if (!worldTimeOverridesFile.exists() || !worldTimeOverridesFile.isFile()) {
            this.logger.info("Unable to find file \"" + worldTimeOverridesFile + "\"");
            this.logger.info("Trying to create it...");

            File dataFolder = worldTimeOverridesFile.getParentFile();
            boolean dataFolderExists = true;
            if (!dataFolder.exists() || !dataFolder.isDirectory()) {
                this.logger.info("Data folder of plugin is missing");
                if (!dataFolder.mkdirs()) {
                    this.logger.warning("Failed to create data folder of plugin");
                    dataFolderExists = false;
                } else {
                    this.logger.info("Created data folder of plugin");
                }
            }

            if (dataFolderExists) {
                try {
                    if (!worldTimeOverridesFile.createNewFile()) {
                        this.logger.warning("Failed to create file \"" + worldTimeOverridesFile + "\" because it already existed");
                    } else {
                        this.logger.info("Created file \"" + worldTimeOverridesFile + "\"");
                    }
                } catch (IOException exception) {
                    this.logger.warning("Exception was thrown while trying to create file \"" + worldTimeOverridesFile + "\"");
                    exception.printStackTrace();
                }
            }

        } else {
            YamlConfiguration yaml = new YamlConfiguration();
            try {
                yaml.load(worldTimeOverridesFile);
            } catch (IOException | InvalidConfigurationException exception) {
                exception.printStackTrace();
            }
            this.totalWarnings += this.worldTimeManager.loadFrom(yaml);
        }

    }

    //

    @Override
    public void finish() {

        this.logger.info("Reload completed within " + this.elapsedSeconds + " s. and with " + this.totalWarnings + " warning(s)");
        WorldTimeOverride[] worldTimeOverrides = this.worldTimeManager.getWorldTimeOverrides();
        if (worldTimeOverrides.length == 0) {
            this.logger.info("No world time overrides were loaded");
        } else {
            for (WorldTimeOverride worldTimeOverride : worldTimeOverrides) {
                this.logger.info("Loaded world time override of type \"" + worldTimeOverride.getType() + "\" for world \"" + worldTimeOverride.getWorldAdapter().worldName() + "\"");
                worldTimeOverride.worldLoadCheck();
                if (worldTimeOverride.isWorldLoaded()) {
                    worldTimeOverride.applyToWorld();
                }
            }
        }

        notifySubscribers(getSubscriberNotification());
    }

}
