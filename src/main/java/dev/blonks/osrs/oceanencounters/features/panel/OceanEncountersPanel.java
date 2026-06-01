package dev.blonks.osrs.oceanencounters.features.panel;

import dev.blonks.osrs.oceanencounters.features.encounters.EncounterStatsManager;
import dev.blonks.osrs.oceanencounters.features.util.Encounter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.ProgressBar;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
public class OceanEncountersPanel extends PluginPanel {
    private final EncounterStatsManager encounterStatsManager;

    private static final String DOWN_ARROW = "▼";
    private static final String RIGHT_ARROW = "▶";

    private JPanel encounterPanel;


    public OceanEncountersPanel(EncounterStatsManager encounterStatsManager) {
        this.encounterStatsManager = encounterStatsManager;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel title = new JLabel("Encounter Stats");
        title.setForeground(Color.WHITE);
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(new EmptyBorder(1, 3, 10, 0));
        titlePanel.add(title, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        encounterPanel = buildEncounterPanel();
        add(encounterPanel, BorderLayout.CENTER);
    }

    private JPanel buildEncounterPanel() {
        encounterPanel = new JPanel();
        encounterPanel.setBorder(new EmptyBorder(1, 1, 1, 1));
        encounterPanel.setLayout(new BoxLayout(encounterPanel, BoxLayout.Y_AXIS));

        int totalCount = encounterStatsManager.getEncounterCounts().getCount();
        log.info("Total Encounter Count: {}", totalCount);
        if (totalCount == 0) {
            totalCount = 1;
        }
        for (Encounter encounter : Encounter.values()) {
            JPanel newEncounterPanel = new EncounterCard(encounterStatsManager, encounter);
            encounterPanel.add(newEncounterPanel, BorderLayout.CENTER);
        }
        return encounterPanel;
    }

    public void update() {
        remove(encounterPanel);
        encounterPanel = buildEncounterPanel();
        add(encounterPanel, BorderLayout.CENTER);
    }
}
