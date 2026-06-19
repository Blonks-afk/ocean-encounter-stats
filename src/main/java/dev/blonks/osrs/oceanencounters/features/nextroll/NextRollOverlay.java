package dev.blonks.osrs.oceanencounters.features.nextroll;

import dev.blonks.osrs.oceanencounters.OceanEncounterConfig;
import dev.blonks.osrs.oceanencounters.module.PluginLifecycleComponent;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import net.runelite.client.util.RSTimeUnit;

@Slf4j
@Singleton
public class NextRollOverlay extends OverlayPanel implements PluginLifecycleComponent {

    private final Client client;
    private final NextRollTracker tracker;
	private final OceanEncounterConfig config;

    @Inject
    public NextRollOverlay(Client client, NextRollTracker tracker,  OceanEncounterConfig config) {
        this.client = client;
        this.tracker = tracker;
		this.config = config;

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
			int cutoff = config.nextRollOverlayHide();
			int tickCutoff = cutoff * 100;
			if (tracker.getTicksDisembarked() > tickCutoff || !tracker.isBeenOnBoat()) {
				return null;
			}
        }
        String rollString = String.format("%d (%d, vb:%d)", tracker.getTicksRemaining(), tracker.getTicksMoving(), tracker.getVarbitTicksMoving());

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Next Roll in")
                .color(Color.YELLOW)
                .build());
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(rollString)
                .build());

		if (!tracker.getDisembarkPenalties().isEmpty()) {
			panelComponent.getChildren().add(TitleComponent.builder()
				.text("Disembark Penalties")
				.color(Color.RED)
				.build());
			for (int i = 0; i < tracker.getDisembarkPenalties().size(); i++) {
				int disembark = tracker.getDisembarkPenalties().get(i);

				String disembarkString = String.format("%d", disembark);
				panelComponent.getChildren().add(TitleComponent.builder()
					.text(disembarkString)
					.build());
			}
		}

        return super.render(graphics);
    }
}
