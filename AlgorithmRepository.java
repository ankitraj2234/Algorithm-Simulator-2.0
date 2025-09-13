// src/main/java/com/simulator/analysis/AlgorithmRepository.java
package com.simulator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class AlgorithmRepository {
    private static final String DATA_PATH = "/data/algorithms.json";
    private static AlgorithmRepository INSTANCE;
    private final Map<String, List<AlgorithmRecord>> byCategory;

    private AlgorithmRepository(Map<String, List<AlgorithmRecord>> byCategory) {
        this.byCategory = byCategory;
    }

    public static synchronized AlgorithmRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    private static AlgorithmRepository load() {
        try (InputStream in = AlgorithmRepository.class.getResourceAsStream(DATA_PATH)) {
            if (in == null) throw new IllegalStateException("Missing data file: " + DATA_PATH);
            InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            Type type = new TypeToken<Map<String, List<AlgorithmRecord>>>() {}.getType();
            Map<String, List<AlgorithmRecord>> map = new Gson().fromJson(reader, type);
            // Normalize null lists to empty
            map.replaceAll((k, v) -> v == null ? new ArrayList<>() : v);
            return new AlgorithmRepository(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load algorithms.json", e);
        }
    }

    public List<String> getCategories() {
        return new ArrayList<>(byCategory.keySet()).stream().sorted().collect(Collectors.toList());
    }

    public List<AlgorithmRecord> getAlgorithms(String category) {
        return new ArrayList<>(byCategory.getOrDefault(category, Collections.emptyList()));
    }

    public Optional<AlgorithmRecord> find(String category, String name) {
        return getAlgorithms(category).stream().filter(a -> a.getName().equals(name)).findFirst();
    }
}
