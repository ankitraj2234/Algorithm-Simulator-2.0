// src/main/java/com/simulator/analysis/AlgorithmRecord.java
package com.simulator;

public class AlgorithmRecord {
    private String name;
    private String best;
    private String average;
    private String worst;
    private String space;
    private Boolean stable;   // optional
    private Boolean inPlace;  // optional
    private String notes;     // optional

    public String getName() { return name; }
    public String getBest() { return best; }
    public String getAverage() { return average; }
    public String getWorst() { return worst; }
    public String getSpace() { return space; }
    public Boolean getStable() { return stable != null && stable; }
    public Boolean getInPlace() { return inPlace != null && inPlace; }
    public String getNotes() { return notes == null ? "" : notes; }
}
