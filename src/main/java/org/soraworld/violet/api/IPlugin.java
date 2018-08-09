package org.soraworld.violet.api;

import java.io.InputStream;

public interface IPlugin {
    String getId();

    default InputStream getAsset(String path) {
        return getClass().getResourceAsStream("/assets/" + getId() + '/' + path);
    }
}
