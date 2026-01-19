package com.simulator;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository of Hash Table code examples with comprehensive implementations.
 */
public class HashTableCodeRepository {
    private final Map<String, CodeExample> codeExamples;

    public HashTableCodeRepository() {
        codeExamples = new HashMap<>();
        initializeCodeExamples();
    }

    private void initializeCodeExamples() {
        // =============== BASIC HASH TABLE OPERATIONS ===============
        codeExamples.put("Create Hash Table", new CodeExample(
                "Create Hash Table",
                "Basic Hash Table structure with separate chaining",
                "O(1) average",
                """
                        // Hash Table implementation with Separate Chaining
                        import java.util.LinkedList;

                        public class HashTable<K, V> {
                            private LinkedList<Entry<K, V>>[] buckets;
                            private int capacity;
                            private int size;

                            public HashTable() {
                                this(16); // Default capacity
                            }

                            @SuppressWarnings("unchecked")
                            public HashTable(int capacity) {
                                this.capacity = capacity;
                                this.size = 0;
                                this.buckets = new LinkedList[capacity];

                                // Initialize each bucket
                                for (int i = 0; i < capacity; i++) {
                                    buckets[i] = new LinkedList<>();
                                }
                            }

                            // Hash function
                            private int hash(K key) {
                                return Math.abs(key.hashCode() % capacity);
                            }

                            public int size() { return size; }
                            public boolean isEmpty() { return size == 0; }

                            public double getLoadFactor() {
                                return (double) size / capacity;
                            }

                            // Entry class
                            static class Entry<K, V> {
                                K key;
                                V value;

                                Entry(K key, V value) {
                                    this.key = key;
                                    this.value = value;
                                }
                            }
                        }

                        // Usage Example:
                        HashTable<String, Integer> ht = new HashTable<>(10);
                        System.out.println("Size: " + ht.size()); // 0
                        System.out.println("Load Factor: " + ht.getLoadFactor()); // 0.0
                        """));

        codeExamples.put("Put Operation", new CodeExample(
                "Put/Insert Operation",
                "Insert or update key-value pair in hash table",
                "O(1) average, O(n) worst",
                """
                        // Put operation - insert or update
                        public void put(K key, V value) {
                            if (key == null) {
                                throw new IllegalArgumentException("Key cannot be null");
                            }

                            int bucketIndex = hash(key);
                            LinkedList<Entry<K, V>> bucket = buckets[bucketIndex];

                            // Check if key exists and update
                            for (Entry<K, V> entry : bucket) {
                                if (entry.key.equals(key)) {
                                    entry.value = value;  // Update existing
                                    System.out.println("Updated: " + key + " at bucket " + bucketIndex);
                                    return;
                                }
                            }

                            // Key doesn't exist, insert new entry
                            bucket.add(new Entry<>(key, value));
                            size++;

                            // Check if collision occurred
                            if (bucket.size() > 1) {
                                System.out.println("Collision at bucket " + bucketIndex);
                            }

                            // Consider resizing if load factor too high
                            if (getLoadFactor() > 0.75) {
                                resize();
                            }
                        }

                        // Usage Example:
                        HashTable<String, Integer> ht = new HashTable<>();
                        ht.put("Alice", 25);
                        ht.put("Bob", 30);
                        ht.put("Alice", 26);  // Updates Alice's value
                        """));

        codeExamples.put("Get Operation", new CodeExample(
                "Get/Search Operation",
                "Retrieve value by key from hash table",
                "O(1) average, O(n) worst",
                """
                        // Get operation - retrieve value by key
                        public V get(K key) {
                            if (key == null) {
                                throw new IllegalArgumentException("Key cannot be null");
                            }

                            int bucketIndex = hash(key);
                            LinkedList<Entry<K, V>> bucket = buckets[bucketIndex];

                            // Search in bucket
                            for (Entry<K, V> entry : bucket) {
                                if (entry.key.equals(key)) {
                                    System.out.println("Found " + key + " at bucket " + bucketIndex);
                                    return entry.value;
                                }
                            }

                            // Key not found
                            return null;
                        }

                        // Check if key exists
                        public boolean containsKey(K key) {
                            return get(key) != null;
                        }

                        // Usage Example:
                        HashTable<String, Integer> ht = new HashTable<>();
                        ht.put("Alice", 25);

                        Integer age = ht.get("Alice");  // Returns 25
                        Integer notFound = ht.get("Charlie");  // Returns null

                        if (ht.containsKey("Alice")) {
                            System.out.println("Alice exists!");
                        }
                        """));

        codeExamples.put("Remove Operation", new CodeExample(
                "Remove/Delete Operation",
                "Remove key-value pair from hash table",
                "O(1) average, O(n) worst",
                """
                        // Remove operation
                        public V remove(K key) {
                            if (key == null) {
                                throw new IllegalArgumentException("Key cannot be null");
                            }

                            int bucketIndex = hash(key);
                            LinkedList<Entry<K, V>> bucket = buckets[bucketIndex];

                            // Search and remove
                            Iterator<Entry<K, V>> iterator = bucket.iterator();
                            while (iterator.hasNext()) {
                                Entry<K, V> entry = iterator.next();
                                if (entry.key.equals(key)) {
                                    V value = entry.value;
                                    iterator.remove();
                                    size--;
                                    System.out.println("Removed " + key + " from bucket " + bucketIndex);
                                    return value;
                                }
                            }

                            // Key not found
                            return null;
                        }

                        // Usage Example:
                        HashTable<String, Integer> ht = new HashTable<>();
                        ht.put("Alice", 25);
                        ht.put("Bob", 30);

                        Integer removed = ht.remove("Alice");  // Returns 25
                        System.out.println("Removed value: " + removed);

                        Integer notFound = ht.remove("Charlie");  // Returns null
                        """));

        // =============== HASH FUNCTIONS ===============
        codeExamples.put("Hash Functions", new CodeExample(
                "Common Hash Functions",
                "Different hash function implementations",
                "O(k) where k = key length",
                """
                        // Division Method (Most common)
                        public int divisionHash(int key, int tableSize) {
                            return Math.abs(key % tableSize);
                        }

                        // Multiplication Method
                        public int multiplicationHash(int key, int tableSize) {
                            double A = 0.6180339887;  // (√5 - 1) / 2
                            double product = key * A;
                            double fractional = product - Math.floor(product);
                            return (int) (tableSize * fractional);
                        }

                        // String Hash (djb2 algorithm)
                        public int stringHash(String key, int tableSize) {
                            long hash = 5381;
                            for (char c : key.toCharArray()) {
                                hash = ((hash << 5) + hash) + c;  // hash * 33 + c
                            }
                            return (int) Math.abs(hash % tableSize);
                        }

                        // Java's built-in hashCode usage
                        public int javaHash(Object key, int tableSize) {
                            return Math.abs(key.hashCode() % tableSize);
                        }

                        // Polynomial Rolling Hash (for strings)
                        public int polynomialHash(String key, int tableSize) {
                            int p = 31;  // Prime base
                            long hash = 0;
                            long pPow = 1;

                            for (char c : key.toCharArray()) {
                                hash = (hash + (c - 'a' + 1) * pPow) % tableSize;
                                pPow = (pPow * p) % tableSize;
                            }

                            return (int) Math.abs(hash);
                        }

                        // Usage Example:
                        String key = "Hello";
                        int index = stringHash(key, 16);
                        System.out.println("Hash index for '" + key + "': " + index);
                        """));

        // =============== COLLISION HANDLING ===============
        codeExamples.put("Separate Chaining", new CodeExample(
                "Separate Chaining",
                "Handle collisions using linked lists at each bucket",
                "O(1) avg, O(n) worst",
                """
                        // Separate Chaining Implementation
                        public class SeparateChainingHashTable<K, V> {
                            private LinkedList<Entry<K, V>>[] buckets;
                            private int capacity;
                            private int size;

                            @SuppressWarnings("unchecked")
                            public SeparateChainingHashTable(int capacity) {
                                this.capacity = capacity;
                                this.buckets = new LinkedList[capacity];
                                for (int i = 0; i < capacity; i++) {
                                    buckets[i] = new LinkedList<>();
                                }
                            }

                            public void put(K key, V value) {
                                int index = hash(key);
                                LinkedList<Entry<K, V>> chain = buckets[index];

                                // Update if exists
                                for (Entry<K, V> entry : chain) {
                                    if (entry.key.equals(key)) {
                                        entry.value = value;
                                        return;
                                    }
                                }

                                // Add to chain
                                chain.add(new Entry<>(key, value));
                                size++;

                                // Log collision if chain has more than 1 element
                                if (chain.size() > 1) {
                                    System.out.println("Chain at bucket " + index + ": " + chain.size() + " entries");
                                }
                            }

                            public V get(K key) {
                                int index = hash(key);
                                for (Entry<K, V> entry : buckets[index]) {
                                    if (entry.key.equals(key)) {
                                        return entry.value;
                                    }
                                }
                                return null;
                            }

                            private int hash(K key) {
                                return Math.abs(key.hashCode() % capacity);
                            }
                        }

                        // Visualization:
                        // Bucket 0: [Alice=25] -> [Dave=22] -> null
                        // Bucket 1: [Bob=30] -> null
                        // Bucket 2: empty
                        // Bucket 3: [Charlie=28] -> null
                        """));

        codeExamples.put("Linear Probing", new CodeExample(
                "Linear Probing",
                "Handle collisions by probing next available slot",
                "O(1) avg, O(n) worst",
                """
                        // Linear Probing Implementation
                        public class LinearProbingHashTable<K, V> {
                            private K[] keys;
                            private V[] values;
                            private int capacity;
                            private int size;

                            @SuppressWarnings("unchecked")
                            public LinearProbingHashTable(int capacity) {
                                this.capacity = capacity;
                                this.keys = (K[]) new Object[capacity];
                                this.values = (V[]) new Object[capacity];
                            }

                            private int hash(K key) {
                                return Math.abs(key.hashCode() % capacity);
                            }

                            public void put(K key, V value) {
                                if (size >= capacity * 0.75) {
                                    throw new RuntimeException("Table is too full");
                                }

                                int index = hash(key);
                                int probeCount = 0;

                                // Linear probing: check next slot if occupied
                                while (keys[index] != null) {
                                    if (keys[index].equals(key)) {
                                        values[index] = value;  // Update
                                        return;
                                    }
                                    index = (index + 1) % capacity;  // Move to next slot
                                    probeCount++;
                                }

                                keys[index] = key;
                                values[index] = value;
                                size++;

                                if (probeCount > 0) {
                                    System.out.println("Inserted after " + probeCount + " probes");
                                }
                            }

                            public V get(K key) {
                                int index = hash(key);
                                int probeCount = 0;

                                while (keys[index] != null) {
                                    if (keys[index].equals(key)) {
                                        return values[index];
                                    }
                                    index = (index + 1) % capacity;
                                    probeCount++;

                                    if (probeCount >= capacity) {
                                        break;  // Searched entire table
                                    }
                                }
                                return null;
                            }
                        }

                        // Visualization:
                        // Index: 0    1    2    3    4    5
                        // Key:   A    B    -    C    D    E
                        // If A and D hash to 0, D probes to slot 3 or 4
                        """));

        // =============== ADVANCED OPERATIONS ===============
        codeExamples.put("Resize Operation", new CodeExample(
                "Dynamic Resizing",
                "Resize hash table when load factor exceeds threshold",
                "O(n)",
                """
                        // Resize/Rehash operation
                        @SuppressWarnings("unchecked")
                        private void resize() {
                            int newCapacity = capacity * 2;
                            LinkedList<Entry<K, V>>[] oldBuckets = buckets;

                            // Create new buckets array
                            buckets = new LinkedList[newCapacity];
                            capacity = newCapacity;
                            size = 0;

                            for (int i = 0; i < capacity; i++) {
                                buckets[i] = new LinkedList<>();
                            }

                            // Rehash all entries
                            for (LinkedList<Entry<K, V>> bucket : oldBuckets) {
                                for (Entry<K, V> entry : bucket) {
                                    put(entry.key, entry.value);  // Rehash
                                }
                            }

                            System.out.println("Resized from " + (capacity/2) + " to " + capacity);
                        }

                        // Call resize when load factor > threshold
                        public void put(K key, V value) {
                            // ... insert logic ...

                            if (getLoadFactor() > 0.75) {
                                resize();  // Double the capacity
                            }
                        }

                        // Usage Example:
                        // Initial: capacity=4, size=3, loadFactor=0.75
                        // After put: capacity=8, all entries rehashed
                        """));

        codeExamples.put("Java HashMap", new CodeExample(
                "Java HashMap Usage",
                "Using Java's built-in HashMap class",
                "O(1) average",
                """
                        // Java HashMap - Production-Ready Hash Table
                        import java.util.HashMap;
                        import java.util.Map;

                        public class HashMapExample {
                            public static void main(String[] args) {
                                // Create HashMap
                                Map<String, Integer> ages = new HashMap<>();

                                // Put operations
                                ages.put("Alice", 25);
                                ages.put("Bob", 30);
                                ages.put("Charlie", 28);

                                // Get operations
                                int aliceAge = ages.get("Alice");  // 25
                                Integer unknown = ages.get("Dave"); // null

                                // Get with default value
                                int daveAge = ages.getOrDefault("Dave", 0);  // 0

                                // Check existence
                                if (ages.containsKey("Alice")) {
                                    System.out.println("Alice found!");
                                }

                                // Remove
                                ages.remove("Bob");

                                // Iterate entries
                                for (Map.Entry<String, Integer> entry : ages.entrySet()) {
                                    System.out.println(entry.getKey() + ": " + entry.getValue());
                                }

                                // Iterate keys
                                for (String name : ages.keySet()) {
                                    System.out.println(name);
                                }

                                // Iterate values
                                for (Integer age : ages.values()) {
                                    System.out.println(age);
                                }

                                // putIfAbsent - only put if key doesn't exist
                                ages.putIfAbsent("Alice", 99);  // Won't change Alice's value

                                // compute - update value with function
                                ages.compute("Alice", (k, v) -> v + 1);  // Alice is now 26

                                // Size and clear
                                System.out.println("Size: " + ages.size());
                                ages.clear();
                            }
                        }
                        """));

        codeExamples.put("Applications", new CodeExample(
                "Hash Table Applications",
                "Common use cases for hash tables",
                "Various",
                """
                        // 1. Two Sum Problem
                        public int[] twoSum(int[] nums, int target) {
                            Map<Integer, Integer> map = new HashMap<>();

                            for (int i = 0; i < nums.length; i++) {
                                int complement = target - nums[i];
                                if (map.containsKey(complement)) {
                                    return new int[] { map.get(complement), i };
                                }
                                map.put(nums[i], i);
                            }
                            return new int[0];
                        }

                        // 2. Frequency Counter
                        public Map<Character, Integer> countFrequency(String s) {
                            Map<Character, Integer> freq = new HashMap<>();
                            for (char c : s.toCharArray()) {
                                freq.put(c, freq.getOrDefault(c, 0) + 1);
                            }
                            return freq;
                        }

                        // 3. First Non-Repeating Character
                        public char firstUnique(String s) {
                            Map<Character, Integer> count = new LinkedHashMap<>();
                            for (char c : s.toCharArray()) {
                                count.put(c, count.getOrDefault(c, 0) + 1);
                            }
                            for (Map.Entry<Character, Integer> entry : count.entrySet()) {
                                if (entry.getValue() == 1) {
                                    return entry.getKey();
                                }
                            }
                            return '_';  // Not found
                        }

                        // 4. Group Anagrams
                        public List<List<String>> groupAnagrams(String[] strs) {
                            Map<String, List<String>> map = new HashMap<>();
                            for (String s : strs) {
                                char[] chars = s.toCharArray();
                                Arrays.sort(chars);
                                String key = new String(chars);

                                map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
                            }
                            return new ArrayList<>(map.values());
                        }

                        // 5. LRU Cache (using LinkedHashMap)
                        public class LRUCache<K, V> extends LinkedHashMap<K, V> {
                            private final int capacity;

                            public LRUCache(int capacity) {
                                super(capacity, 0.75f, true);  // accessOrder = true
                                this.capacity = capacity;
                            }

                            @Override
                            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                                return size() > capacity;
                            }
                        }
                        """));
    }

    public CodeExample getCodeExample(String name) {
        return codeExamples.get(name);
    }

    public String getWelcomeMessage() {
        return """
                ╔══════════════════════════════════════════════════════════════════╗
                ║                                                                    ║
                ║    #️⃣ Welcome to the Hash Table Code Repository!                   ║
                ║                                                                    ║
                ║    This comprehensive collection includes:                         ║
                ║                                                                    ║
                ║    🔑 Basic Operations                                             ║
                ║       • Create, Put, Get, Remove operations                        ║
                ║                                                                    ║
                ║    🔧 Hash Functions                                               ║
                ║       • Division, Multiplication, String hashing                   ║
                ║                                                                    ║
                ║    💥 Collision Handling                                           ║
                ║       • Separate Chaining, Linear Probing                          ║
                ║                                                                    ║
                ║    ⚡ Advanced Topics                                              ║
                ║       • Dynamic Resizing, Java HashMap, Applications               ║
                ║                                                                    ║
                ║    👈 Select a category from the left panel to explore             ║
                ║       detailed code examples with explanations.                    ║
                ║                                                                    ║
                ║    💡 Tip: Hash tables provide O(1) average-case operations!       ║
                ║                                                                    ║
                ╚══════════════════════════════════════════════════════════════════╝
                """;
    }

    // Inner class for code examples
    public static class CodeExample {
        private final String title;
        private final String description;
        private final String complexity;
        private final String code;

        public CodeExample(String title, String description, String complexity, String code) {
            this.title = title;
            this.description = description;
            this.complexity = complexity;
            this.code = code;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getComplexity() {
            return complexity;
        }

        public String getCode() {
            return code;
        }
    }
}
