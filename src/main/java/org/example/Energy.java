package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.*;

public class Energy {

    private int id;
    private String end;
    private String start;
    private List<Volume> volumes;
    private Map<LocalDate, Double> dailyAvg = new HashMap<>();

    public int getId() {
        return id;
    }

    public String getEnd() {
        return end;
    }

    public String getStart() {
        return start;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public List<Volume> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<Volume> volumes) {
        this.volumes = volumes;
    }

    public void setDailyAvg(Map<LocalDate, Double> dailyAvg) {this.dailyAvg=dailyAvg;}

    public Map<LocalDate, Double> getDailyAvg() {
        return dailyAvg;
    }

    public static class Volume {
        @JsonProperty("Key")
        private String key;
        @JsonProperty("Value")
        private double value;

        public String getKey() {
            return key;
        }

        public double getValue() {
            return value;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }


}
