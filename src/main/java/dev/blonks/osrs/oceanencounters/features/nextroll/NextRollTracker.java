package dev.blonks.osrs.oceanencounters.features.nextroll;

import dev.blonks.osrs.oceanencounters.OceanEncounterConfig;
import dev.blonks.osrs.oceanencounters.features.encounters.events.EncounterSpawned;
import dev.blonks.osrs.oceanencounters.features.encounters.events.InkStoutChatMessage;
import dev.blonks.osrs.oceanencounters.features.util.LocationService;
import dev.blonks.osrs.oceanencounters.module.PluginLifecycleComponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class NextRollTracker implements PluginLifecycleComponent {
    // Set debounce to 110, just before the next possible spawn at 115/120
    private static final int DEBOUNCE_TICKS = 110;

    private final Client client;
    private final LocationService locationService;

    private boolean queueEncounter = false;

    @Getter
    private int ticksRemaining = 0;
    @Getter
    private int ticksMoving = 0;

    @Override
    public void startup() {
        resetState();
    }

    @Override
    public void shutdown() {
        resetState();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        List<GameState> validStates = List.of(
                GameState.LOGGED_IN,
                GameState.LOADING
        );
        if (!validStates.contains(event.getGameState())) {
            resetState();
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        // TODO: Implement reset logic (i.e. docking

        // Check for and reset on certain conditions
        int speed = locationService.getSpeed();
        if (speed != 0.0) {
            ticksRemaining--;
            ticksMoving++;
        }

        // only after incrementing counters, process any queued encounters
        if (queueEncounter) {
            queueEncounter = false;
            client.addChatMessage(
                    ChatMessageType.NPC_SAY,
                    "Ocean Encounters",
                    String.format("Spawned after moving for %d ticks!", ticksMoving),
                    "Ocean Encounters"
            );
            resetState();
        }

        // After all processing of events is complete, update any counters if they are at their upper/lower bounds
        if (ticksRemaining <= 0) {
            ticksRemaining = 115;
        }
    }

    /**
     * Queue processing when an encounter NPC is spawned. If the player has a
     * Kraken Ink Stout keg on their boat, this will end up just hitting the debounce
     * and doing nothing. It should naturally trend towards being more accurate
     * as more encounter spawns are seen.
     * @param e
     */
    @Subscribe
    private void onEncounterSpawned(EncounterSpawned e) {
        if (ticksMoving > DEBOUNCE_TICKS) {
            queueEncounter = true;
        }
    }

    /**
     * Queue encounter spawn processing when the ink stout chat message procs.
     * Since this is directly from the server, we can disregard any local assumptions
     * about duration so no need for debounce
     * @param event
     */
    @Subscribe
    private void onInkStoutChatMessage(InkStoutChatMessage event) {
        queueEncounter = true;
    }

    /**
     * TODO: Implement 115 vs 120 logic for kraken ink stout
     */
    private void resetState() {
        if (krakenInkPresent()) {
            ticksRemaining = 115;
        } else {
            ticksRemaining = 120;
        }
        ticksMoving = 0;
        queueEncounter = false;
    }

    private boolean krakenInkPresent() {
        // TODO: Implement actual kraken ink stout keg check
        return true;
    }
}
