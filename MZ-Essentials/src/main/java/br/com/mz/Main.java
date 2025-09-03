package br.com.mz;

import br.com.mz.commands.TpaCommand;
import br.com.mz.listeners.TpaListeners;
import com.google.inject.Guice;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {

        var injector = Guice.createInjector(new PluginModule(this));

        var tpaListener = injector.getInstance(TpaListeners.class);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(tpaListener, this);

        var tpaCommand = injector.getInstance(TpaCommand.class);
        Objects.requireNonNull(getCommand("tpa")).setExecutor(tpaCommand);

        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }
}
