package org.soraworld.violet.manager;

import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.SpongeViolet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.util.ChatColor;
import org.spongepowered.api.command.CommandSource;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;

/**
 * Violet 管理器.
 */
public final class FSManager extends SpongeManager {

    @Setting(comment = "comment.uuid")
    private UUID uuid = UUID.randomUUID();

    private static FSManager manager;
    private static HashMap<String, HashMap<String, String>> langMaps = new HashMap<>();

    /**
     * 实例化管理器.
     *
     * @param plugin 插件实例
     * @param path   配置保存路径
     */
    public FSManager(SpongeViolet plugin, Path path) {
        super(plugin, path);
        manager = this;
    }

    public boolean setLang(String lang) {
        boolean flag = super.setLang(lang);
        langMaps.clear();
        langMaps.put(lang, langMap);
        return flag;
    }

    public String trans(String key, Object... args) {
        String text = langMap.get(key);
        if (text == null || text.isEmpty()) return key;
        if (args.length > 0) {
            try {
                return String.format(text, args);
            } catch (Throwable e) {
                console(ChatColor.RED + "Default translation " + key + " -> " + text + " format failed !");
            }
        }
        return text;
    }

    public ChatColor defChatColor() {
        return ChatColor.DARK_PURPLE;
    }

    /**
     * 全局翻译.
     *
     * @param lang 翻译语言
     * @param key  键
     * @param args 参数
     * @return 全局翻译结果
     */
    public static String trans(String lang, String key, Object... args) {
        if (manager == null) return key;
        String text = langMaps.computeIfAbsent(lang, s -> manager.loadLangMap(s)).get(key);
        if (text == null || text.isEmpty()) {
            return manager.trans(key, args);
        } else return text;
    }

    /**
     * 向命令发送者显示 violet 插件列表.
     *
     * @param sender 命令发送者
     */
    public void listPlugins(CommandSource sender) {
        for (IPlugin plugin : plugins) {
            sendKey(sender, "pluginInfo", plugin.getId(), plugin.getVersion());
        }
    }

    /**
     * 获取 Violet 插件运行 uuid
     *
     * @return the uuid
     */
    public UUID getUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
            asyncSave();
        }
        return uuid;
    }
}
