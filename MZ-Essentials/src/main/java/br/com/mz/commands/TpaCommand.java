package br.com.mz.commands;

import br.com.mz.Main;
import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class TpaCommand implements CommandExecutor {

    private final HashMap<UUID, UUID> _tpaRequest = new HashMap<>();
    private final HashMap<UUID, BukkitTask> _tpaAcceptedTasks = new HashMap<>();

    private final Main _plugin;

    @Inject
    public TpaCommand(Main plugin) {
        _plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)){
            sender.sendMessage("Comando exclusivo para players.");
            return false;
        }

        if(args.length != 1){
            p.sendMessage("§cUso incorreto. Use: ");
            p.sendMessage("§e/tpa §b<nick>.");
            p.sendMessage("§e/tpa §aaceitar.");
            p.sendMessage("§e/tpa §crecusar.");
            return  false;
        }

        if(args[0].equalsIgnoreCase("aceitar")){
            if(!_tpaRequest.containsKey(p.getUniqueId())){
                p.sendMessage("§cVoce nao tem nenhum pedido de tpa ativo.");
                return false;
            }

            var requesterId =  _tpaRequest.get(p.getUniqueId());
            var playerRequester =  Bukkit.getPlayer(requesterId);

            _tpaRequest.remove(p.getUniqueId());

            BukkitTask task = new BukkitRunnable(){
                @Override
                public void run() {
                    assert playerRequester != null;
                    if(playerRequester.isOnline()){
                        playerRequester.teleport(p.getLocation());
                        playerRequester.sendMessage("§aVocê foi teleportado com sucesso!");
                        p.sendMessage("§aO jogador §b"+playerRequester.getName()+" §afoi ate voce!");
                    }

                    _tpaRequest.remove(playerRequester.getUniqueId());
                    _tpaAcceptedTasks.remove(playerRequester.getUniqueId());
                }
            }.runTaskLater(_plugin, 20L*3);

            assert playerRequester != null;
            _tpaAcceptedTasks.put(playerRequester.getUniqueId(), task);

            playerRequester.sendMessage("§aO pedido de TPA foi aceito. Em 3 segundos você será teletransportado");
            playerRequester.sendMessage("§cNÃO SE MEXA, em caso de movimentação o tpa será cancelado automaticamente");

            return true;
        }

        if(args[0].equalsIgnoreCase("recusar")){
            if(!_tpaRequest.containsKey(p.getUniqueId())){
                p.sendMessage("§cVoce nao tem nenhum pedido de tpa ativo.");
                return false;
            }

            var requesterId =  _tpaRequest.get(p.getUniqueId());
            var playerRequester =  Bukkit.getPlayer(requesterId);

            assert playerRequester != null;
            playerRequester.sendMessage("§cO jogador " + p.getName() + " Recusou seu pedido de tpa.");

            _tpaRequest.remove(p.getUniqueId());
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null || !target.isOnline()){
            p.sendMessage("§cJogador não encontrado!");
            return  false;
        }

        if(p == target){
            p.sendMessage("§cVocê nao pode dar tpa em você mesmo.");
            return false;
        }

        _tpaRequest.put(target.getUniqueId(), p.getUniqueId());

        p.sendMessage("§aPedido de TPA enviado para " + target.getName());
        target.sendMessage("§aO jogador §b" + p.getName() + " §aEsta enviando um pedido de tpa.");
        target.sendMessage("§eUse §a/tpa aceitar §epara aceitar ou §c/tpa recusar §epara recusar.");

        Bukkit.getScheduler().runTaskLater(_plugin, () -> {
            if(_tpaRequest.containsKey(target.getUniqueId()) && _tpaRequest.get(target.getUniqueId()).equals(p.getUniqueId())){
                _tpaRequest.remove(target.getUniqueId());
                p.sendMessage("§cSeu pedido de TPA para "+ target.getName() +" expirou");
                target.sendMessage("§cO pedido de TPA de "+ p.getName() +" para voce expirou");
            }
        }, 20L*60);

        return false;
    }

    public HashMap<UUID, BukkitTask> getTpaAcceptedTasks() {
        return _tpaAcceptedTasks;
    }

    public void removeTpaAcceptedTask(UUID uuid){
        _tpaAcceptedTasks.remove(uuid);
    }
}
