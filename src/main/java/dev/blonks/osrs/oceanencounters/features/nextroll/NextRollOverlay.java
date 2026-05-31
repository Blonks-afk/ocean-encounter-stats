package dev.blonks.osrs.oceanencounters.features.nextroll;

import dev.blonks.osrs.oceanencounters.OceanEncounterConfig;
import dev.blonks.osrs.oceanencounters.module.PluginLifecycleComponent;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Slf4j
@Singleton
public class NextRollOverlay extends OverlayPanel implements PluginLifecycleComponent {

    private final Client client;
    private final NextRollTracker tracker;

    @Inject
    public NextRollOverlay(Client client, NextRollTracker tracker) {
        this.client = client;
        this.tracker = tracker;

        setPreferredPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public boolean isEnabled(OceanEncounterConfig config) {
        if (!config.displayRollTimer()) {
            return false;
        }
        return OceanEncounterConfig.NextRollDisplayType.OVERLAY == config.displayType();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (client.getLocalPlayer().getWorldView().isTopLevel()) {
            return null;
        }
        String rollString = String.format("%d (%d)", tracker.getTicksRemaining(), tracker.getTicksMoving());

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Next Roll in")
                .color(Color.YELLOW)
                .build());
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(rollString)
                .build());

        return super.render(graphics);
    }
}
