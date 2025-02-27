package me.Carnage.cCIPWolv;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class VanillaItems {

    public static ItemStack getItem(String itemName) {
        Material material = null;
        ItemMeta itemMeta = null;

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
