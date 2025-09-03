package br.com.mz.listeners;

import br.com.mz.commands.TpaCommand;
import com.google.inject.Inject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

public class TpaListeners implements Listener {

    private final TpaCommand _tpaCommand;

    @Inject
    public TpaListeners(TpaCommand tpaCommand){
        _tpaCommand = tpaCommand;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        var playerId = p.getUniqueId();

        var teleportTasks = _tpaCommand.getTpaAcceptedTasks();

        if(!teleportTasks.containsKey(playerId)){
            return;
        }

        Location from = e.getFrom();
        Location to = e.getTo();

        if(from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()){
            return;
        }

        BukkitTask task = teleportTasks.get(playerId);
        task.cancel();
        _tpaCommand.removeTpaAcceptedTask(playerId);

        p.sendMessage("§cVocê se moveu, e seu TPA foi cancelado.");
    }
}
