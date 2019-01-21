package org.soraworld.violet.api;

import java.io.InputStream;
import java.net.URL;

/**
 * 插件接口.
 */
public interface IPlugin {

    /**
     * 获取插件id.
     * 推荐全部使用小写字母和数字，且务必不要带空格和特殊字符.
     *
     * @return id
     */
    String getId();

    /**
     * 获取插件名称.
     * 推荐不要带空格和特殊字符.
     *
     * @return 名称
     */
    String getName();

    /**
     * 获取资源id.
     * 推荐全部使用小写字母和数字，且务必不要带空格和特殊字符.
     * 特别注意: 资源文件夹 assets 的子目录的名字要和该 id 保持一致，
     * 否则将无法提取资源文件 !!!
     *
     * @return 资源id
     */
    default String assetsId() {
        return getId();
    }

    /**
     * 获取插件版本.
     * 请使用 x.y.z 格式
     *
     * @return 版本
     */
    String getVersion();

    /**
     * 插件是否已启用.
     *
     * @return 是否启用
     */
    boolean isEnabled();

    /**
     * 从jar获取资源文件的 {@link InputStream}.
     *
     * @param path assets目录下，插件id之后的路径
     * @return 资源文件的输入流
     */
    default InputStream getAssetStream(String path) {
        return getClass().getResourceAsStream("/assets/" + assetsId() + '/' + path);
    }

    /**
     * 从jar获取资源文件的 {@link URL}.
     *
     * @param path assets目录下，插件id之后的路径
     * @return 资源文件的 URL
     */
    default URL getAssetURL(String path) {
        return getClass().getResource("/assets/" + assetsId() + '/' + path);
    }

    /**
     * 插件启用后.
     */
    default void afterEnable() {
    }

    /**
     * 插件停用前.
     */
    default void beforeDisable() {
    }
}
