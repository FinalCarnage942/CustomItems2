package me.Carnage.cCIPWolv;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;

import java.util.*;

public final class CCIPWolv extends JavaPlugin implements Listener, TabCompleter {

    private final HashMap<UUID, HashMap<Material, Long>> cooldowns = new HashMap<>();
    private final HashMap<UUID, Queue<Location>> lastLocations = new HashMap<>();
    private final HashMap<UUID, Boolean> playerResponse = new HashMap<>();

    private long cooldownTime = 120000; // Cooldown time in milliseconds (120 seconds)

    private final String[] vanillaPowers = {"ObsidianShard", "HealingWand", "PrismarinePearl", "HealingTears", "ShulkerShell", "DragonScale", "StrengthenBlazeRod", "ChorusFruitofFloating"};
    private final String[] customPowers = {"WolfScream","HollowPurple","RedOrb","BlueOrb"};

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("CCIPWolv").setExecutor(this);
        getCommand("CCIPWolv").setTabCompleter(this);
        getLogger().info("CCIPWolv Plugin has been enabled!");

        // Schedule a repeating task to store player locations
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                lastLocations.putIfAbsent(playerId, new LinkedList<>());

                Queue<Location> locations = lastLocations.get(playerId);
                if (locations.size() >= 60) {
                    locations.poll(); // Remove the oldest location if we already have 60
                }
                locations.offer(player.getLocation()); // Add the current location
            }
        }, 0L, 20L); // Store every second (20 ticks)
    }

    @Override
    public void onDisable() {
        getLogger().info("CCIPWolv Plugin has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("CCIPWolv")) {
            if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
                String playerName = args[1];
                String category = args[2];
                String itemName = args[3];

                Player targetPlayer = Bukkit.getPlayer(playerName);
                if (targetPlayer != null) {
                    ItemStack itemToGive = getItemFromCategory(category, itemName);
                    if (itemToGive != null) {
                        targetPlayer.getInventory().addItem(itemToGive);
                        targetPlayer.sendMessage("You have been given " + itemToGive.getItemMeta().displayName() + "!");
                        sender.sendMessage("Gave " + itemToGive.getItemMeta().displayName() + " to " + playerName + ".");
                    } else {
                        sender.sendMessage("Item not found in the specified category.");
                    }
                } else {
                    sender.sendMessage("Player not found.");
                }
                return true;
            } else {
                sender.sendMessage("Usage: /CCIPWolv give <PlayerName> <Category> <ItemName>");
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("give");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            completions.add("VanillaPowers");
            completions.add("CustomPowers");
        } else if (args.length == 4) {
            if (args[2].equalsIgnoreCase("VanillaPowers")) {
                for (String item : vanillaPowers) {
                    completions.add(item);
                }
            } else if (args[2].equalsIgnoreCase("CustomPowers")) {
                for (String item : customPowers) {
                    completions.add(item);
                }
            }
        }
        return completions;
    }

    private ItemStack getItemFromCategory(String category, String itemName) {
        if (category.equalsIgnoreCase("VanillaPowers")) {
            return VanillaItems.getItem(itemName);
        } else if (category.equalsIgnoreCase("CustomPowers")) {
            return CustomItems.getItem(itemName);
        }
        return null;
    }
}
