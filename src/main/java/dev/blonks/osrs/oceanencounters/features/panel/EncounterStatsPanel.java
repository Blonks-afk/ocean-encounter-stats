package dev.blonks.osrs.oceanencounters.features.panel;

import dev.blonks.osrs.oceanencounters.features.encounters.EncounterStatsManager;
import dev.blonks.osrs.oceanencounters.model.Encounter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Slf4j
public class EncounterStatsPanel extends PluginPanel {
    private final EncounterStatsManager encounterStatsManager;

    private static final String DOWN_ARROW = "▼";
    private static final String RIGHT_ARROW = "▶";

    private JPanel encounterPanel;


    public EncounterStatsPanel(EncounterStatsManager encounterStatsManager) {
        this.encounterStatsManager = encounterStatsManager;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(6, 6, 6, 6));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        // Create and add main title
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
        // Create and add individual encounter cards
        encounterPanel = new JPanel();
        encounterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        encounterPanel.setLayout(new BoxLayout(encounterPanel, BoxLayout.Y_AXIS));
        encounterPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
//        encounterPanel.setLayout(new BorderLayout());

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
