package fr.studioevident.tpplot.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import fr.studioevident.tpplot.TpPlot;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class TpPlotCommand implements CommandExecutor, TabCompleter {
    private final TpPlot plugin;

    public TpPlotCommand(TpPlot plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, final Command command, final String alias, final String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player)sender;
        World world = player.getWorld();

        if (player.getGameMode() != GameMode.CREATIVE) {
            plugin.sendMessage(player, "§cVous ne pouvez pas exécuter cette commande !");
            return true;
        }

        if (args.length != 1 && args.length != 2) {
            plugin.sendMessage(player, "§cNombre d'arguments invalide !");
            return true;
        }

        Map<String, ProtectedRegion> allRegions = plugin.getRegionsOfWorld(world);
        List<String> regions = plugin.getPlotsNamesOfPlayer(world, Bukkit.getOfflinePlayerIfCached(args[0]));

        ProtectedRegion plot;
        if (args.length == 2)
        {
            String plotName = args[1];
            plot = allRegions.get(plotName);
        }
        else if (regions.size() == 1)
        {
            plot = allRegions.get(regions.get(0));
        }
        else if (regions.size() == 0)
        {
            plugin.sendMessage(player, "§cCe joueur n'a aucun plot !");
            return true;
        }
        else
        {
            plugin.sendMessage(player, "§cCe joueur a plusieurs plots: " + ChatColor.GOLD + String.join(", ", regions));
            return true;
        }

        if (plot == null) {
            plugin.sendMessage(player, "§cPlot introuvable !");
            return true;
        }

        player.teleport(plugin.getTpPoint(world, plot));
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        Player player = (Player)sender;

        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (Player p : plugin.getPlayersWithPlot(player.getWorld())) {
                names.add(p.getName());
            }

            return names;
        }

        if (args.length == 2) {
            return plugin.getPlotsNamesOfPlayer(player.getWorld(), Bukkit.getOfflinePlayerIfCached(args[0]));
        }

        return new ArrayList<>();
    }
}
