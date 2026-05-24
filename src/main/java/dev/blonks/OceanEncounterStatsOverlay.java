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
        panel.getChildren().add(TitleComponent.builder()
                .text("Stats since")
                .color(Color.YELLOW)
                .build());
        panel.getChildren().add(TitleComponent.builder()
                .text("last encounter")
                .color(Color.YELLOW)
                .build());
        panel.getChildren().add(TitleComponent.builder()
                .text("Ticks: " + plugin.getTicksSinceLast())
                .color(Color.WHITE)
                .build());
        panel.getChildren().add(TitleComponent.builder()
                .text("Borders: " + plugin.getRegionBordersSinceLast())
                .color(Color.WHITE)
                .build());
        panel.getChildren().add(TitleComponent.builder()
                .text("Tiles: " + plugin.getTilesSinceLast())
                .color(Color.WHITE)
                .build());
        BigDecimal tiles = BigDecimal.valueOf(plugin.getTilesSinceLast());
        BigDecimal ticks = BigDecimal.valueOf(plugin.getTicksSinceLast());
        BigDecimal speed = tiles.divide(ticks, 1, RoundingMode.HALF_UP);
        panel.getChildren().add(TitleComponent.builder()
                .text("Speed: " + speed)
                .color(Color.WHITE)
                .build());
        panel.getChildren().add(TitleComponent.builder()
                .text("")
                .color(Color.WHITE)
                .build());

        // Total average stats
        Double avgTicks = plugin.getStats().stream().collect(Collectors.averagingDouble(EncounterStats::getTicksSinceLast));
        Double avgRegions = plugin.getStats().stream().collect(Collectors.averagingDouble(EncounterStats::getRegionBordersSinceLast));
        Double avgTiles = plugin.getStats().stream().collect(Collectors.averagingDouble(EncounterStats::getTilesSinceLast));
        BigDecimal avgTilesBd = BigDecimal.valueOf(avgTiles);
        BigDecimal avgTicksBd = BigDecimal.valueOf(avgTicks);
        if (avgTicksBd.compareTo(BigDecimal.ZERO) == 0) {
            avgTicksBd = BigDecimal.ONE;
        }
        BigDecimal avgSpeed = avgTilesBd.divide(avgTicksBd, 1, RoundingMode.HALF_UP);
        panel.getChildren().add(TitleComponent.builder()
                .text("Average stats")
                .color(Color.YELLOW)
                .build());
        panel.getChildren().add(TitleComponent.builder()
                .text("per encounter")
                .color(Color.YELLOW)
                .build());
        panel.getChildren().add(TitleComponent.builder()
                .text("Ticks: " + avgTicks)
                .color(Color.WHITE)
                .build());
        panel.getChildren().add(TitleComponent.builder()
                .text("Borders: " + avgRegions)
                .color(Color.WHITE)
                .build());
        panel.getChildren().add(TitleComponent.builder()
                .text("Tiles: " + avgTiles)
                .color(Color.WHITE)
                .build());
        panel.getChildren().add(TitleComponent.builder()
                .text("Speed: " + avgSpeed)
                .color(Color.WHITE)
                .build());
        panel.getChildren().add(TitleComponent.builder()
                .text("")
                .color(Color.WHITE)
                .build());

        panel.getChildren().add(TitleComponent.builder()
                .text("Encounters seen")
                .color(Color.YELLOW)
                .build());

        Map<Encounter, List<EncounterStats>> statMap = plugin.getStats().stream().collect(Collectors.groupingBy(EncounterStats::getEncounter));

        for (var type : Encounter.values()) {
            List<EncounterStats> statsList = statMap.get(type);
            int count = 0;
            if (statsList != null) {
                count = statsList.size();
            }
            panel.getChildren().add(TitleComponent.builder()
                    .text(type.getName() + ": " + count)
                    .color(Color.WHITE)
                    .build());
        }

        return panel.render(graphics);
    }

    private String camelCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
