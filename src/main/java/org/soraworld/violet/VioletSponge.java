package org.soraworld.violet;

import org.soraworld.violet.command.VioletCommand;
import org.soraworld.violet.config.VioletManager;
import org.soraworld.violet.config.VioletSetting;
import org.soraworld.violet.constant.Violets;
import org.soraworld.violet.listener.EventListener;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import rikka.RikkaAPI;
import rikka.api.command.CommandArgs;
import rikka.api.command.ExecuteResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Plugin(
        id = Violets.PLUGIN_ID,
        name = Violets.PLUGIN_NAME,
        version = Violets.PLUGIN_VERSION,
        authors = {"Himmelt"},
        url = "https://github.com/Himmelt/Violet",
        description = "Violet Plugin Library."
)
public class VioletSponge implements CommandCallable {

    @ConfigDir(sharedRoot = false)
    private Path path;

    protected VioletManager manager;
    protected VioletCommand command;

    @Listener
    public void onEnable(GameInitializationEvent event) {
        if (path == null) path = new File("config").toPath().resolve(Violets.PLUGIN_ID);
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (Throwable ignored) {
            }
        }
        initPlugin(path);
        Sponge.getCommandManager().register(this, this, Violets.PLUGIN_ID);
        Sponge.getEventManager().registerListeners(this, new EventListener());
    }

    @Nonnull
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments) {
        ExecuteResult result = command.execute(RikkaAPI.getCommandSender(source), new CommandArgs(arguments.split(" ")));
        return result == ExecuteResult.SUCCESS ? CommandResult.success() : CommandResult.empty();
    }

    protected void initPlugin(Path path) {
        manager = new VioletManager(path, new VioletSetting());
        manager.load();
        command = new VioletCommand(Violets.PERM_ADMIN, false, manager, Violets.PLUGIN_ID);
    }

    @Nonnull
    public List<String> getSuggestions(@Nonnull CommandSource source, @Nonnull String arguments, @Nullable Location<World> world) {
        return new ArrayList<>();
    }

    public boolean testPermission(@Nonnull CommandSource source) {
        return true;
    }

    @Nonnull
    public Optional<Text> getShortDescription(@Nonnull CommandSource source) {
        return Optional.empty();
    }

    @Nonnull
    public Optional<Text> getHelp(@Nonnull CommandSource source) {
        return Optional.empty();
    }

    @Nonnull
    public Text getUsage(@Nonnull CommandSource source) {
        return Text.EMPTY;
    }

}
