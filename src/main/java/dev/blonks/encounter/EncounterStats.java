package dev.blonks.encounter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EncounterStats {
    private final Encounter encounter;
    private int ticksMoving;
}
