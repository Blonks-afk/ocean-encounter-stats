package dev.blonks.encounter;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentStats {
    public int ticksSinceLast = 0;
    private int regionBordersSinceLast = 0;
    private int tilesSinceLast = 0;
    private int stationaryTicksSinceLast = 0;
    private int ticksSteering = 0;
    private int ticksCrewSteering = 0;
    private int ticksFreeSteering = 0;
    private int ticksOnMainland = 0;

    public EncounterStats createEncounterStats(Encounter encounter) {
        EncounterStats stats = new EncounterStats(
                encounter,
                ticksSinceLast,
                regionBordersSinceLast,
                tilesSinceLast,
                stationaryTicksSinceLast,
                ticksSteering,
                ticksCrewSteering,
                ticksFreeSteering,
                ticksOnMainland
        );

        resetState();
        return stats;
    }

    private void resetState() {
        ticksSinceLast = 0;
        regionBordersSinceLast = 0;
        tilesSinceLast = 0;
        stationaryTicksSinceLast = 0;
        ticksSteering = 0;
        ticksCrewSteering = 0;
        ticksFreeSteering = 0;
        ticksOnMainland = 0;
    }
}
