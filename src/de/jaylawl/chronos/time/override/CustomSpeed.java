package de.jaylawl.chronos.time.override;

import de.jaylawl.chronos.util.Tickable;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.world.WorldLoadEvent;
import org.jetbrains.annotations.NotNull;

public class CustomSpeed extends WorldTimeOverride implements Tickable {

    private final int ticksPerCycle;
    private final int cooldownPeriod;
    private int cooldownTicks;

    protected CustomSpeed(@NotNull Builder builder) {
        super(builder);
        this.ticksPerCycle = builder.ticksPerCycle;
        this.cooldownPeriod = Math.max(0, (this.ticksPerCycle / WorldTimeOverride.MINECRAFT_DAYLIGHT_CYCLE_TICKS) - 1); // wtf is this formula?
        this.cooldownTicks = this.cooldownPeriod;
    }

    //

    public int getTicksPerCycle() {
        return this.ticksPerCycle;
    }

    public int getCooldownPeriod() {
        return this.cooldownPeriod;
    }

    public int getCooldownTicks() {
        return this.cooldownTicks;
    }

    @Override
    public void tick() {
        if (this.cooldownTicks > 0) {
            this.cooldownTicks--;
            return;
        } else {
            this.cooldownTicks = this.cooldownPeriod;
        }

        if (!isWorldLoaded()) {
            return;
        }

        final World world = getWorldAdapter().getWorld();
        assert world != null;
        long worldTime = world.getTime();
        worldTime++;
        worldTime %= WorldTimeOverride.MINECRAFT_DAYLIGHT_CYCLE_TICKS;
        world.setTime(worldTime);
    }

    @Override
    public void applyToWorld() {
        if (!isWorldLoaded()) {
            return;
        }
        World world = getWorldAdapter().getWorld();
        assert world != null;
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
    }

    @Override
    public void onWorldLoad(@NotNull WorldLoadEvent event) {
        super.onWorldLoad(event);
        applyToWorld();
    }

}
