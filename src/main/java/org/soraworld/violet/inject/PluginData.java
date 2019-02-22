package org.soraworld.violet.inject;

import org.soraworld.violet.command.ICommand;

import java.util.HashSet;

public class PluginData {
    public Class<?> mainManagerClass;
    public final HashSet<ICommand> commands = new HashSet<>();
    public final HashSet<Class<?>> injectClasses = new HashSet<>();
    public final HashSet<Class<?>> commandClasses = new HashSet<>();
    public final HashSet<Class<?>> listenerClasses = new HashSet<>();
}
