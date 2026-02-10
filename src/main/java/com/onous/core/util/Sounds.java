package com.onous.core.util;

import com.onous.core.OnousCore;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Sound Utility
 * Play sounds with theme consistency
 */
public final class Sounds {

    // ═══════════════════════════════════════
    // Private Constructor
    // ═══════════════════════════════════════
    private Sounds() {
        // Utility class
    }

    // ═══════════════════════════════════════
    // Core Play Method
    // ═══════════════════════════════════════
    
    /**
     * Play sound to player
     * 
     * @param player Target player
     * @param sound Sound to play
     * @param volume Volume (0.0 - 1.0)
     * @param pitch Pitch (0.5 - 2.0)
     */
    public static void play(Player player, Sound sound, float volume, float pitch) {
        if (player == null || sound == null) return;
        
        // Check if sounds enabled in config
        if (!OnousCore.get().getConfig().getBoolean("sounds.enabled", true)) {
            return;
        }

        // Check if player has sounds enabled in settings
        if (!OnousCore.get().getData().getSettings(player.getUniqueId()).isSoundEnabled()) {
            return;
        }

        // Get volume from config
        float configVolume = (float) OnousCore.get().getConfig().getDouble("sounds.volume", 0.5);
        
        player.playSound(player.getLocation(), sound, volume * configVolume, pitch);
    }

    /**
     * Play sound with default volume and pitch
     */
    public static void play(Player player, Sound sound) {
        play(player, sound, 1.0f, 1.0f);
    }

    // ═══════════════════════════════════════
    // UI Sounds
    // ═══════════════════════════════════════
    
    /**
     * GUI open sound
     */
    public static void open(Player player) {
        play(player, Sound.BLOCK_CHEST_OPEN, 0.5f, 1.2f);
    }

    /**
     * GUI close sound
     */
    public static void close(Player player) {
        play(player, Sound.BLOCK_CHEST_CLOSE, 0.5f, 1.2f);
    }

    /**
     * Button click sound
     */
    public static void click(Player player) {
        play(player, Sound.UI_BUTTON_CLICK, 0.6f, 1.0f);
    }

    /**
     * Page turn sound
     */
    public static void page(Player player) {
        play(player, Sound.ITEM_BOOK_PAGE_TURN, 0.8f, 1.0f);
    }

    // ═══════════════════════════════════════
    // Feedback Sounds
    // ═══════════════════════════════════════
    
    /**
     * Success sound
     */
    public static void success(Player player) {
        play(player, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2.0f);
    }

    /**
     * Error sound
     */
    public static void error(Player player) {
        play(player, Sound.ENTITY_VILLAGER_NO, 0.6f, 1.0f);
    }

    /**
     * Warning sound
     */
    public static void warning(Player player) {
        play(player, Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.5f);
    }

    /**
     * Notification sound
     */
    public static void notify(Player player) {
        play(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6f, 1.2f);
    }

    /**
     * Pop sound (light notification)
     */
    public static void pop(Player player) {
        play(player, Sound.ENTITY_CHICKEN_EGG, 0.5f, 1.5f);
    }

    // ═══════════════════════════════════════
    // Action Sounds
    // ═══════════════════════════════════════
    
    /**
     * Teleport sound
     */
    public static void teleport(Player player) {
        play(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 1.0f);
    }

    /**
     * Heal sound
     */
    public static void heal(Player player) {
        play(player, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
    }

    /**
     * Eat/Feed sound
     */
    public static void feed(Player player) {
        play(player, Sound.ENTITY_PLAYER_BURP, 0.6f, 1.0f);
    }

    /**
     * Fly enable sound
     */
    public static void flyOn(Player player) {
        play(player, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.4f, 1.5f);
    }

    /**
     * Fly disable sound
     */
    public static void flyOff(Player player) {
        play(player, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.4f, 0.8f);
    }

    // ═══════════════════════════════════════
    // Message Sounds
    // ═══════════════════════════════════════
    
    /**
     * Private message receive sound
     */
    public static void message(Player player) {
        play(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.8f);
    }

    /**
     * TPA request sound
     */
    public static void tpaRequest(Player player) {
        play(player, Sound.BLOCK_NOTE_BLOCK_BELL, 0.7f, 1.0f);
    }

    // ═══════════════════════════════════════
    // Toggle Sounds
    // ═══════════════════════════════════════
    
    /**
     * Toggle on sound
     */
    public static void toggleOn(Player player) {
        play(player, Sound.BLOCK_LEVER_CLICK, 0.6f, 1.5f);
    }

    /**
     * Toggle off sound
     */
    public static void toggleOff(Player player) {
        play(player, Sound.BLOCK_LEVER_CLICK, 0.6f, 0.8f);
    }

    // ═══════════════════════════════════════
    // Countdown Sounds
    // ═══════════════════════════════════════
    
    /**
     * Countdown tick sound
     */
    public static void tick(Player player) {
        play(player, Sound.BLOCK_NOTE_BLOCK_HAT, 0.5f, 1.0f);
    }

    /**
     * Countdown finish sound
     */
    public static void countdown(Player player, int secondsLeft) {
        if (secondsLeft <= 0) {
            success(player);
        } else if (secondsLeft <= 3) {
            play(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.6f, 1.0f + (0.2f * (3 - secondsLeft)));
        } else {
            tick(player);
        }
    }
}