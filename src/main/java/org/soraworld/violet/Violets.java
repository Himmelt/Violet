package org.soraworld.violet;

import java.util.regex.Pattern;

public final class Violets {

    public static final String PLUGIN_ID = "violet";
    public static final String PLUGIN_NAME = "Violet";
    public static final String PLUGIN_VERSION = "2.0.0";

    public static final String KEY_CHAT_HEAD = "chatHead";
    public static final String KEY_GET_VERSION = "getVersion";
    public static final String KEY_GET_LANG = "getLang";
    public static final String KEY_SET_LANG = "setLang";
    public static final String KEY_CFG_SAVE = "configSaved";
    public static final String KEY_CFG_SAVE_FAIL = "configSaveFailed";
    public static final String KEY_CFG_LOAD = "configLoaded";
    public static final String KEY_CFG_LOAD_FAIL = "configLoadFailed";
    public static final String KEY_NO_CMD_PERM = "noCommandPerm";
    public static final String KEY_DEBUG_ON = "debugON";
    public static final String KEY_DEBUG_OFF = "debugOFF";

    public static final String KEY_ONLY_PLAYER = "onlyPlayer";
    public static final String KEY_INVALID_ARG = "invalidArg";
    public static final String KEY_INVALID_INT = "invalidInt";
    public static final String KEY_INVALID_FLOAT = "invalidFloat";
    public static final String KEY_CMD_USAGE = "cmdUsage";
    public static final String KEY_ONLY_PLAYER_OR_INVALID_ARG = "onlyPlayerOrInvalidArg";

    public static final String PERM_ADMIN = PLUGIN_ID + ".admin";

    public static final char COLOR_CHAR = '\u00A7';
    public static final Pattern COLOR_PATTERN = Pattern.compile("((?<!&)&[0-9a-fk-or])+");

    public static final String KEY_PLUGIN_ENABLED = "pluginEnabled";
    public static final String KEY_PLUGIN_DISABLED = "pluginDisabled";

}
