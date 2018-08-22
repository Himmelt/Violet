package org.soraworld.violet.api;

import javax.annotation.Nonnull;
import java.io.InputStream;

public interface IPlugin {

    @Nonnull
    String getId();

    @Nonnull
    String getVersion();

    default InputStream getAsset(String path) {
        return getClass().getResourceAsStream("/assets/" + getId() + '/' + path);
    }

    void afterEnable();

    void beforeDisable();
}
