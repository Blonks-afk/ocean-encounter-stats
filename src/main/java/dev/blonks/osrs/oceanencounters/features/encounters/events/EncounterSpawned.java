package dev.blonks.osrs.oceanencounters.features.encounters.events;

import dev.blonks.osrs.oceanencounters.features.util.Encounter;
import lombok.AllArgsConstructor;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
public class EncounterSpawned {
    private final Encounter encounter;
    private final WorldPoint spawnPoint;
    private final WorldPoint boatLocation;
    private final int boatOrientation;
}
