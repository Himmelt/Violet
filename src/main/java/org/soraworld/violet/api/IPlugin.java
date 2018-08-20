package org.soraworld.violet.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.List;

public interface IPlugin {

    @Nonnull
    String getId();

    default InputStream getAsset(String path) {
        return getClass().getResourceAsStream("/assets/" + getId() + '/' + path);
    }

    @Nullable
    List<Object> registerListeners();

    void afterEnable();

    void beforeDisable();
}
