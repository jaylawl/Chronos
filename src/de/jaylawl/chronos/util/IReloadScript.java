package de.jaylawl.chronos.util;

import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.logging.Logger;

// Copied from CommonLjb 03/03/2022

public abstract class IReloadScript {

    public enum CancellationReason {
        TIMEOUT("Reload process was cancelled because it timed out"),
        EXCEPTION("Reload process was cancelled due to an unhandled exception"),
        COMMAND("Reload process was cancelled via command"),
        UNKNOWN("Reload process was cancelled for unknown reasons");

        private final String message;

        CancellationReason(@NotNull String message) {
            this.message = message;
        }

        //

        public @NotNull String getMessage() {
            return this.message;
        }

    }

    private static final long DEFAULT_TIMEOUT_MILLISECONDS = 10000;

    protected final JavaPlugin pluginInstance;
    protected final Logger logger;
    protected long startMilliseconds = -1;
    protected double elapsedSeconds = -1;
    protected int totalWarnings = 0;

    private final LinkedHashSet<UUID> subscribers = new LinkedHashSet<>();
    private BukkitTask process;
    private BukkitTask watchDog;
    private boolean finished = false;
    private boolean cancelled = false;

    public IReloadScript(@NotNull JavaPlugin pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.logger = this.pluginInstance.getLogger();
    }

    //

    public final void run() throws IllegalStateException {
        if (this.startMilliseconds != -1) {
            throw new IllegalStateException("ReloadScript#run() has already been called");
        }
        this.startMilliseconds = System.currentTimeMillis();
        start();
        this.process = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    IReloadScript.this.initialSyncTasks();
                    IReloadScript.this.asyncTasks();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            IReloadScript.this.finalSyncTasks();
                            IReloadScript.this.elapsedSeconds = (System.currentTimeMillis() - IReloadScript.this.startMilliseconds) / 1000d;
                            IReloadScript.this.finished = true;
                            IReloadScript.this.killRunnables();
                            IReloadScript.this.finish();
                        }
                    }.runTask(IReloadScript.this.pluginInstance);
                } catch (Exception exception) {
                    IReloadScript.this.cancel(CancellationReason.EXCEPTION);
                    exception.printStackTrace();
                }
            }
        }.runTaskAsynchronously(this.pluginInstance);
        this.watchDog = new BukkitRunnable() {
            @Override
            public void run() {
                if (IReloadScript.this.isConcluded() || this.isCancelled()) {
                    IReloadScript.this.killRunnables();
                } else if (System.currentTimeMillis() - IReloadScript.this.startMilliseconds > IReloadScript.DEFAULT_TIMEOUT_MILLISECONDS) {
                    IReloadScript.this.cancel(CancellationReason.TIMEOUT);
                }
            }
        }.runTaskTimer(this.pluginInstance, 1L, 1L);
    }

    private void killRunnables() {
        if (this.process != null) {
            this.process.cancel();
        }
        if (this.watchDog != null) {
            this.watchDog.cancel();
        }
    }

    public final void cancel(@NotNull CancellationReason reason) {
        cancel(reason.message);
    }

    public final void cancel(@NotNull String message) {
        killRunnables();
        this.cancelled = true;
        this.logger.severe(message);
        notifySubscribers(Component.text(message).color(NamedTextColor.RED));
    }

    public final boolean isFinished() {
        return this.finished;
    }

    public final boolean isCancelled() {
        return this.cancelled;
    }

    public final boolean isConcluded() {
        return this.finished || this.cancelled;
    }

    public final void addSubscriber(@NotNull UUID playerUniqueId) {
        this.subscribers.add(playerUniqueId);
    }

    public final void removeSubscriber(@NotNull UUID playerUniqueId) {
        this.subscribers.remove(playerUniqueId);
    }

    public final void notifySubscribers(Component message) {
        if (message != null) {
            for (UUID subscriberId : this.subscribers) {
                Player player = Bukkit.getPlayer(subscriberId);
                if (player != null && player.isOnline()) {
                    player.sendMessage(message, MessageType.SYSTEM);
                }
            }
        }
    }

    //

    public void start() {
        this.logger.info("Starting plugin reload...");
        notifySubscribers(Component.text("Starting reload of " + this.pluginInstance.getName() + "..."));
    }

    public void initialSyncTasks() {
    }

    public void asyncTasks() {
    }

    public void finalSyncTasks() {
    }

    public void finish() {
        this.logger.info("Reload completed within " + this.elapsedSeconds + " s. and with " + this.totalWarnings + " warning(s)");
        notifySubscribers(getSubscriberNotification());
    }

    public @NotNull Component getSubscriberNotification() {
        String a = "Reloaded %1 within %2 s. ";
        a = a.replace("%1", this.pluginInstance.getName());
        a = a.replace("%2", String.valueOf(this.elapsedSeconds));
        Component notification = Component.text(a).color(NamedTextColor.WHITE);
        Component hoverable1 = Component.text("[").color(NamedTextColor.GRAY);
        Component hoverable2;
        Component hoverableText;
        if (this.totalWarnings <= 0) {
            hoverable2 = Component.text("✔").color(NamedTextColor.GREEN);
            hoverableText = Component.text("Reload completed without warnings").color(NamedTextColor.GREEN);
        } else {
            hoverable2 = Component.text("!").color(NamedTextColor.YELLOW);
            hoverableText = Component.text("Reload produced " + this.totalWarnings + " warning(s)\nSee console for details...").color(NamedTextColor.YELLOW);
        }
        Component hoverable3 = Component.text("]").color(NamedTextColor.GRAY);
        Component hoverable = hoverable1.append(hoverable2).append(hoverable3).hoverEvent(HoverEvent.showText(hoverableText));
        return notification.append(hoverable);
    }

}
