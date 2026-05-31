package dev.blonks.osrs.oceanencounters.features.encounters;

import dev.blonks.osrs.oceanencounters.features.encounters.events.EncounterSpawned;
import dev.blonks.osrs.oceanencounters.features.encounters.events.InkStoutChatMessage;
import dev.blonks.osrs.oceanencounters.features.util.Encounter;
import dev.blonks.osrs.oceanencounters.features.util.LocationService;
import dev.blonks.osrs.oceanencounters.module.PluginLifecycleComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EncounterEventManager implements PluginLifecycleComponent {

    private final EventBus eventBus;
    private final LocationService locationService;


    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        Encounter encounterType = Encounter.fromNpcId(npcSpawned.getNpc().getId());

        if (encounterType == null) {
            return;
        }

        EncounterSpawned queuedEncounterSpawned = new EncounterSpawned(
                encounterType,
                npcSpawned.getNpc().getWorldLocation(),
                locationService.getWorldTargetLocation(),
                locationService.getTargetOrientation()
        );
        eventBus.post(queuedEncounterSpawned);
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        // TODO: Probably verify this is a system message rather than a player message
        if (chatMessage.getMessage().endsWith("The stout has increased the chance of something happening...")) {
            eventBus.post(new InkStoutChatMessage());
        }
    }

}
