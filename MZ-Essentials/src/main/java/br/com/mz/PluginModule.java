package br.com.mz;

import br.com.mz.commands.TpaCommand;
import br.com.mz.listeners.TpaListeners;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class PluginModule extends AbstractModule {

    private final Main _plugin;

    public PluginModule(Main plugin){
        _plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(Main.class).toInstance(_plugin);

        bind(TpaCommand.class).in(Scopes.SINGLETON);

        bind(TpaListeners.class).in(Scopes.SINGLETON);
    }
}
