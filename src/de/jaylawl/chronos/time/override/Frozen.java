package de.jaylawl.chronos.time.override;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.world.WorldLoadEvent;
import org.jetbrains.annotations.NotNull;

public class Frozen extends WorldTimeOverride {

    private final short freezeTime;

    protected Frozen(@NotNull Builder builder) {
        super(builder);
        this.freezeTime = builder.freezeTime.shortValue();
    }

    //

    public short getFreezeTime() {
        return this.freezeTime;
    }

    @Override
    public void applyToWorld() {
        if (!isWorldLoaded()) {
            return;
        }
        World world = getWorldAdapter().getWorld();
        assert world != null;
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setTime(this.freezeTime);
    }

    @Override
    public void onWorldLoad(@NotNull WorldLoadEvent event) {
        super.onWorldLoad(event);
        applyToWorld();
    }

}
