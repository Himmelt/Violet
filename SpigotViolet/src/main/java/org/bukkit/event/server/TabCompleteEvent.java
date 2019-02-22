package org.bukkit.event.server;

import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class TabCompleteEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final CommandSender sender;
    private final String buffer;
    private List<String> completions;
    private boolean cancelled;

    public TabCompleteEvent(CommandSender sender, String buffer, List<String> completions) {
        Validate.notNull(sender, "sender");
        Validate.notNull(buffer, "buffer");
        Validate.notNull(completions, "completions");

        this.sender = sender;
        this.buffer = buffer;
        this.completions = completions;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getBuffer() {
        return buffer;
    }

    public List<String> getCompletions() {
        return completions;
    }

    public void setCompletions(List<String> completions) {
        Validate.notNull(completions);
        this.completions = completions;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
