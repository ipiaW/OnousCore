package com.onous.core.economy;

import com.onous.core.OnousCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EcoManager {

    private final OnousCore plugin;
    private final File file;
    private YamlConfiguration config;
    private final Map<UUID, Double> balanceCache = new HashMap<>();

    public EcoManager(OnousCore plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "economy.yml");
        load();
    }

    public void load() {
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        config = YamlConfiguration.loadConfiguration(file);
        
        balanceCache.clear();
        if (config.contains("balances")) {
            for (String key : config.getConfigurationSection("balances").getKeys(false)) {
                try {
                    balanceCache.put(UUID.fromString(key), config.getDouble("balances." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save() {
        for (Map.Entry<UUID, Double> entry : balanceCache.entrySet()) {
            config.set("balances." + entry.getKey().toString(), entry.getValue());
        }
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public double getBalance(UUID uuid) {
        return balanceCache.getOrDefault(uuid, plugin.getConfig().getDouble("economy.starting-balance", 1000.0));
    }

    public double getBalance(OfflinePlayer player) {
        return getBalance(player.getUniqueId());
    }

    public void setBalance(UUID uuid, double amount) {
        balanceCache.put(uuid, Math.max(0, amount));
        save();
    }

    public void setBalance(OfflinePlayer player, double amount) {
        setBalance(player.getUniqueId(), amount);
    }

    public void deposit(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public void deposit(OfflinePlayer player, double amount) {
        deposit(player.getUniqueId(), amount);
    }

    public void withdraw(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) - amount);
    }

    public void withdraw(OfflinePlayer player, double amount) {
        withdraw(player.getUniqueId(), amount);
    }

    public boolean has(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }

    public boolean has(OfflinePlayer player, double amount) {
        return has(player.getUniqueId(), amount);
    }

    public String format(double amount) {
        if (amount >= 1_000_000_000) return String.format("%.1fB", amount / 1_000_000_000);
        if (amount >= 1_000_000) return String.format("%.1fM", amount / 1_000_000);
        if (amount >= 1_000) return String.format("%.1fk", amount / 1_000);
        return String.format("%.0f", amount);
    }

    public List<Map.Entry<UUID, Double>> getTopBalances(int limit) {
        List<Map.Entry<UUID, Double>> list = new ArrayList<>(balanceCache.entrySet());
        list.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return list.subList(0, Math.min(limit, list.size()));
    }
	
	    // Method baru untuk parsing suffix (k, m, b, t)
    public double parseAmount(String input) {
        if (input == null || input.isEmpty()) return 0;
        
        input = input.toLowerCase().replace(",", ""); // support 1,000
        double multiplier = 1;

        if (input.endsWith("k")) {
            multiplier = 1_000;
            input = input.substring(0, input.length() - 1);
        } else if (input.endsWith("m")) {
            multiplier = 1_000_000;
            input = input.substring(0, input.length() - 1);
        } else if (input.endsWith("b")) {
            multiplier = 1_000_000_000;
            input = input.substring(0, input.length() - 1);
        } else if (input.endsWith("t")) {
            multiplier = 1_000_000_000_000L;
            input = input.substring(0, input.length() - 1);
        }

        try {
            return Double.parseDouble(input) * multiplier;
        } catch (NumberFormatException e) {
            return -1; // Indikator error
        }
    }
	
}
