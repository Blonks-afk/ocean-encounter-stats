package dev.blonks.osrs.oceanencounters.features.panel;

import dev.blonks.osrs.oceanencounters.features.encounters.EncounterStatsManager;
import dev.blonks.osrs.oceanencounters.features.encounters.events.EncounterSpawned;
import dev.blonks.osrs.oceanencounters.model.EncounterCounts;
import dev.blonks.osrs.oceanencounters.module.PluginLifecycleComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PanelManager implements PluginLifecycleComponent {
    private final ClientToolbar clientToolbar;
    private NavigationButton navButton;
    private OceanEncountersPanel oceanEncountersPanel;
    private final EncounterCounts encounterCounts;
    private final EncounterStatsManager encounterStatsManager;

    @Override
    public void startup() {
        log.info("Starting PanelManager");
        oceanEncountersPanel = new OceanEncountersPanel(encounterStatsManager);
        navButton = NavigationButton.builder()
                .priority(30)
                .tooltip("Ocean Encounters")
                .panel(oceanEncountersPanel)
                .icon(ImageUtil.loadImageResource(getClass(), "/Giant_clam_(pearl).png"))
                .build();
        clientToolbar.addNavigation(navButton);
    }

    @Subscribe
    public void onEncounterSpawned(EncounterSpawned encounterSpawned) {
        if (encounterSpawned.isDistinct()) {
            oceanEncountersPanel.update();
        }
    }

    @Override
    public void shutdown() {
        clientToolbar.removeNavigation(navButton);
        navButton = null;
    }
}
