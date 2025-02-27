package me.Carnage.cCIP1;

import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;

public class Utility {

    public static void sendActionBar(Player player, String message) {
        player.sendActionBar(Component.text(message));
    }
}
