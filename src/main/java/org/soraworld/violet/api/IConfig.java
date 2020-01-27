package org.soraworld.violet.api;

/**
 * @author Himmelt
 */
public interface IConfig {
    /**
     * Before load.
     */
    void beforeLoad();

    /**
     * After load.
     */
    void afterLoad();
}
