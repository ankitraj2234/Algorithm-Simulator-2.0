package com.simulator;

import java.util.*;

/**
 * Hash Table data structure implementation using separate chaining.
 * Supports key-value pairs with collision handling through linked lists.
 */
public class HashTableModel {

    private List<List<Entry>> buckets;
    private int capacity;
    private int size;
    private int collisionCount;

    public HashTableModel() {
        this(10); // Default capacity
    }

    public HashTableModel(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.collisionCount = 0;
        this.buckets = new ArrayList<>(capacity);

        // Initialize buckets
        for (int i = 0; i < capacity; i++) {
            buckets.add(new ArrayList<>());
        }
    }

    /**
     * Compute hash index for a key.
     */
    public int hash(String key) {
        return Math.abs(key.hashCode() % capacity);
    }

    /**
     * Insert or update a key-value pair.
     */
    public void put(String key, String value) {
        int index = hash(key);
        List<Entry> bucket = buckets.get(index);

        // Check if key exists and update
        for (Entry entry : bucket) {
            if (entry.key.equals(key)) {
                entry.value = value;
                System.out.println("Updated: " + key + " -> " + value + " at bucket " + index);
                return;
            }
        }

        // Track collision if bucket not empty
        if (!bucket.isEmpty()) {
            collisionCount++;
            System.out.println("⚠️ Collision detected at bucket " + index + " for key: " + key);
        }

        // Insert new entry
        bucket.add(new Entry(key, value));
        size++;
        System.out.println("Inserted: " + key + " -> " + value + " at bucket " + index);
    }

    /**
     * Get value for a key.
     */
    public String get(String key) {
        int index = hash(key);
        List<Entry> bucket = buckets.get(index);

        for (Entry entry : bucket) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }
        return null; // Key not found
    }

    /**
     * Remove a key-value pair.
     */
    public boolean remove(String key) {
        int index = hash(key);
        List<Entry> bucket = buckets.get(index);

        Iterator<Entry> iterator = bucket.iterator();
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            if (entry.key.equals(key)) {
                iterator.remove();
                size--;
                System.out.println("Removed: " + key + " from bucket " + index);
                return true;
            }
        }
        return false; // Key not found
    }

    /**
     * Check if key exists.
     */
    public boolean containsKey(String key) {
        return get(key) != null;
    }

    public int size() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getCollisionCount() {
        return collisionCount;
    }

    public double getLoadFactor() {
        return (double) size / capacity;
    }

    public void clear() {
        for (List<Entry> bucket : buckets) {
            bucket.clear();
        }
        size = 0;
        collisionCount = 0;
        System.out.println("Hash table cleared");
    }

    /**
     * Get all entries in a specific bucket.
     */
    public List<Entry> getBucket(int index) {
        if (index >= 0 && index < capacity) {
            return new ArrayList<>(buckets.get(index));
        }
        return new ArrayList<>();
    }

    /**
     * Get bucket counts for visualization.
     */
    public int[] getBucketCounts() {
        int[] counts = new int[capacity];
        for (int i = 0; i < capacity; i++) {
            counts[i] = buckets.get(i).size();
        }
        return counts;
    }

    /**
     * Get all entries.
     */
    public List<Entry> getAllEntries() {
        List<Entry> allEntries = new ArrayList<>();
        for (List<Entry> bucket : buckets) {
            allEntries.addAll(bucket);
        }
        return allEntries;
    }

    /**
     * Get all keys.
     */
    public Set<String> keySet() {
        Set<String> keys = new HashSet<>();
        for (List<Entry> bucket : buckets) {
            for (Entry entry : bucket) {
                keys.add(entry.key);
            }
        }
        return keys;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("HashTable {\n");
        for (int i = 0; i < capacity; i++) {
            sb.append("  Bucket ").append(i).append(": ");
            if (buckets.get(i).isEmpty()) {
                sb.append("[empty]");
            } else {
                sb.append(buckets.get(i));
            }
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Entry class for key-value pairs.
     */
    public static class Entry {
        private String key;
        private String value;

        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }
}
