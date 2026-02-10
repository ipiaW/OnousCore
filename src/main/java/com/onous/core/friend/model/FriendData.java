package com.onous.core.friend.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * FriendData
 * Menyimpan list teman & pending request untuk 1 player (owner).
 *
 * Relasi FRIEND dua arah:
 *  - Jika A add B dan B accept, maka:
 *      FriendData(A).friends mengandung B
 *      FriendData(B).friends mengandung A
 *
 * Field:
 *  - friends  : teman yang sudah confirmed
 *  - requests : incoming request (siapa saja yang meng-add owner)
 */
public class FriendData {

    private final UUID owner;
    private final Set<UUID> friends;
    private final Set<UUID> requests;

    public FriendData(UUID owner) {
        this.owner = owner;
        this.friends = new HashSet<>();
        this.requests = new HashSet<>();
    }

    public UUID getOwner() {
        return owner;
    }

    public Set<UUID> getFriends() {
        return friends;
    }

    public Set<UUID> getRequests() {
        return requests;
    }

    public boolean isFriend(UUID other) {
        return friends.contains(other);
    }

    public void addFriend(UUID other) {
        friends.add(other);
    }

    public void removeFriend(UUID other) {
        friends.remove(other);
    }

    public boolean hasRequestFrom(UUID other) {
        return requests.contains(other);
    }

    public void addRequest(UUID from) {
        requests.add(from);
    }

    public void removeRequest(UUID from) {
        requests.remove(from);
    }

    public void clearRequests() {
        requests.clear();
    }

    public int getFriendCount() {
        return friends.size();
    }
}
