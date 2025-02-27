package me.Carnage.cCIP1;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemEffects {

    public static void applyEffects(Player player, ItemStack item, ConfigManager configManager) {
        Material material = item.getType();
        String itemName = configManager.getItemName(material);

        switch (material) {
            case OBSIDIAN:
                applyObsidianShardEffects(player, configManager);
                break;
            case STICK:
                applyBambooEffects(player, configManager);
                break;
            case PRISMARINE_SHARD:
                applyPrismarinePearlEffects(player, configManager);
                break;
            case GHAST_TEAR:
                applyGhastTearEffects(player, configManager);
                break;
            case SHULKER_SHELL:
                applyShulkerShellEffects(player, configManager);
                break;
            case BLACK_DYE:
                applyDragonScaleEffects(player, configManager);
                break;
            case BLAZE_ROD:
                applyBlazeRodEffects(player, configManager);
                break;
            case CHORUS_FRUIT:
                applyChorusFruitEffects(player, configManager);
                break;
            default:
                break;
        }
    }

    private static void applyObsidianShardEffects(Player player, ConfigManager configManager) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, configManager.getItemEffectDuration(Material.OBSIDIAN, "STRENGTH"), configManager.getItemEffectAmplifier(Material.OBSIDIAN, "STRENGTH")));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, configManager.getItemEffectDuration(Material.OBSIDIAN, "SLOWNESS"), configManager.getItemEffectAmplifier(Material.OBSIDIAN, "SLOWNESS")));
        Utility.sendActionBar(player, configManager.getItemEffectMessage(Material.OBSIDIAN, "STRENGTH"));
    }

    private static void applyBambooEffects(Player player, ConfigManager configManager) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, configManager.getItemEffectDuration(Material.STICK, "REGENERATION"), configManager.getItemEffectAmplifier(Material.STICK, "REGENERATION")));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, configManager.getItemEffectDuration(Material.STICK, "SPEED"), configManager.getItemEffectAmplifier(Material.STICK, "SPEED")));
        Utility.sendActionBar(player, configManager.getItemEffectMessage(Material.STICK, "REGENERATION"));
    }

    private static void applyPrismarinePearlEffects(Player player, ConfigManager configManager) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, configManager.getItemEffectDuration(Material.PRISMARINE_SHARD, "WATER_BREATHING"), configManager.getItemEffectAmplifier(Material.PRISMARINE_SHARD, "WATER_BREATHING")));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, configManager.getItemEffectDuration(Material.PRISMARINE_SHARD, "DOLPHINS_GRACE"), configManager.getItemEffectAmplifier(Material.PRISMARINE_SHARD, "DOLPHINS_GRACE")));
        Utility.sendActionBar(player, configManager.getItemEffectMessage(Material.PRISMARINE_SHARD, "WATER_BREATHING"));
    }

    private static void applyGhastTearEffects(Player player, ConfigManager configManager) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, configManager.getItemEffectDuration(Material.GHAST_TEAR, "REGENERATION"), configManager.getItemEffectAmplifier(Material.GHAST_TEAR, "REGENERATION")));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, configManager.getItemEffectDuration(Material.GHAST_TEAR, "ABSORPTION"), configManager.getItemEffectAmplifier(Material.GHAST_TEAR, "ABSORPTION")));
        Utility.sendActionBar(player, configManager.getItemEffectMessage(Material.GHAST_TEAR, "REGENERATION"));
    }

    private static void applyShulkerShellEffects(Player player, ConfigManager configManager) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, configManager.getItemEffectDuration(Material.SHULKER_SHELL, "LEVITATION"), configManager.getItemEffectAmplifier(Material.SHULKER_SHELL, "LEVITATION")));
        Utility.sendActionBar(player, configManager.getItemEffectMessage(Material.SHULKER_SHELL, "LEVITATION"));
    }

    private static void applyDragonScaleEffects(Player player, ConfigManager configManager) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, configManager.getItemEffectDuration(Material.BLACK_DYE, "HASTE"), configManager.getItemEffectAmplifier(Material.BLACK_DYE, "HASTE")));
        Utility.sendActionBar(player, configManager.getItemEffectMessage(Material.BLACK_DYE, "HASTE"));
    }

    private static void applyBlazeRodEffects(Player player, ConfigManager configManager) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, configManager.getItemEffectDuration(Material.BLAZE_ROD, "STRENGTH"), configManager.getItemEffectAmplifier(Material.BLAZE_ROD, "STRENGTH")));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, configManager.getItemEffectDuration(Material.BLAZE_ROD, "FIRE_RESISTANCE"), configManager.getItemEffectAmplifier(Material.BLAZE_ROD, "FIRE_RESISTANCE")));
        Utility.sendActionBar(player, configManager.getItemEffectMessage(Material.BLAZE_ROD, "STRENGTH"));
    }

    private static void applyChorusFruitEffects(Player player, ConfigManager configManager) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, configManager.getItemEffectDuration(Material.CHORUS_FRUIT, "SLOW_FALLING"), configManager.getItemEffectAmplifier(Material.CHORUS_FRUIT, "SLOW_FALLING")));
        Utility.sendActionBar(player, configManager.getItemEffectMessage(Material.CHORUS_FRUIT, "SLOW_FALLING"));
    }
}
