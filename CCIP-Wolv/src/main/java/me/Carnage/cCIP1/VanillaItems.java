package me.Carnage.cCIP1;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class VanillaItems {

    private final ConfigManager configManager;

    public VanillaItems(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public static ItemStack getItem(String itemName, ConfigManager configManager) {
        Material material = null;
        ItemMeta itemMeta = null;

        switch (itemName.toLowerCase()) {
            case "obsidianshard":
                material = Material.OBSIDIAN;
                itemMeta = createItemMeta(configManager.getItemName(Material.OBSIDIAN), configManager.getItemDescription(Material.OBSIDIAN));
                break;
            case "healingwand":
                material = Material.STICK;
                itemMeta = createItemMeta(configManager.getItemName(Material.STICK), configManager.getItemDescription(Material.STICK));
                break;
            case "prismarinepearl":
                material = Material.PRISMARINE_SHARD;
                itemMeta = createItemMeta(configManager.getItemName(Material.PRISMARINE_SHARD), configManager.getItemDescription(Material.PRISMARINE_SHARD));
                break;
            case "healingtears":
                material = Material.GHAST_TEAR;
                itemMeta = createItemMeta(configManager.getItemName(Material.GHAST_TEAR), configManager.getItemDescription(Material.GHAST_TEAR));
                break;
            case "shulkershell":
                material = Material.SHULKER_SHELL;
                itemMeta = createItemMeta(configManager.getItemName(Material.SHULKER_SHELL), configManager.getItemDescription(Material.SHULKER_SHELL));
                break;
            case "dragonscale":
                material = Material.BLACK_DYE;
                itemMeta = createItemMeta(configManager.getItemName(Material.BLACK_DYE), configManager.getItemDescription(Material.BLACK_DYE));
                break;
            case "strengthenblazerod":
                material = Material.BLAZE_ROD;
                itemMeta = createItemMeta(configManager.getItemName(Material.BLAZE_ROD), configManager.getItemDescription(Material.BLAZE_ROD));
                break;
            case "chorusfruitoffloating":
                material = Material.CHORUS_FRUIT;
                itemMeta = createItemMeta(configManager.getItemName(Material.CHORUS_FRUIT), configManager.getItemDescription(Material.CHORUS_FRUIT));
                break;
            default:
                return null;
        }
        if (material != null && itemMeta != null) {
            ItemStack itemStack = new ItemStack(material);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        return null;
    }

    private static ItemMeta createItemMeta(String displayName, String... loreLines) {
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.OBSIDIAN);
        meta.displayName(Component.text(displayName));
        List<Component> lore = new ArrayList<>();
        for (String line : loreLines) {
            lore.add(Component.text(line));
        }
        meta.lore(lore);
        return meta;
    }
}
