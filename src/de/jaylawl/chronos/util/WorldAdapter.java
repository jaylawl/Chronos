package de.jaylawl.chronos.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Copied from Jaygolf @ 03/03/2022

public record WorldAdapter(String worldName) {

    public WorldAdapter(@NotNull String worldName) {
        this.worldName = worldName;
    }

    //

    public @Nullable World getWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    public boolean isWorldLoaded() {
        return getWorld() != null;
    }

}
