package dev.blonks;

import dev.blonks.encounter.Encounter;
import dev.blonks.encounter.EncounterStats;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OceanEncounterStatsOverlay extends Overlay {
    private OceanEncounterStatsPlugin plugin;
    private final PanelComponent panel = new PanelComponent();

    @Inject
    public OceanEncounterStatsOverlay(OceanEncounterStatsPlugin plugin) {
        this.plugin = plugin;

        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panel.getChildren().clear();

        // Stats since last
        renderStat("Stats since", "", Color.YELLOW);
        renderStat("last encounter", "", Color.YELLOW);

        renderStat("Ticks: ", plugin.getTicksMoving());
        renderStat("Speed: ", plugin.getSpeed());
        renderStat("Next Roll: ", plugin.getNextEncounterRoll());
        renderStat("", "");

        // Total average stats
        renderStat("Encounters seen", "", Color.YELLOW);

        for (var type : Encounter.values()) {
            int count = plugin.getEncounters().get(type);
            panel.getChildren().add(TitleComponent.builder()
                    .text(type.getName() + ": " + count)
                    .color(Color.WHITE)
                    .build());
        }

        return panel.render(graphics);
    }

    private <I> void renderStat(String title, I value) {
        renderStat(title, value, Color.WHITE);
    }

    private <I> void renderStat(String title, I value, Color color) {
        panel.getChildren().add(TitleComponent.builder()
                .text(title + value)
                .color(color)
                .build());
    }
}
