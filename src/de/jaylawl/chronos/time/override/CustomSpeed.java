package de.jaylawl.chronos.time.override;

import de.jaylawl.chronos.util.Tickable;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class CustomSpeed extends WorldTimeOverride implements Tickable {

    private final int ticksPerCycle;
    private final int cooldownPeriod;
    private int cooldownTicks;

    protected CustomSpeed(@NotNull Builder builder) {
        super(builder);
        this.ticksPerCycle = builder.ticksPerCycle;
        this.cooldownPeriod = Math.max(0, (this.ticksPerCycle / 24000) - 1); // wtf is this formula?
        this.cooldownTicks = this.cooldownPeriod;
    }

    //

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
        worldTime %= 24000L;
        world.setTime(worldTime);
    }

}
