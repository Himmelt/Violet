package org.soraworld.violet.bstats;

import org.bukkit.Bukkit;
import org.soraworld.violet.manager.SpigotManager;

import java.util.Timer;
import java.util.TimerTask;

public class SpigotMetrics extends Metrics<SpigotManager.Manager> {

    public SpigotMetrics(SpigotManager.Manager manager) {
        super(manager);
    }

    public void start() {
        if (timer != null) timer.cancel();
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (!manager.getPlugin().isEnabled()) {
                    timer.cancel();
                    return;
                }
                Bukkit.getScheduler().runTask(manager.getPlugin(), () -> submitData(false));
            }
        }, 1000 * 60 * 5, 1000 * 60 * 30);
    }

    public String getServerJson() {
        int playerAmount = Bukkit.getServer().getOnlinePlayers().size();
        int onlineMode = Bukkit.getOnlineMode() ? 1 : 0;
        String bukkitVersion = Bukkit.getVersion();
        bukkitVersion = bukkitVersion.substring(bukkitVersion.indexOf("MC: ") + 4, bukkitVersion.length() - 1);
        return BUKKIT_JSON
                .replace("%serverUUID%", manager.getUuid().toString())
                .replace("%playerAmount%", String.valueOf(playerAmount))
                .replace("%onlineMode%", String.valueOf(onlineMode))
                .replace("%bukkitVersion%", bukkitVersion)
                .replace("%javaVersion%", JAVA_VERSION)
                .replace("%osName%", OS_NAME)
                .replace("%osArch%", OS_ARCH)
                .replace("%osVersion%", OS_VERSION)
                .replace("%coreCount%", String.valueOf(CORE_COUNT));
    }
}
