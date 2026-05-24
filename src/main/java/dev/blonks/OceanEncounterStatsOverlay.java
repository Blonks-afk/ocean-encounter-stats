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

        renderStat("Ticks: ", plugin.getCurrentStats().getTicksSinceLast());
        renderStat("Ticks Steering: ", plugin.getCurrentStats().getTicksSteering());
        renderStat("Ticks Stationary: ", plugin.getCurrentStats().getStationaryTicksSinceLast());
        renderStat("Ticks on Mainland: ", plugin.getCurrentStats().getTicksOnMainland());
        renderStat("Ticks Crew Steering: ", plugin.getCurrentStats().getTicksCrewSteering());
        renderStat("Ticks Free Steering: ", plugin.getCurrentStats().getTicksFreeSteering());
        renderStat("Region Borders: ", plugin.getCurrentStats().getRegionBordersSinceLast());
        renderStat("Tiles: ", plugin.getCurrentStats().getTilesSinceLast());
        BigDecimal tiles = BigDecimal.valueOf(plugin.getCurrentStats().getTilesSinceLast());
        BigDecimal ticks = new BigDecimal(plugin.getCurrentStats().getTicksSinceLast());
        if (ticks.equals(BigDecimal.ZERO)) {
            ticks = BigDecimal.ZERO;
        }
        BigDecimal speed = tiles.divide(ticks, 1, RoundingMode.HALF_UP);
        renderStat("Speed: ", speed);
        renderStat("", "");

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
        renderStat("Average stats", "", Color.YELLOW);
        renderStat("per encounter", "", Color.YELLOW);
        renderStat("Ticks: ", roundDouble(avgTicks));
        renderStat("Borders: ", roundDouble(avgRegions));
        renderStat("Tiles: ", roundDouble(avgTiles));
        renderStat("Speed: ", avgSpeed);
        renderStat("", "");
        renderStat("Encounters seen", "", Color.YELLOW);

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

    private <I> void renderStat(String title, I value) {
        renderStat(title, value, Color.WHITE);
    }

    private <I> void renderStat(String title, I value, Color color) {
        panel.getChildren().add(TitleComponent.builder()
                .text(title + value)
                .color(color)
                .build());
    }

    private BigDecimal roundDouble(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd;
    }
}
