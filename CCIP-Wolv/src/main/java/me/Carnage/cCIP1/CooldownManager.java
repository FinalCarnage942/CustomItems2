package me.Carnage.cCIP1;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {

    private final HashMap<UUID, HashMap<Material, Long>> cooldowns = new HashMap<>();
    private final ConfigManager configManager;

    public CooldownManager() {
        this.configManager = new ConfigManager(CCIP1.getPlugin(CCIP1.class));
    }

    public void startCooldown(UUID playerId, Material itemType, Player player) {
        long currentTime = System.currentTimeMillis();
        cooldowns.putIfAbsent(playerId, new HashMap<>());
        cooldowns.get(playerId).put(itemType, currentTime);

        // Set the player's XP level based on the cooldown time
        int xpLevels = (int) (configManager.getItemCooldown(itemType) / 120); // 120 seconds cooldown, 12000 ticks = 120 seconds
        player.setLevel(xpLevels); // Set the player's XP level for cooldown
    }

    public boolean checkCooldown(UUID playerId, Material itemType, long currentTime) {
        if (!cooldowns.containsKey(playerId)) {
            return true; // No cooldowns set for this player
        }
        HashMap<Material, Long> playerCooldowns = cooldowns.get(playerId);
        return !playerCooldowns.containsKey(itemType) || (currentTime - playerCooldowns.get(itemType)) >= (configManager.getItemCooldown(itemType) * 1000);
    }

    public void setCooldown(UUID playerId, Material itemType, long currentTime) {
        cooldowns.putIfAbsent(playerId, new HashMap<>());
        cooldowns.get(playerId).put(itemType, currentTime);
        displayCooldown(playerId, itemType);
    }

    private void displayCooldown(UUID playerId, Material itemType) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null && cooldowns.containsKey(playerId)) {
                    HashMap<Material, Long> playerCooldowns = cooldowns.get(playerId);
                    if (playerCooldowns.containsKey(itemType)) {
                        long elapsed = System.currentTimeMillis() - playerCooldowns.get(itemType);
                        int remainingSeconds = (int) ((configManager.getItemCooldown(itemType) * 1000 - elapsed) / 1000); // Calculate remaining seconds

                        // Calculate the XP level based on the remaining time
                        int xpLevel = Math.max(remainingSeconds, 0); // XP level should be the remaining seconds
                        player.setLevel(xpLevel); // Set the XP level, ensuring it doesn't go negative

                        if (elapsed >= (configManager.getItemCooldown(itemType) * 1000)) {
                            playerCooldowns.remove(itemType); // Remove the cooldown for this item
                            player.setLevel(0); // Reset the XP level
                            Utility.sendActionBar(player, "§c§lCooldown is over for " + itemType.name() + "!"); // Notify the player above the hotbar
                            this.cancel(); // Stop the task
                        }
                    }
                }
            }
        }.runTaskTimer(CCIP1.getPlugin(CCIP1.class), 0L, 20L); // Update every second (20 ticks)
    }
}
