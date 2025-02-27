package me.Carnage.cCIP1;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public final class CCIP1 extends JavaPlugin {

    private ConfigManager configManager;
    private CooldownManager cooldownManager;
    private EventListener eventListener;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        cooldownManager = new CooldownManager();
        eventListener = new EventListener(this);

        getServer().getPluginManager().registerEvents(eventListener, this);
        getCommand("CCIPWolv").setExecutor(this);
        getCommand("CCIPWolv").setTabCompleter(this);
        getLogger().info("CCIPWolv Plugin has been enabled!");

        // Schedule a repeating task to store player locations
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player player : getServer().getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                eventListener.getLastLocations().putIfAbsent(playerId, new LinkedList<>());

                Queue<Location> locations = eventListener.getLastLocations().get(playerId);
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

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public EventListener getEventListener() {
        return eventListener;
    }
}
