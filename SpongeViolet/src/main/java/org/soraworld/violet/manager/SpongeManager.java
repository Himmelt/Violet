package org.soraworld.violet.manager;

import org.soraworld.violet.api.ISender;
import org.soraworld.violet.plugin.SpongePlugin;
import org.soraworld.violet.wrapper.WrapperSender;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.nio.file.Path;

/**
 * Sponge 管理器.
 */
public abstract class SpongeManager extends VioletManager<SpongePlugin> {

    public SpongeManager(SpongePlugin plugin, Path path) {
        super(plugin, path);
    }

    public void asyncSave() {
        if (!asyncSaveLock) {
            asyncSaveLock = true;
            Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> {
                save();
                asyncSaveLock = false;
            });
        }
    }

    public void send(CommandSource sender, String message) {
        sender.sendMessage(Text.of(colorHead + message));
    }

    public void sendKey(CommandSource sender, String key, Object... args) {
        send(sender, trans(key, args));
    }

    public void sendKey(ISender sender, String key, Object... args) {
        send(((WrapperSender) sender).getSender(), trans(key, args));
    }

    public void console(String text) {
        Sponge.getServer().getConsole().sendMessage(Text.of(colorHead + text));
    }

    public void broadcast(String text) {
        Sponge.getServer().getBroadcastChannel().send(Text.of(colorHead + text));
    }
}
