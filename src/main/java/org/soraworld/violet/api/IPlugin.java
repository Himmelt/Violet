package org.soraworld.violet.api;

import javax.annotation.Nonnull;
import java.io.InputStream;

/**
 * 插件接口.
 */
public interface IPlugin {

    /**
     * 获取插件id.
     * 推荐全部小写，且不要带空格和特殊字符.
     *
     * @return id
     */
    @Nonnull
    String getId();

    /**
     * 获取插件版本.
     * 请使用 x.y.z 格式
     *
     * @return 版本
     */
    @Nonnull
    String getVersion();

    /**
     * 从jar获取资源文件的 {@link InputStream}.
     *
     * @param path assets目录下，插件id之后的路径
     * @return 资源文件的输入流
     */
    default InputStream getAsset(String path) {
        return getClass().getResourceAsStream("/assets/" + getId() + '/' + path);
    }

    /**
     * 插件启用后.
     */
    void afterEnable();

    /**
     * 插件停用前.
     */
    void beforeDisable();
}
