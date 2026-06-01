package dev.blonks.osrs.oceanencounters.module;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import dev.blonks.osrs.oceanencounters.OceanEncounterConfig;
import dev.blonks.osrs.oceanencounters.features.encounters.EncounterEventManager;
import dev.blonks.osrs.oceanencounters.features.encounters.EncounterStatsManager;
import dev.blonks.osrs.oceanencounters.features.nextroll.NextRollInfobox;
import dev.blonks.osrs.oceanencounters.features.nextroll.NextRollOverlay;
import dev.blonks.osrs.oceanencounters.features.nextroll.NextRollTracker;
import dev.blonks.osrs.oceanencounters.features.panel.PanelManager;
import dev.blonks.osrs.oceanencounters.features.util.LocationService;
import dev.blonks.osrs.oceanencounters.features.writer.EventFileWriter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

import java.util.Set;

@Slf4j
public class OceanEncounterModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ComponentManager.class);
    }

    @Provides
    Set<PluginLifecycleComponent> lifecycleComponents(
        EncounterEventManager encounterEventManager,
        NextRollTracker nextRollTracker,
        NextRollOverlay nextRollOverlay,
        NextRollInfobox nextRollInfobox,
        PanelManager panelManager,
        LocationService locationService,
        EventFileWriter eventFileWriter,
        EncounterStatsManager encounterStatsManager
    ) {
        var builder = ImmutableSet.<PluginLifecycleComponent>builder()
                .add(encounterEventManager)
                .add(nextRollTracker)
                .add(nextRollOverlay)
                .add(nextRollInfobox)
                .add(locationService)
                .add(eventFileWriter)
                .add(encounterStatsManager)
                .add(panelManager);
        return builder.build();
    }

    @Provides
    @Singleton
    OceanEncounterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(OceanEncounterConfig.class);
    }
}
