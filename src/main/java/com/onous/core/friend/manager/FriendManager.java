package com.onous.core.friend.manager;

import com.onous.core.OnousCore;
import com.onous.core.friend.model.FriendData;
import com.onous.core.util.CC;
import com.onous.core.util.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * FriendManager
 * Meng-handle semua logic friend:
 *  - Data load/save
 *  - Request add friend
 *  - Accept / remove friend
 *
 * NOTE:
 *  - GUI & command akan pakai manager ini.
 */
public class FriendManager {

    private final OnousCore plugin;
    private final Map<UUID, FriendData> dataCache = new HashMap<>();

    private final File friendFolder;

    // Limit (nanti bisa disambungkan ke config)
    private final int maxFriends = 50;
    private final int maxRequests = 50;

    public FriendManager(OnousCore plugin) {
        this.plugin = plugin;
        this.friendFolder = new File(plugin.getDataFolder(), "friends");
        if (!friendFolder.exists()) {
            friendFolder.mkdirs();
        }
    }

    // ═══════════════════════════════════════
    // Load / Save FriendData
    // ═══════════════════════════════════════

    public FriendData getData(UUID owner) {
        return dataCache.computeIfAbsent(owner, this::loadData);
    }

    private FriendData loadData(UUID owner) {
        FriendData data = new FriendData(owner);
        File file = new File(friendFolder, owner.toString() + ".yml");
        if (!file.exists()) {
            return data;
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        // Friends
        List<String> friendList = cfg.getStringList("friends");
        for (String s : friendList) {
            try {
                data.getFriends().add(UUID.fromString(s));
            } catch (IllegalArgumentException ignored) {}
        }

        // Requests
        List<String> reqList = cfg.getStringList("requests");
        for (String s : reqList) {
            try {
                data.getRequests().add(UUID.fromString(s));
            } catch (IllegalArgumentException ignored) {}
        }

        return data;
    }

    public void saveData(UUID owner) {
        FriendData data = dataCache.get(owner);
        if (data == null) return;

        File file = new File(friendFolder, owner.toString() + ".yml");
        YamlConfiguration cfg = new YamlConfiguration();

        List<String> friendList = new ArrayList<>();
        for (UUID f : data.getFriends()) {
            friendList.add(f.toString());
        }
        cfg.set("friends", friendList);

        List<String> reqList = new ArrayList<>();
        for (UUID r : data.getRequests()) {
            reqList.add(r.toString());
        }
        cfg.set("requests", reqList);

        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save friend data: " + owner);
        }
    }

    public void unloadData(UUID owner) {
        saveData(owner);
        dataCache.remove(owner);
    }

    public void saveAll() {
        for (UUID uuid : dataCache.keySet()) {
            saveData(uuid);
        }
        plugin.getLogger().info("All friend data saved.");
    }

    // ═══════════════════════════════════════
    // Core Friend Logic
    // ═══════════════════════════════════════

    public boolean areFriends(UUID a, UUID b) {
        FriendData da = getData(a);
        return da.isFriend(b);
    }

    /**
     * Kirim friend request.
     *
     * Flow basic:
     *  - Cek limit friend & request
     *  - Cek sudah berteman
     *  - Cek tidak ada pending request
     *  - Tambah ke incoming request target
     *
     * Untuk GUI:
     *  - command handler akan membuka GUI di target.
     */
    public boolean sendRequest(Player sender, Player target) {
        UUID s = sender.getUniqueId();
        UUID t = target.getUniqueId();

        FriendData senderData = getData(s);
        FriendData targetData = getData(t);

        // Cek self
        if (s.equals(t)) {
            CC.sendPrefixed(sender, CC.ERROR + "You cannot add yourself as a friend.");
            Sounds.error(sender);
            return false;
        }

        // Sudah berteman?
        if (senderData.isFriend(t)) {
            CC.sendPrefixed(sender, CC.ERROR + "You are already friends.");
            Sounds.error(sender);
            return false;
        }

        // Limit friend
        if (senderData.getFriendCount() >= maxFriends) {
            CC.sendPrefixed(sender, CC.ERROR + "You reached max friend limit (" + maxFriends + ").");
            Sounds.error(sender);
            return false;
        }
        if (targetData.getFriendCount() >= maxFriends) {
            CC.sendPrefixed(sender, CC.ERROR + "That player reached max friend limit.");
            Sounds.error(sender);
            return false;
        }

        // Limit request
        if (targetData.getRequests().size() >= maxRequests) {
            CC.sendPrefixed(sender, CC.ERROR + "That player's request list is full.");
            Sounds.error(sender);
            return false;
        }

        // Sudah ada request?
        if (targetData.hasRequestFrom(s)) {
            CC.sendPrefixed(sender, CC.ERROR + "You already sent a friend request.");
            Sounds.error(sender);
            return false;
        }

        // Tambah ke incoming request target
        targetData.addRequest(s);
        saveData(t);

        // Notif sender (target akan di-handle oleh GUI/command)
        CC.sendPrefixed(sender, CC.GRAY + "Friend request sent to " + CC.WHITE + target.getName());
        Sounds.pop(sender);
        return true;
    }

    /**
     * Accept friend request:
     *  - Hapus request
     *  - Tambah A↔B di kedua FriendData
     */
    public void acceptRequest(Player target, UUID fromUUID) {
        UUID t = target.getUniqueId();
        FriendData targetData = getData(t);

        if (!targetData.hasRequestFrom(fromUUID)) {
            CC.sendPrefixed(target, CC.ERROR + "No friend request from that player.");
            Sounds.error(target);
            return;
        }

        Player from = Bukkit.getPlayer(fromUUID);
        String fromName = (from != null ? from.getName() : "Unknown");

        FriendData fromData = getData(fromUUID);

        // Hapus request
        targetData.removeRequest(fromUUID);

        // Tambah friend dua arah
        targetData.addFriend(fromUUID);
        fromData.addFriend(t);

        saveData(t);
        saveData(fromUUID);

        // Notif
        CC.sendPrefixed(target, CC.SUCCESS + "You are now friends with " + CC.WHITE + fromName);
        Sounds.success(target);

        if (from != null && from.isOnline()) {
            CC.sendPrefixed(from, CC.SUCCESS + target.getName() + CC.GRAY + " accepted your friend request.");
            Sounds.success(from);
        }
    }

    /**
     * Deny friend request:
     *  - Hapus dari list request target
     */
    public void denyRequest(Player target, UUID fromUUID) {
        UUID t = target.getUniqueId();
        FriendData targetData = getData(t);

        if (!targetData.hasRequestFrom(fromUUID)) {
            CC.sendPrefixed(target, CC.ERROR + "No friend request from that player.");
            Sounds.error(target);
            return;
        }

        targetData.removeRequest(fromUUID);
        saveData(t);

        Player from = Bukkit.getPlayer(fromUUID);
        String fromName = (from != null ? from.getName() : "Unknown");

        CC.sendPrefixed(target, CC.GRAY + "You denied friend request from " + CC.WHITE + fromName);
        Sounds.pop(target);

        if (from != null && from.isOnline()) {
            CC.sendPrefixed(from, CC.ERROR + target.getName() + CC.GRAY + " denied your friend request.");
            Sounds.pop(from);
        }
    }

    /**
     * Remove friend (dua arah)
     */
    public void removeFriend(Player remover, Player other) {
        UUID r = remover.getUniqueId();
        UUID o = other.getUniqueId();

        FriendData rd = getData(r);
        FriendData od = getData(o);

        if (!rd.isFriend(o)) {
            CC.sendPrefixed(remover, CC.ERROR + "You are not friends.");
            Sounds.error(remover);
            return;
        }

        rd.removeFriend(o);
        od.removeFriend(r);

        saveData(r);
        saveData(o);

        CC.sendPrefixed(remover, CC.GRAY + "You removed " + CC.WHITE + other.getName() + CC.GRAY + " from your friends.");
        Sounds.pop(remover);

        if (other.isOnline()) {
            CC.sendPrefixed(other, CC.GRAY + remover.getName() + " removed you from their friends.");
            Sounds.pop(other);
        }
    }
}
