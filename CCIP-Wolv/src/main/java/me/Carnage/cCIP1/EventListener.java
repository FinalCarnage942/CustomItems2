package me.Carnage.cCIP1;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class EventListener implements Listener {

    private final HashMap<UUID, HashMap<Material, Long>> cooldowns = new HashMap<>();
    private final HashMap<UUID, Queue<Location>> lastLocations = new HashMap<>();
    private final HashMap<UUID, Boolean> playerResponse = new HashMap<>();

    private final CooldownManager cooldownManager;
    private final ConfigManager configManager;

    public EventListener(CCIP1 plugin) {
        this.cooldownManager = new CooldownManager();
        this.configManager = new ConfigManager(plugin);
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.CHORUS_FRUIT) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot eat this item!");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        // Check for right-click actions
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (item != null && item.getType() != Material.AIR) {
                if (isCustomItem(item)) {
                    UUID playerId = player.getUniqueId();
                    long currentTime = System.currentTimeMillis();

                    // Check if the item has a cooldown
                    if (cooldownManager.checkCooldown(playerId, item.getType(), currentTime)) {
                        if (item.getType() == Material.RED_DYE && offHandItem.getType() == Material.BLUE_DYE) {
                            // Send confirmation message
                            sendConfirmationMessage(player);
                        } else if (item.getType() != Material.RED_DYE && offHandItem.getType() != Material.BLUE_DYE) {
                            // Handle other item interactions
                            switch (item.getType()) {
                                case GOAT_HORN:
                                    cooldownManager.startCooldown(playerId, item.getType(), player);
                                    handleGoatHorn(player);
                                    break;
                                case END_CRYSTAL:
                                    cooldownManager.startCooldown(playerId, item.getType(), player);
                                    handleEndCrystal(player);
                                    break;
                                case OBSIDIAN:
                                case STICK:
                                case PRISMARINE_SHARD:
                                case GHAST_TEAR:
                                case SHULKER_SHELL:
                                case BLACK_DYE:
                                case BLAZE_ROD:
                                case CHORUS_FRUIT:
                                    cooldownManager.startCooldown(playerId, item.getType(), player);
                                    applyItemEffects(player, item);
                                    break;
                                default:
                                    break;
                            }
                            cooldownManager.setCooldown(playerId, item.getType(), currentTime);
                        } else {
                            player.sendMessage("§4§lYou must wait before using this item again!");
                        }
                    }
                }
            }
        }
    }

    // Send a confirmation message to the player with clickable options
    private void sendConfirmationMessage(Player player) {
        // Create clickable text for YES and NO
        Component yes = Component.text("§a[YES]")
                .clickEvent(ClickEvent.runCommand("/combine yes"));

        Component no = Component.text("§c[NO]")
                .clickEvent(ClickEvent.runCommand("/combine no"));

        // Send the confirmation message
        player.sendMessage(Component.text("§eDo you wish to combine the Red and Blue orb? ").append(yes).append(Component.text(" ")).append(no));
    }

    // Check if the item is a custom item with display name or lore
    private boolean isCustomItem(ItemStack item) {
        return item != null && item.getType() != Material.AIR && (item.getItemMeta() != null && (item.getItemMeta().hasDisplayName() || item.getItemMeta().hasLore()));
    }

    // Destroy blocks in a specified radius around a center location
    private void destroyBlocksInArea(Location center, int radius) {
        int minX = center.getBlockX() - radius;
        int maxX = center.getBlockX() + radius;
        int minY = center.getBlockY() - radius;
        int maxY = center.getBlockY() + radius;
        int minZ = center.getBlockZ() - radius;
        int maxZ = center.getBlockZ() + radius;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location loc = new Location(center.getWorld(), x, y, z);
                    if (loc.distance(center) <= radius) {
                        loc.getBlock().setType(Material.AIR); // Replace the block with air
                    }
                }
            }
        }
    }

    // Handle Goat Horn item interaction
    private void handleGoatHorn(Player player) {
        Location playerLocation = player.getLocation();
        double radius = 10.0; // Radius to affect players

        // Loop through all online players
        for (Player target : Bukkit.getOnlinePlayers()) {
            // Check if the target is not the player and is within the radius
            if (!target.equals(player) && target.getLocation().distance(playerLocation) <= radius) {
                // Calculate the direction from the player to the target
                Vector direction = target.getLocation().toVector().subtract(playerLocation.toVector()).normalize();

                // Push the target away
                target.setVelocity(direction.multiply(2)); // Adjust the multiplier for the push strength

                // Apply blindness effect for 5 seconds
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1)); // 100 ticks = 5 seconds
            }
        }
    }

    // Handle End Crystal item interaction
    private void handleEndCrystal(Player player) {
        Location launchLocation = player.getEyeLocation();
        Vector direction = launchLocation.getDirection().normalize();

        // Create a custom projectile using FallingBlock
        BlockData blockData = Material.PURPLE_STAINED_GLASS.createBlockData();
        FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(launchLocation, blockData);
        fallingBlock.setDropItem(false); // Prevent the falling block from dropping items

        // Set an initial velocity to give it a forward motion
        fallingBlock.setVelocity(direction.multiply(configManager.getFallingBlockSpeed())); // Set the speed of the falling block

        // Create a task to handle the projectile's behavior
        new BukkitRunnable() {
            private int ticksPassed = 0; // Track how many ticks have passed

            @Override
            public void run() {
                if (ticksPassed < 300) { // 300 ticks = ~15 seconds
                    // Update the falling block's position
                    Location currentLocation = fallingBlock.getLocation();

                    // Reapply the forward velocity to ensure it continues to move forward
                    Vector newVelocity = direction.clone().multiply(0.5); // Adjust speed as necessary
                    fallingBlock.setVelocity(newVelocity); // Continue moving forward

                    // Destroy blocks in the area around the falling block
                    destroyBlocksInArea(currentLocation, configManager.getFallingBlockWidth()); // Adjust radius as necessary

                    // Add particle effect around the falling block while it's in the air
                    player.getWorld().spawnParticle(Particle.PORTAL, currentLocation, 1000); // High particle count for effect
                    ticksPassed++;
                } else {
                    // Stop the falling block and cancel the task after the duration
                    fallingBlock.remove(); // Remove the falling block
                    cancel(); // Stop the task
                }
            }
        }.runTaskTimer(CCIP1.getPlugin(CCIP1.class), 0, 1); // Check every tick

        player.sendMessage("§bYou unleashed the Hollow Purple!");
    }

    // Apply effects based on the item type
    private void applyItemEffects(Player player, ItemStack item) {
        ItemEffects.applyEffects(player, item, configManager);
    }

    public HashMap<UUID, Queue<Location>> getLastLocations() {
        return lastLocations;
    }
}
