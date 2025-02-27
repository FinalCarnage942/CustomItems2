package me.Carnage.cCIP1;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class CustomItems {

    private final ConfigManager configManager;

    public CustomItems(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public static ItemStack getItem(String itemName, ConfigManager configManager) {
        Material material = null;
        ItemMeta itemMeta = null;

        switch (itemName.toLowerCase()) {
            case "wolfscream":
                material = Material.GOAT_HORN;
                itemMeta = createItemMeta(configManager.getItemName(Material.GOAT_HORN), configManager.getItemDescription(Material.GOAT_HORN));
                break;
            case "hollowpurple":
                material = Material.END_CRYSTAL;
                itemMeta = createItemMeta(configManager.getItemName(Material.END_CRYSTAL), configManager.getItemDescription(Material.END_CRYSTAL));
                break;
            case "blueorb":
                material = Material.BLUE_DYE; // Use an appropriate material
                itemMeta = createItemMeta(configManager.getItemName(Material.BLUE_DYE), configManager.getItemDescription(Material.BLUE_DYE));
                break;
            case "redorb":
                material = Material.RED_DYE; // Use an appropriate material
                itemMeta = createItemMeta(configManager.getItemName(Material.RED_DYE), configManager.getItemDescription(Material.RED_DYE));
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
