package dev.blonks.osrs.oceanencounters.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EncounterCounts {
    private Map<Integer, Integer> encounterCounts = new ConcurrentHashMap<>();

    public int getCount() {
        int sum = 0;
        for (int count : encounterCounts.values()) {
            sum += count;
        }
        return sum;
    }

    public int getCount(Encounter encounter) {
        int sum = 0;
        for (int npcIds : encounter.getNpcIds()) {
            sum += encounterCounts.get(npcIds);
        }
        return sum;
    }

    public int getCount(int npcId) {
        return encounterCounts.get(npcId);
    }

    public void incrementCount(int npcId) {
        int current = encounterCounts.getOrDefault(npcId, 0);
        current += 1;
        encounterCounts.put(npcId, current);
    }

    public void setCount(int npcId, int count) {
        encounterCounts.put(npcId, count);
    }
}
