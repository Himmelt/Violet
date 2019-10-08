package org.soraworld.violet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.rocksdb.RocksDB;
import org.soraworld.violet.manager.FManager;
import org.soraworld.violet.plugin.SpigotPlugin;
import org.soraworld.violet.util.DownloadUtils;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.UUID;

/**
 * @author Himmelt
 */
public class SpigotViolet extends SpigotPlugin<FManager> {

    private static SpigotViolet instance;

    {
        instance = this;
    }


    @Override
    public void beforeLoad() {
        loadLibraries();
    }

    private void loadLibraries() {
        Path hocon = getRootPath().resolve("lib").resolve("hocon-" + Violet.HOCON_VERSION + ".jar");
        Path rocksdb = getRootPath().resolve("lib").resolve("rocksdbjni-" + Violet.ROCKSDB_VERSION + ".jar");
        try {
            DownloadUtils.download("https://maven.aliyun.com/repository/public/org/soraworld/hocon/1.2.1/hocon-1.2.1.jar", hocon);
            DownloadUtils.download("https://maven.aliyun.com/repository/public/org/rocksdb/rocksdbjni/6.2.2/rocksdbjni-6.2.2-win64.jar", rocksdb);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        ClassLoader loader = getClass().getClassLoader().getParent();
        if (loader == null) {
            loader = getClass().getClassLoader();
        }
        System.out.println(loader);
        try {
            final Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
            addURL.invoke(loader, hocon.toUri().toURL());
            addURL.invoke(loader, rocksdb.toUri().toURL());
            RocksDB.loadLibrary();
        } catch (final Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public FManager getManager() {
        return manager;
    }

    @Override
    public void afterEnable() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            manager.asyncLoadData(player.getUniqueId());
        }
        try {
            RocksDB db = RocksDB.open(getRootPath().resolve("rocksdb").toString());
            byte[] bytes = db.get("testKey".getBytes());
            String str = new String(bytes);
            System.out.println("get testKey:" + str);
            db.put("testKey".getBytes(), "teshlsaddlkahsd,mzc sfheha;nsa c zcakfnjcb ahkdshdbfej cnczd vvtValue".getBytes());
            db.put("abbb".getBytes(), "tessajdbuy397462gd ai gadhnoinfvty2n0n60qcngy2384wcaifliq4ptvnsuitValue".getBytes());
            db.put("dqwrsda".getBytes(), "testackfomhuav9pyt8pqyrwyvdhfu k djbuiba tbwo7ebot73crabsValue".getBytes());
            db.put("rtyi".getBytes(), "testVnduho9w8374qun293ryoifnoae9 ty0439alue".getBytes());
            db.put("ffdhgjgj".getBytes(), "testafhoinhv3q480972rnalshfl90l7rayewuyahlvajdfs lzshValue".getBytes());
            db.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeDisable() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            manager.saveData(player.getUniqueId(), false);
        }
    }

    public static UUID getUuid() {
        return instance.manager.getUuid();
    }
}
