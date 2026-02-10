package com.onous.core.gui;

import com.onous.core.OnousCore;
import com.onous.core.util.CC;
import com.onous.core.util.ItemBuilder;
import com.onous.core.util.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BalanceGUI extends GUI {

    public BalanceGUI(OnousCore plugin, Player player) {
        super(plugin, player, "&8Balance Top", 6);
    }

    @Override
    public void setup() {
        List<Map.Entry<UUID, Double>> top = plugin.getEcoManager().getTopBalances(45);

        for (int i = 0; i < top.size(); i++) {
            Map.Entry<UUID, Double> entry = top.get(i);
            String name = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            if (name == null) name = "Unknown";

            ItemStack head = new ItemBuilder(Material.PLAYER_HEAD)
                    .name("&e#" + (i + 1) + " &f" + name)
                    .lore("&7Balance: &a" + plugin.getEcoManager().format(entry.getValue()))
                    .build();
            
            // Set skull owner manually
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(entry.getKey()));
            head.setItemMeta(meta);

            setItem(i, head);
        }

        // Close Button
        setItem(49, new ItemBuilder(Material.BARRIER).name("&cClose").build());
    }

    @Override
    public void onClick(int slot) {
        if (slot == 49) {
            close();
            Sounds.click(player);
        }
    }
}
