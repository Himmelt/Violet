package org.soraworld.violet.manager;

import org.soraworld.violet.plugin.SpongePlugin;
import org.soraworld.violet.text.ClickText;
import org.soraworld.violet.text.HoverText;
import org.soraworld.violet.text.JsonText;
import org.soraworld.violet.util.ChatColor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.nio.file.Path;

/**
 * Sponge 管理器.
 */
public abstract class VManager extends IManager<SpongePlugin> {

    public VManager(SpongePlugin plugin, Path path) {
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

    public void sendJson(Player player, JsonText... texts) {
        String commandLine = "tellraw " + player.getName() + " " + ChatColor.colorize(JsonText.toJson(jsonHead, texts));
        //Sponge.getServer().getConsole(.dispatchCommand(Bukkit.getConsoleSender(), commandLine);
        debug(commandLine);
    }

    public void checkUpdate(CommandSource sender) {
        if (checkUpdate) {
            Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> {
                if (hasUpdate()) {
                    if (sender instanceof Player) {
                        sendJson((Player) sender, new JsonText(trans("hasUpdate")),
                                new JsonText(ChatColor.GREEN + plugin.updateURL(),
                                        new ClickText(plugin.updateURL(), ClickText.Action.OPEN_URL),
                                        new HoverText(trans("clickUpdate"), HoverText.Action.SHOW_TEXT)
                                )
                        );
                    } else {
                        sendKey(sender, "hasUpdate" + ChatColor.GREEN + plugin.updateURL());
                    }
                }
            });
        }
    }

    public void console(String text) {
        Sponge.getServer().getConsole().sendMessage(Text.of(colorHead + text));
    }

    public void broadcast(String text) {
        Sponge.getServer().getBroadcastChannel().send(Text.of(colorHead + text));
    }
}
