package dev.blonks.osrs.oceanencounters.features.encounters;

import dev.blonks.osrs.oceanencounters.features.encounters.events.EncounterSpawned;
import dev.blonks.osrs.oceanencounters.features.encounters.events.InkStoutChatMessage;
import dev.blonks.osrs.oceanencounters.features.nextroll.NextRollTracker;
import dev.blonks.osrs.oceanencounters.model.Encounter;
import dev.blonks.osrs.oceanencounters.features.util.LocationService;
import dev.blonks.osrs.oceanencounters.module.PluginLifecycleComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EncounterEventManager implements PluginLifecycleComponent {
    private static final int DEBOUNCE = 105;

    private final Client client;
    private final EventBus eventBus;
    private final LocationService locationService;
    private final NextRollTracker nextRollTracker;

    private int lastEncounterTicks = 0;
    private boolean firstSpawn = true;

    @Override
    public void startup() {
    }

    @Override
    public void shutdown() {
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        // reset firstSpawn flag
        firstSpawn = true;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        Encounter encounterType = Encounter.fromNpcId(npcSpawned.getNpc().getId());

        if (encounterType == null) {
            return;
        }

        if (lastEncounterTicks == 0) {
            lastEncounterTicks = nextRollTracker.getTicksMoving();
        }
        // An encounter is distinct if it occurred AFTER our debounce travel tick count AND is the first
        // (wind encounters sometimes spawn multiple on the same tick)
        boolean distinct = lastEncounterTicks > DEBOUNCE && firstSpawn;
        EncounterSpawned queuedEncounterSpawned = new EncounterSpawned(
                encounterType,
                npcSpawned.getNpc().getId(),
                npcSpawned.getNpc().getWorldLocation(),
                locationService.getWorldTargetLocation(),
                locationService.getTargetOrientation(),
                lastEncounterTicks,
                distinct
        );
        firstSpawn = false;

        eventBus.post(queuedEncounterSpawned);
        // Only reset if it's a distinct encounter
        if (distinct) {
            lastEncounterTicks = 0;
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        // TODO: Probably verify this is a system message rather than a player message
        if (chatMessage.getMessage().endsWith("The stout has increased the chance of something happening...")) {
            eventBus.post(new InkStoutChatMessage());
            lastEncounterTicks = nextRollTracker.getTicksMoving();
        }
    }

}
