package dev.blonks.encounter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EncounterStats {
    private final int version = 1;
    private final Encounter encounter;
    private int ticksSinceLast = 0;
    private int regionBordersSinceLast = 0;
    private int tilesSinceLast = 0;
    private int stationaryTicksSinceLast = 0;
    private int ticksSteering = 0;
    private int ticksCrewSteering = 0;
    private int ticksFreeSteering = 0;
    private int ticksOnMainland = 0;
    // sail trims
    // sail xp drops
    // player location
    // player heading
    // encounter spawn location
    // encounter heading
    // kraken ink barrel on ship
}
