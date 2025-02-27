package me.Carnage.cCIPWolv;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class CustomItems {

    public static ItemStack getItem(String itemName) {
        Material material = null;
        ItemMeta itemMeta = null;

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
