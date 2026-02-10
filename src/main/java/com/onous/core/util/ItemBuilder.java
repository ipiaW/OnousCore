package com.onous.core.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fluent Item Builder
 * Easy way to create ItemStacks
 */
public class ItemBuilder {

    // ═══════════════════════════════════════
    // Fields
    // ═══════════════════════════════════════
    private final ItemStack item;
    private final ItemMeta meta;

    // ═══════════════════════════════════════
    // Constructors
    // ═══════════════════════════════════════
    
    /**
     * Create builder from material
     */
    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    /**
     * Create builder from existing item
     */
    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }

    // ═══════════════════════════════════════
    // Basic Properties
    // ═══════════════════════════════════════
    
    /**
     * Set display name
     */
    public ItemBuilder name(String name) {
        if (meta != null && name != null) {
            meta.setDisplayName(CC.translate(name));
        }
        return this;
    }

    /**
     * Set lore from varargs
     */
    public ItemBuilder lore(String... lines) {
        if (meta != null && lines != null) {
            List<String> lore = new ArrayList<>();
            for (String line : lines) {
                lore.add(CC.translate(line));
            }
            meta.setLore(lore);
        }
        return this;
    }

    /**
     * Set lore from list
     */
    public ItemBuilder lore(List<String> lines) {
        if (meta != null && lines != null) {
            List<String> lore = new ArrayList<>();
            for (String line : lines) {
                lore.add(CC.translate(line));
            }
            meta.setLore(lore);
        }
        return this;
    }

    /**
     * Add single lore line
     */
    public ItemBuilder addLore(String line) {
        if (meta != null && line != null) {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(CC.translate(line));
            meta.setLore(lore);
        }
        return this;
    }

    /**
     * Set amount
     */
    public ItemBuilder amount(int amount) {
        item.setAmount(Math.max(1, Math.min(64, amount)));
        return this;
    }

    // ═══════════════════════════════════════
    // Enchantments & Flags
    // ═══════════════════════════════════════
    
    /**
     * Add enchantment glow effect
     */
    public ItemBuilder glow() {
        if (meta != null) {
            meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    /**
     * Add enchantment
     */
    public ItemBuilder enchant(Enchantment enchantment, int level) {
        if (meta != null && enchantment != null) {
            meta.addEnchant(enchantment, level, true);
        }
        return this;
    }

    /**
     * Hide all flags
     */
    public ItemBuilder hideFlags() {
        if (meta != null) {
            meta.addItemFlags(ItemFlag.values());
        }
        return this;
    }

    /**
     * Add specific flag
     */
    public ItemBuilder addFlag(ItemFlag flag) {
        if (meta != null && flag != null) {
            meta.addItemFlags(flag);
        }
        return this;
    }

    /**
     * Set unbreakable
     */
    public ItemBuilder unbreakable() {
        if (meta != null) {
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        return this;
    }

    /**
     * Set custom model data
     */
    public ItemBuilder customModelData(int data) {
        if (meta != null) {
            meta.setCustomModelData(data);
        }
        return this;
    }

    // ═══════════════════════════════════════
    // Skull
    // ═══════════════════════════════════════
    
    /**
     * Set skull owner
     */
    public ItemBuilder skull(OfflinePlayer player) {
        if (meta instanceof SkullMeta skullMeta && player != null) {
            skullMeta.setOwningPlayer(player);
        }
        return this;
    }

    // ═══════════════════════════════════════
    // Build
    // ═══════════════════════════════════════
    
    /**
     * Build the ItemStack
     */
    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }

    // ═══════════════════════════════════════
    // Static Quick Builders
    // ═══════════════════════════════════════
    
    /**
     * Create filler glass pane (orange theme)
     */
    public static ItemStack filler() {
        return new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE)
                .name(" ")
                .build();
    }

    /**
     * Create filler with custom color
     */
    public static ItemStack filler(Material glassMaterial) {
        return new ItemBuilder(glassMaterial)
                .name(" ")
                .build();
    }

    /**
     * Create close button
     */
    public static ItemStack close() {
        return new ItemBuilder(Material.BARRIER)
                .name(CC.ERROR + "✕ " + CC.GRAY + "Close")
                .lore(
                        "",
                        CC.DARK + "Click to close menu"
                )
                .build();
    }

    /**
     * Create back button
     */
    public static ItemStack back() {
        return new ItemBuilder(Material.ARROW)
                .name(CC.GRAY + "← Back")
                .lore(
                        "",
                        CC.DARK + "Click to go back"
                )
                .build();
    }

    /**
     * Create previous page button
     */
    public static ItemStack previousPage(int currentPage) {
        return new ItemBuilder(Material.ARROW)
                .name(CC.PRIMARY + "← " + CC.GRAY + "Previous Page")
                .lore(
                        "",
                        CC.DARK + "Current: Page " + currentPage
                )
                .build();
    }

    /**
     * Create next page button
     */
    public static ItemStack nextPage(int currentPage) {
        return new ItemBuilder(Material.ARROW)
                .name(CC.GRAY + "Next Page " + CC.PRIMARY + "→")
                .lore(
                        "",
                        CC.DARK + "Current: Page " + currentPage
                )
                .build();
    }

    /**
     * Create page info item
     */
    public static ItemStack pageInfo(int currentPage, int totalPages) {
        return new ItemBuilder(Material.PAPER)
                .name(CC.PRIMARY + "Page " + CC.WHITE + currentPage + CC.GRAY + "/" + CC.WHITE + totalPages)
                .build();
    }

    /**
     * Create confirm button
     */
    public static ItemStack confirm() {
        return new ItemBuilder(Material.LIME_CONCRETE)
                .name(CC.SUCCESS + "✓ " + CC.WHITE + "Confirm")
                .lore(
                        "",
                        CC.DARK + "Click to confirm"
                )
                .build();
    }

    /**
     * Create cancel button
     */
    public static ItemStack cancel() {
        return new ItemBuilder(Material.RED_CONCRETE)
                .name(CC.ERROR + "✕ " + CC.WHITE + "Cancel")
                .lore(
                        "",
                        CC.DARK + "Click to cancel"
                )
                .build();
    }

    /**
     * Create toggle button
     */
    public static ItemStack toggle(String name, boolean enabled, String... description) {
        Material material = enabled ? Material.LIME_DYE : Material.GRAY_DYE;
        String status = enabled ? CC.SUCCESS + "● Enabled" : CC.ERROR + "● Disabled";
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.addAll(Arrays.asList(description));
        lore.add("");
        lore.add(status);
        lore.add("");
        lore.add(CC.DARK + "Click to toggle");
        
        return new ItemBuilder(material)
                .name(CC.PRIMARY + name)
                .lore(lore)
                .build();
    }

    /**
     * Create player head
     */
    public static ItemStack playerHead(OfflinePlayer player, String name, String... lore) {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .skull(player)
                .name(name)
                .lore(lore)
                .build();
    }
}