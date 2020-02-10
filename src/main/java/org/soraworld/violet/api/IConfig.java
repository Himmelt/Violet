package org.soraworld.violet.api;

/**
 * The interface Config.
 *
 * @author Himmelt
 */
public interface IConfig {
    /**
     * After load.
     */
    void afterLoad();

    /**
     * Before save.
     */
    void beforeSave();
}
