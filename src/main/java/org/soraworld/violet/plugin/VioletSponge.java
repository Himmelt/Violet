package org.soraworld.violet.plugin;

import org.soraworld.violet.Violet;
import org.soraworld.violet.constant.Violets;
import org.soraworld.violet.listener.EventListener;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static rikka.RikkaAPI.getCommandSender;

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
    private Path path = new File(Violets.PLUGIN_NAME).toPath();
    private VioletPlugin plugin = new Violet();

    @Listener
    public void onEnable(GameInitializationEvent event) {
        plugin.loadConfig(path);
        Sponge.getCommandManager().register(this, this, Violets.PLUGIN_ID);
        Sponge.getEventManager().registerListeners(this, new EventListener());
    }

    @Listener
    public void onDisable(GameStoppingServerEvent event) {
        plugin.onDisable();
    }

    @Nonnull
    public CommandResult process(@Nonnull CommandSource source, @Nonnull String arguments) {
        boolean result = plugin.execute(getCommandSender(source), new ArrayList<>(Arrays.asList(arguments.split(" "))));
        return result ? CommandResult.success() : CommandResult.empty();
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
