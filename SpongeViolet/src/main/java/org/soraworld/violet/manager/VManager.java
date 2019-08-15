package org.soraworld.violet.manager;

import org.jetbrains.annotations.Nullable;
import org.soraworld.violet.plugin.SpongePlugin;
import org.soraworld.violet.text.ClickText;
import org.soraworld.violet.text.HoverText;
import org.soraworld.violet.text.JsonText;
import org.soraworld.violet.util.ChatColor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.title.Title;

import java.nio.file.Path;

/**
 * Sponge 管理器.
 */
public abstract class VManager extends IManager<SpongePlugin> {

    public VManager(SpongePlugin plugin, Path path) {
        super(plugin, path);
    }

    public void asyncSave(@Nullable CommandSource sender) {
        if (!asyncSaveLock.get()) {
            asyncSaveLock.set(true);
            Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> {
                boolean flag = save();
                if (sender != null) {
                    Sponge.getScheduler().createSyncExecutor(plugin).execute(() -> sendKey(sender, flag ? "configSaved" : "configSaveFailed"));
                }
                asyncSaveLock.set(false);
            });
        }
    }

    public void asyncBackUp(@Nullable CommandSource sender) {
        if (!asyncBackLock.get()) {
            asyncBackLock.set(true);
            Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> {
                boolean flag = doBackUp();
                if (sender != null) {
                    Sponge.getScheduler().createSyncExecutor(plugin).execute(() -> sendKey(sender, flag ? "backUpSuccess" : "backUpFailed"));
                }
                asyncBackLock.set(false);
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
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), commandLine);
        debug(commandLine);
    }

    public void sendActionBar(Player player, String text) {
        player.sendMessage(ChatTypes.ACTION_BAR, Text.of(text));
    }

    public void sendActionKey(Player player, String key, Object... args) {
        player.sendMessage(ChatTypes.ACTION_BAR, Text.of(trans(key, args)));
    }

    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(Title.builder()
                .title(Text.of(title))
                .subtitle(Text.of(subtitle))
                .fadeIn(fadeIn).stay(stay).fadeOut(fadeOut).build());
    }

    public void checkUpdate(CommandSource sender) {
        if (checkUpdate) {
            Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> {
                if (hasUpdate()) {
                    if (sender instanceof Player) {
                        Sponge.getScheduler().createSyncExecutor(plugin).execute(() -> {
                            sendJson((Player) sender, new JsonText(trans("hasUpdate")),
                                    new JsonText(ChatColor.GREEN + plugin.updateURL(),
                                            new ClickText(plugin.updateURL(), ClickText.Action.OPEN_URL),
                                            new HoverText(trans("clickUpdate"), HoverText.Action.SHOW_TEXT)
                                    )
                            );
                        });
                    } else {
                        send(sender, trans("hasUpdate") + ChatColor.GREEN + plugin.updateURL());
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

    public boolean hasPermission(Subject subject, String permission) {
        if (permission == null || permission.isEmpty()) return true;
        permission = permMap.getOrDefault(permission, permission);
        return permission == null || permission.isEmpty() || subject.hasPermission(permission);
    }
}
