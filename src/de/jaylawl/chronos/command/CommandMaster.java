package de.jaylawl.chronos.command;

import de.jaylawl.chronos.Chronos;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CommandMaster implements CommandExecutor, TabCompleter {

    public CommandMaster() {
    }

    //

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] arguments) {
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] arguments) {
        if (!Chronos.getInstance().reload(commandSender)) {
            commandSender.sendMessage(Component.text("Plugin is already reloading. You will be notified upon completion..."));
        }
        return true;
    }

}
