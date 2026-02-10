package com.onous.core.listener;

import com.onous.core.OnousCore;
import com.onous.core.gui.GUI;
import com.onous.core.util.Sounds;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * GUI Listener
 * Handles all GUI interactions
 */
public class GUIListener implements Listener {

    // ═══════════════════════════════════════
    // Fields
    // ═══════════════════════════════════════
    private final OnousCore plugin;

    // ═══════════════════════════════════════
    // Constructor
    // ═══════════════════════════════════════
    
    public GUIListener(OnousCore plugin) {
        this.plugin = plugin;
    }

    // ═══════════════════════════════════════
    // Inventory Click
    // ═══════════════════════════════════════
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        // ─────────────────────────────────────
        // Basic Checks
        // ─────────────────────────────────────
        
        // Check if clicker is player
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        // Get inventory holder
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        // Check if our GUI
        if (!(holder instanceof GUI gui)) {
            return;
        }

        // ─────────────────────────────────────
        // Cancel Event
        // ─────────────────────────────────────
        
        // Always cancel to prevent item taking
        event.setCancelled(true);

        // ─────────────────────────────────────
        // Validate Click
        // ─────────────────────────────────────
        
        // Ignore clicks outside inventory
        if (event.getClickedInventory() == null) {
            return;
        }

        // Ignore clicks in player's own inventory
        if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
            return;
        }

        // Ignore if slot is negative (outside GUI)
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= inventory.getSize()) {
            return;
        }

        // ─────────────────────────────────────
        // Handle Click
        // ─────────────────────────────────────
        
        // Call GUI onClick
        gui.onClick(slot);
    }

    // ═══════════════════════════════════════
    // Inventory Drag
    // ═══════════════════════════════════════
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        // ─────────────────────────────────────
        // Basic Checks
        // ─────────────────────────────────────
        
        // Check if dragger is player
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        // Get inventory holder
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        // Check if our GUI
        if (!(holder instanceof GUI)) {
            return;
        }

        // ─────────────────────────────────────
        // Cancel Drag in GUI
        // ─────────────────────────────────────
        
        // Check if any dragged slot is in GUI
        for (int slot : event.getRawSlots()) {
            if (slot < inventory.getSize()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    // ═══════════════════════════════════════
    // Inventory Close
    // ═══════════════════════════════════════
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent event) {
        // ─────────────────────────────────────
        // Basic Checks
        // ─────────────────────────────────────
        
        // Check if closer is player
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        // Get inventory holder
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        // Check if our GUI
        if (!(holder instanceof GUI gui)) {
            return;
        }

        // ─────────────────────────────────────
        // Handle Close
        // ─────────────────────────────────────
        
        // Optional: Play close sound
        // Sounds.close(player);
        
        // Optional: Save data on close
        // plugin.getData().savePlayer(player.getUniqueId());
    }
}
