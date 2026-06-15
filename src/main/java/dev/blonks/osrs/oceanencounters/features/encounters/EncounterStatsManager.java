package dev.blonks.osrs.oceanencounters.features.encounters;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import dev.blonks.osrs.oceanencounters.OceanEncounterConfig;
import dev.blonks.osrs.oceanencounters.features.encounters.events.EncounterSpawned;
import dev.blonks.osrs.oceanencounters.model.Encounter;
import dev.blonks.osrs.oceanencounters.model.EncounterCounts;
import dev.blonks.osrs.oceanencounters.module.PluginLifecycleComponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.http.api.RuneLiteAPI;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EncounterStatsManager implements PluginLifecycleComponent {
    private final ConfigManager configManager;

    @Getter
    private EncounterCounts encounterCounts;

    @Override
    public boolean isEnabled(OceanEncounterConfig config) {
        return config.encounterQuantity();
    }

    @Override
    public void startup() {
        String json = configManager.getConfiguration(OceanEncounterConfig.CONFIG_GROUP, "encountersSeenData");

        log.info("Encounter Quantity: {}", json);
        if (Strings.isNullOrEmpty(json)) {
            encounterCounts = new EncounterCounts();
            for(Encounter encounter : Encounter.values()) {
                for (int id : encounter.getNpcIds()) {
                    encounterCounts.setCount(id, 0);
                }
            }
        } else {
            encounterCounts = new Gson().fromJson(json, EncounterCounts.class);
        }
        log.info("Encounter Counts (post-default): {}", encounterCounts);
    }

    @Subscribe
    public void onEncounterSpawned(EncounterSpawned encounterSpawned) {
        if (encounterSpawned.isDistinct()) {
            encounterCounts.incrementCount(encounterSpawned.getNpcId());
            String json = RuneLiteAPI.GSON.toJson(encounterCounts);
            configManager.setConfiguration(OceanEncounterConfig.CONFIG_GROUP, "encountersSeenData", json);
        }
    }
}
