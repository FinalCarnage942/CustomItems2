package me.Carnage.cCIPWolv;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.configuration.file.FileConfiguration; // Import for configuration
import java.io.File; // Import for file handling
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Particle; // For Particle effects

import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.util.Vector;

public final class CCIPWolv extends JavaPlugin implements Listener, TabCompleter {

    private final HashMap<UUID, HashMap<Material, Long>> cooldowns = new HashMap<>();
    private final HashMap<UUID, Queue<Location>> lastLocations = new HashMap<>();
    private final HashMap<UUID, Boolean> playerResponse = new HashMap<>();

    private long cooldownTime = 120000; // Cooldown time in milliseconds (120 seconds)

    private final String[] vanillaPowers = {"ObsidianShard", "HealingWand", "PrismarinePearl", "HealingTears", "ShulkerShell", "DragonScale", "StrengthenBlazeRod", "ChorusFruitofFloating"};
    private final String[] customPowers = {"WolfScream","HollowPurple","RedOrb","BlueOrb"};

    private void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

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
                    if (checkCooldown(playerId, item.getType(), currentTime)) {
                        if (item.getType() == Material.RED_DYE && offHandItem.getType() == Material.BLUE_DYE) {
                            // Send confirmation message
                            sendConfirmationMessage(player);
                        } else if(item.getType() != Material.RED_DYE && offHandItem.getType() != Material.BLUE_DYE){
                            // Handle other item interactions
                            switch (item.getType()) {
                                case GOAT_HORN:
                                    startCooldown(playerId, item.getType(), player);
                                    // Get the player's location
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
                                    break;
                                case END_CRYSTAL:
                                    startCooldown(playerId, item.getType(), player);

                                    // Get the player's location and direction
                                    Location launchLocation = player.getEyeLocation();
                                    Vector direction = launchLocation.getDirection().normalize();

                                    // Create a custom projectile using FallingBlock
                                    FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(launchLocation, Material.PURPLE_STAINED_GLASS, (byte) 0);
                                    fallingBlock.setDropItem(false); // Prevent the falling block from dropping items

                                    // Set an initial velocity to give it a forward motion
                                    fallingBlock.setVelocity(direction.multiply(0.7)); // Set the speed of the falling block

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
                                                destroyBlocksInArea(currentLocation, 4); // Adjust radius as necessary

                                                // Add particle effect around the falling block while it's in the air
                                                player.getWorld().spawnParticle(Particle.PORTAL, currentLocation, 1000); // High particle count for effect
                                                ticksPassed++;
                                            } else {
                                                // Stop the falling block and cancel the task after the duration
                                                fallingBlock.remove(); // Remove the falling block
                                                cancel(); // Stop the task
                                            }
                                        }
                                    }.runTaskTimer(this, 0, 1); // Check every tick

                                    player.sendMessage("§bYou unleashed the Hollow Purple!");
                                    break;
                                case OBSIDIAN:
                                    startCooldown(playerId, item.getType(), player);
                                    applyItemEffects(player, item);
                                    break;
                                case STICK:
                                    startCooldown(playerId, item.getType(), player);
                                    applyItemEffects(player, item);
                                    break;
                                case PRISMARINE_SHARD:
                                    startCooldown(playerId, item.getType(), player);
                                    applyItemEffects(player, item);
                                    break;
                                case GHAST_TEAR:
                                    startCooldown(playerId, item.getType(), player);
                                    applyItemEffects(player, item);
                                    break;
                                case SHULKER_SHELL:
                                    startCooldown(playerId, item.getType(), player);
                                    applyItemEffects(player, item);
                                    break;
                                case BLACK_DYE: // Dragon Scale
                                    startCooldown(playerId, item.getType(), player);
                                    applyItemEffects(player, item);
                                    break;
                                case BLAZE_ROD:
                                    startCooldown(playerId, item.getType(), player);
                                    applyItemEffects(player, item);
                                    break;
                                case CHORUS_FRUIT:
                                    startCooldown(playerId, item.getType(), player);
                                    applyItemEffects(player, item);
                                    break;

                                default:
                                    break;
                            }
                            setCooldown(playerId, item.getType(), currentTime);
                        } else {
                            player.sendMessage("§4§lYou must wait before using this item again!");
                        }
                    }
                }
            }
        }
    }

    private void sendConfirmationMessage(Player player) {
        // Create clickable text for YES and NO
        TextComponent yes = new TextComponent("§a[YES]");
        yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/combine yes"));

        TextComponent no = new TextComponent("§c[NO]");
        no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/combine no"));

        // Send the confirmation message
        player.sendMessage("§eDo you wish to combine the Red and Blue orb? " + yes.getText() + " " + no.getText());
    }

    private boolean isCustomItem(ItemStack item) {
        return item != null && item.getType() != Material.AIR && (item.getItemMeta() != null && (item.getItemMeta().hasDisplayName() || item.getItemMeta().hasLore()));
    }

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

    private void startCooldown(UUID playerId, Material itemType, Player player) {
        long currentTime = System.currentTimeMillis();
        cooldowns.putIfAbsent(playerId, new HashMap<>());
        cooldowns.get(playerId).put(itemType, currentTime);

        // Set the player's XP level based on the cooldown time
        int xpLevels = (int) (cooldownTime / 12000); // 120 seconds cooldown, 12000 ticks = 120 seconds
        player.setLevel(xpLevels); // Set the player's XP level for cooldown
    }

    private boolean checkCooldown(UUID playerId, Material itemType, long currentTime) {
        if (!cooldowns.containsKey(playerId)) {
            return true; // No cooldowns set for this player
        }
        HashMap<Material, Long> playerCooldowns = cooldowns.get(playerId);
        return !playerCooldowns.containsKey(itemType) || (currentTime - playerCooldowns.get(itemType)) >= cooldownTime;
    }

    private void setCooldown(UUID playerId, Material itemType, long currentTime) {
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
                        int remainingSeconds = (int) ((cooldownTime - elapsed) / 1000); // Calculate remaining seconds

                        // Calculate the XP level based on the remaining time
                        int xpLevel = Math.max(remainingSeconds, 0); // XP level should be the remaining seconds
                        player.setLevel(xpLevel); // Set the XP level, ensuring it doesn't go negative

                        if (elapsed >= cooldownTime) {
                            playerCooldowns.remove(itemType); // Remove the cooldown for this item
                            player.setLevel(0); // Reset the XP level
                            sendActionBar(player, "§c§lCooldown is over for " + itemType.name() + "!"); // Notify the player above the hotbar
                            this.cancel(); // Stop the task
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L); // Update every second (20 ticks)
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
                        targetPlayer.sendMessage("You have been given " + itemToGive.getItemMeta().getDisplayName() + "!");
                        sender.sendMessage("Gave " + itemToGive.getItemMeta().getDisplayName() + " to " + playerName + ".");
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

    private ItemStack getItemFromCategory(String category, String itemName) {
        Material material = null;
        ItemMeta itemMeta = null;

        if (category.equalsIgnoreCase("VanillaPowers")) {
            switch (itemName.toLowerCase()) {
                case "obsidianshard":
                    material = Material.OBSIDIAN;
                    itemMeta = createItemMeta("§5§l§nObsidian Shard",
                            "§8A shard of obsidian,",
                            "§8imbued with dark power.",
                            "",
                            "§5Cooldown: 120 seconds",
                            "",
                            "",
                            "§dGrants the player Strength and Slowness",
                            "",
                            "",
                            "",
                            "",
                            "§9§l§ki §9§o§lMYTHIC RARITY ITEM§9§l§Ki");
                    break;
                case "healingwand":
                    material = Material.STICK;
                    itemMeta = createItemMeta("§6§l§nHealing Wand",
                            "§2A mystical wand from depths of the jungle,",
                            "§2granting the sacred powers of an old tribe",
                            "",
                            "§5Cooldown: 120 seconds",
                            "",
                            "",
                            "§dGrants the player Regeneration and Speed",
                            "",
                            "",
                            "",
                            "",
                            "§9§l§ki §9§o§lMYTHIC RARITY ITEM §9§l§Ki");
                    break;
                case "prismarinepearl":
                    material = Material.PRISMARINE_SHARD;
                    itemMeta = createItemMeta("§b§l§nPrismarine Pearl",
                            "§bA shimmering shard from the ocean,",
                            "§bproviding the gift of water breathing.",
                            "",
                            "§5Cooldown: 120 seconds",
                            "",
                            "",
                            "§dGrants the player Dolphin Grace and Water Breathing",
                            "",
                            "",
                            "",
                            "",
                            "§9§l§ki §9§o§lMYTHIC RARITY ITEM §9§l§Ki");
                    break;
                case "healingtears":
                    material = Material.GHAST_TEAR;
                    itemMeta = createItemMeta("§c§l§nHealing Tears",
                            "§fA tear shed by a ghast,",
                            "§fknown for its healing properties.",
                            "",
                            "§5Cooldown: 120 seconds",
                            "",
                            "",
                            "§dGrants the player Regeneration and Absorption",
                            "",
                            "",
                            "",
                            "",
                            "§9§l§ki §9§o§lMYTHIC RARITY ITEM §9§l§Ki");
                    break;
                case "shulkershell":
                    material = Material.SHULKER_SHELL;
                    itemMeta = createItemMeta("§d§l§nShulker Shell",
                            "§5A shell from a shulker,",
                            "§5used to create unique potions.",
                            "",
                            "§5Cooldown: 120 seconds",
                            "",
                            "",
                            "§dGrants the player Levitation",
                            "",
                            "",
                            "",
                            "",
                            "§9§l§ki §9§o§lMYTHIC RARITY ITEM §9§l§Ki");
                    break;
                case "dragonscale":
                    material = Material.BLACK_DYE;
                    itemMeta = createItemMeta("§5§l§nDragon Scale",
                            "§dA scale from a mighty dragon,",
                            "§dproviding protection from fire.",
                            "",
                            "§dCooldown: 120 seconds",
                            "",
                            "",
                            "§dGrants the player Haste",
                            "",
                            "",
                            "",
                            "",
                            "§9§l§ki §9§o§lMYTHIC RARITY ITEM §9§l§Ki");
                    break;
                case "strengthenblazerod":
                        material = Material.BLAZE_ROD;
                    itemMeta = createItemMeta("§e§l§nStrengthen BlazeRod",
                            "§6A rod from a blaze,",
                            "§6granting strength to its wielder.",
                            "",
                            "§6Cooldown: 120 seconds",
                            "",
                            "",
                            "§dGrants the player Fire Resistance and Strength",
                            "",
                            "",
                            "",
                            "",
                            "§9§l§ki §9§o§lMYTHIC RARITY ITEM §9§l§Ki");
                    break;
                case "chorusfruitoffloating":
                    material = Material.CHORUS_FRUIT;
                    itemMeta = createItemMeta("§a§l§nChorus Fruit of Floating",
                            "§7A fruit from the End,",
                            "§7allowing you to float gently.",
                            "",
                            "§6Cooldown: 120 seconds",
                            "",
                            "",
                            "§dGrants the player Slow Falling",
                            "",
                            "",
                            "",
                            "",
                            "§9§l§ki §9§o§lMYTHIC RARITY ITEM §9§l§Ki");
                    break;
                default:
                    return null;
            }
        } else if (category.equalsIgnoreCase("CustomPowers")) {
            switch (itemName.toLowerCase()) {
                case "wolfscream":
                    material = Material.GOAT_HORN;
                    itemMeta = createItemMeta("§b§l§nWolf Scream",
                            "§7The eternal howl of a mystical wolf,",
                            "§7that has been cursed by an evil witch",
                            "",
                            "§5Cooldown: 120 seconds",
                            "",
                            "",
                            "§dInflicts darkness upon your enemies, blinding and pushing them away",
                            "",
                            "§9§l§ki §9§o§lMYTHIC RARITY ITEM §9§l§Ki");
                    break;
                case "hollowpurple":
                    material = Material.END_CRYSTAL;
                    itemMeta = createItemMeta("§5§l§nHollow Purple",
                            "§8A powerful technique from Jujutsu Kaisen,",
                            "§8that obliterates everything in its path.",
                            "",
                            "§5Cooldown: 120 seconds",
                            "",
                            "",
                            "§dUnleashes a devastating attack.",
                            "",
                            "",
                            "",
                            "",
                            "§9§l §ki §9§o§lMYTHIC RARITY ITEM§9§l§Ki");
                    break;
                case "blueorb":
                    material = Material.BLUE_DYE; // Use an appropriate material
                    itemMeta = createItemMeta("§b§l§nBlue Orb",
                            "§bA radiant orb that emits an icy glow.",
                            "",
                            "§bLeaves a trail of icy mist and crackles with energy.",
                            "",
                            "",
                            "§5Cooldown: 300 seconds",
                            "",
                            "",
                            "",
                            "",
                            "§9§l§ki §9§o§lMYTHIC RARITY ITEM §9§l§Ki");
                    break;
                case "redorb":
                    material = Material.RED_DYE; // Use an appropriate material
                    itemMeta = createItemMeta("§c§l§nRed Orb",
                            "§cAn intense orb burning with fiery glow.",
                            "",
                            "§cGenerates faint crackling sounds and bursts of flame.",
                            "",
                            "",
                            "§5Cooldown: 300 seconds",
                            "",
                            "",
                            "",
                            "",
                            "§9§l§ki §9§o§lMYTHIC RARITY ITEM §9§l§Ki");
                    break;
                default:
                    return null;
            }
        }
        if (material != null && itemMeta != null) {
            ItemStack itemStack = new ItemStack(material);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        return null;
    }

    private ItemMeta createItemMeta(String displayName, String... loreLines) {
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.OBSIDIAN); // Use any material to create the meta
        meta.setDisplayName(displayName);
        List<String> lore = new ArrayList<>();
        for (String line : loreLines) {
            lore.add(line);
        }
        meta.setLore(lore);
        return meta;
    }

    private void applyItemEffects(Player player, ItemStack item) {
        if (item.getType() == Material.OBSIDIAN) {
            applyObsidianShardEffects(player);
        } else if (item.getType() == Material.STICK) {
            applyBambooEffects(player);
        } else if (item.getType() == Material.PRISMARINE_SHARD) {
            applyPrismarinePearlEffects(player);
        } else if (item.getType() == Material.GHAST_TEAR) {
            applyGhastTearEffects(player);
        } else if (item.getType() == Material.SHULKER_SHELL) {
            applyShulkerShellEffects(player);
        } else if (item.getType() == Material.BLACK_DYE) {
            applyDragonScaleEffects(player);
        } else if (item.getType() == Material.BLAZE_ROD) {
            applyBlazeRodEffects(player);
        } else if (item.getType() == Material.CHORUS_FRUIT) {
            applyChorusFruitEffects(player);
        }
    }

    private void applyObsidianShardEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 2400, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1200, 2));
        sendActionBar(player, "§2§lYou feel empowered by the Obsidian Block!");
    }

    private void applyBambooEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2400, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2400, 1));
        sendActionBar(player, "§2§lYou feel a surge of energy from the Healing Wand!");
    }

    private void applyPrismarinePearlEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 2400, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 2400, 1));
        sendActionBar(player, "§2§lYou can breathe underwater thanks to the Prismarine Pearl!");
    }

    private void applyGhastTearEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2400, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 2));
        sendActionBar(player, "§2§lYou feel healed by the Ghast Tear!");
    }

    private void applyShulkerShellEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 2400, 1));
        sendActionBar(player, "§2§lYou are levitating thanks to the Shulker Shell!");
    }

    private void applyDragonScaleEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 2400, 2));
        sendActionBar(player, "§2§lYou feel the power of the Ender Dragon in your hands!");
    }

    private void applyBlazeRodEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 2400, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2400, 1));
        sendActionBar(player, "§2§lYou feel stronger with the Blaze Rod!");
    }

    private void applyChorusFruitEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 600, 1));
        sendActionBar(player, "§2§lYou are floating gently thanks to the Chorus Fruit!");
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
}