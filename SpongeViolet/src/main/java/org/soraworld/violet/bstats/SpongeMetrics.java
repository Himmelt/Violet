package org.soraworld.violet.bstats;

import org.soraworld.violet.manager.SpongeManager;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;

import java.util.Timer;
import java.util.TimerTask;

public class SpongeMetrics extends Metrics<SpongeManager.Manager> {

    public SpongeMetrics(SpongeManager.Manager manager) {
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
                Scheduler scheduler = Sponge.getScheduler();
                Task.Builder builder = scheduler.createTaskBuilder();
                builder.execute(() -> submitData(true)).submit(manager.getPlugin());
            }
        }, 1000 * 60 * 5, 1000 * 60 * 30);
    }

    public String getServerJson() {
        if (serverJson == null) {
            int onlineMode = Sponge.getServer().getOnlineMode() ? 1 : 0;
            String minecraftVersion = Sponge.getGame().getPlatform().getMinecraftVersion().getName();
            String spongeImplementation = Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getName();
            serverJson = SPONGE_JSON
                    .replace("%serverUUID%", manager.getUuid().toString())
                    .replace("%onlineMode%", String.valueOf(onlineMode))
                    .replace("%minecraftVersion%", minecraftVersion)
                    .replace("%spongeImplementation%", spongeImplementation)
                    .replace("%javaVersion%", JAVA_VERSION)
                    .replace("%osName%", OS_NAME)
                    .replace("%osArch%", OS_ARCH)
                    .replace("%osVersion%", OS_VERSION)
                    .replace("%coreCount%", String.valueOf(CORE_COUNT));
        }
        int playerAmount = Sponge.getServer().getOnlinePlayers().size();
        playerAmount = playerAmount > 200 ? 200 : playerAmount;
        return serverJson.replace("%playerAmount%", String.valueOf(playerAmount));
    }
}
