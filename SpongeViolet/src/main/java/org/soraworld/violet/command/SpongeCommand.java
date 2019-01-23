package org.soraworld.violet.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.api.ICommand;
import org.soraworld.violet.api.IManager;
import org.soraworld.violet.api.ISender;
import org.soraworld.violet.manager.SpongeManager;
import org.soraworld.violet.wrapper.WrapperSender;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

/**
 * Sponge 命令.
 */
public class SpongeCommand implements CommandCallable, ICommand {

    /**
     * 命令主名.
     */
    public final String name;
    /**
     * 管理器.
     */
    public final IManager manager;
    /**
     * 命令权限.
     */
    protected String permission;
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
     * 别名列表.
     */
    protected List<String> aliases = new ArrayList<>();
    /**
     * 用法.
     */
    protected String usage;
    /**
     * 子命令映射表.
     */
    protected final Map<String, ICommand> subs = new LinkedHashMap<>();

    public SpongeCommand(String name, String permission, boolean onlyPlayer, SpongeManager manager, String... aliases) {
        this(null, name, permission, onlyPlayer, manager, aliases);
    }

    public SpongeCommand(ICommand parent, String name, String permission, boolean onlyPlayer, IManager manager, String... aliases) {
        this.name = name;
        this.permission = permission;
        this.parent = parent;
        this.manager = manager;
        this.onlyPlayer = onlyPlayer;
        this.aliases.add(name);
        this.aliases.addAll(Arrays.asList(aliases));
        this.aliases.removeIf(s -> s == null || s.isEmpty() || s.contains(" ") || s.contains(":"));
    }

    public ICommand getParent() {
        return parent;
    }

    public CommandResult process(CommandSource sender, String args) {
        handle(WrapperSender.build(sender), new Args(args));
        return CommandResult.success();
    }

    public Text getUsage(CommandSource source) {
        return Text.of(usage == null ? "" : usage);
    }

    public @NotNull ICommand build(@Nullable ICommand parent, @NotNull String name, @Nullable String perm, boolean onlyPlayer, @NotNull IManager manager) {
        return new SpongeCommand(parent, name, perm, onlyPlayer, manager);
    }

    public String getName() {
        return name;
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

    public void setExecutor(SubExecutor<ICommand, ISender> executor) {
        this.executor = executor;
    }

    public SubExecutor<ICommand, ISender> getExecutor() {
        return executor;
    }

    public Map<String, ICommand> getSubs() {
        return subs;
    }

    public Object setDescription(String description) {
        return this;
    }

    public String getUsage() {
        return usage;
    }

    public Object setUsage(String usage) {
        this.usage = usage;
        return this;
    }

    public List<String> getSuggestions(CommandSource sender, String args, Location<World> location) {
        String[] ss = args.trim().split("[ ]+");
        if (!args.isEmpty() && args.endsWith(" ")) {
            ss = Arrays.copyOf(ss, ss.length + 1);
            ss[ss.length - 1] = "";
        }
        return tabCompletions(new Args(ss));
    }

    public boolean testPermission(CommandSource source) {
        return permission == null || source.hasPermission(permission);
    }

    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(getUsage(source));
    }

    public Optional<Text> getHelp(CommandSource source) {
        return Optional.of(getUsage(source));
    }

    public List<String> getAliases() {
        return aliases;
    }

    public Object setAliases(List<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof SpongeCommand) return name.equalsIgnoreCase(((SpongeCommand) obj).name);
        return false;
    }
}
