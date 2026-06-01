package dev.blonks.osrs.oceanencounters.features.encounters.events;

import dev.blonks.osrs.oceanencounters.features.util.Encounter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

/**
 * An event for when an NPC corresponding to a sailing Encounter has spawned
 */
@Getter
@AllArgsConstructor
public class EncounterSpawned {
    private final Encounter encounter;
    private final int npcId;
    private final WorldPoint spawnPoint;
    private final WorldPoint boatLocation;
    private final int boatOrientation;
    private final int ticksMoving;
    private final boolean distinct;
}
