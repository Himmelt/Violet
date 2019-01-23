package org.soraworld.violet.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.api.ICommand;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.api.ISender;
import org.soraworld.violet.manager.SpigotManager;
import org.soraworld.violet.wrapper.WrapperSender;

import java.util.*;

public class SpigotCommand extends Command implements ICommand {

    /**
     * 主管理器.
     */
    protected final IManager manager;
    /**
     * 是否仅玩家执行.
     */
    protected boolean onlyPlayer;
    /**
     * 父命令.
     */
    protected final ICommand parent;
    /**
     * Tab 补全候选列表.
     */
    protected List<String> tabs;
    /**
     * 注解执行器.
     */
    protected SubExecutor<ICommand, ISender> executor;
    /**
     * 子命令映射表.
     */
    protected final Map<String, ICommand> subs = new LinkedHashMap<>();

    public SpigotCommand(String name, String perm, boolean onlyPlayer, SpigotManager manager, String... aliases) {
        this(null, name, perm, onlyPlayer, manager, aliases);
    }

    public SpigotCommand(ICommand parent, String name, String perm, boolean onlyPlayer, IManager manager, String... aliases) {
        super(name);
        setPermission(perm);
        this.parent = parent;
        this.manager = manager;
        this.onlyPlayer = onlyPlayer;
        ArrayList<String> list = new ArrayList<>(Arrays.asList(aliases));
        list.removeIf(s -> s == null || s.isEmpty() || s.contains(" ") || s.contains(":"));
        setAliases(list);
    }

    @NotNull
    public ICommand build(@Nullable ICommand parent, @NotNull String name, @Nullable String perm, boolean onlyPlayer, @NotNull IManager manager) {
        return new SpigotCommand(parent, name, perm, onlyPlayer, manager);
    }

    public ICommand getParent() {
        return parent;
    }

    public Map<String, ICommand> getSubs() {
        return subs;
    }

    public IManager getManager() {
        return manager;
    }

    public boolean isOnlyPlayer() {
        return onlyPlayer;
    }

    public void setOnlyPlayer(boolean onlyPlayer) {
        this.onlyPlayer = onlyPlayer;
    }

    public List<String> getTabs() {
        return tabs;
    }

    public void setTabs(List<String> tabs) {
        this.tabs = tabs;
    }

    public SubExecutor<ICommand, ISender> getExecutor() {
        return executor;
    }

    public void setExecutor(SubExecutor<ICommand, ISender> executor) {
        this.executor = executor;
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        ISender source = WrapperSender.build(sender);
        handle(source, new Args(args));
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return tabCompletions(new Args(args));
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) {
        return tabCompletions(new Args(args));
    }

    public boolean testPermission(CommandSender target) {
        return getPermission() == null || target.hasPermission(getPermission());
    }

    public boolean testPermissionSilent(CommandSender target) {
        return getPermission() == null || target.hasPermission(getPermission());
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof SpigotCommand) return getName().equalsIgnoreCase(((SpigotCommand) obj).getName());
        return false;
    }
}
