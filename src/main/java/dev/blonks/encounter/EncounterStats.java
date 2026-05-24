package dev.blonks.encounter;

import lombok.Data;

@Data
public class EncounterStats {
    private final Encounter encounter;
    private final int ticksSinceLast;
    private final int regionBordersSinceLast;
    private final int tilesSinceLast;
}
