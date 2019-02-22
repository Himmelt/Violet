package org.soraworld.violet.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.wrapper.WrapperSender;

import java.util.List;

public class CommandAdaptor extends Command implements ICommandAdaptor {

    @NotNull
    private final ICommand command;

    public CommandAdaptor(@NotNull ICommand command) {
        super(command.name);
        this.command = command;
    }

    @NotNull
    public ICommand getCommand() {
        return command;
    }

    /* execute */
    public boolean execute(CommandSender sender, String label, String[] args) {
        command.handle(WrapperSender.build(sender), new Args(args));
        return true;
    }

    /* tabComplete */
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return command.tabComplete(WrapperSender.build(sender), new Args(args));
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) {
        return command.tabComplete(WrapperSender.build(sender), new Args(args));
    }

    /* testPermission */
    public boolean testPermission(CommandSender sender) {
        return command.testPermission(WrapperSender.build(sender));
    }

    public boolean testPermissionSilent(CommandSender sender) {
        return command.testPermission(WrapperSender.build(sender));
    }

    /* others */
    public int hashCode() {
        return command.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof CommandAdaptor) return command.equals(((CommandAdaptor) obj).command);
        return false;
    }
}
