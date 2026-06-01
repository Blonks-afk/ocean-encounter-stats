package dev.blonks.osrs.oceanencounters.features.panel;

import dev.blonks.osrs.oceanencounters.features.encounters.EncounterStatsManager;
import dev.blonks.osrs.oceanencounters.features.util.Encounter;
import net.runelite.client.ui.components.ProgressBar;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EncounterCard extends JPanel {
    private static final String DOWN_ARROW = "▼";
    private static final String RIGHT_ARROW = "▶";

    private final EncounterStatsManager encounterStatsManager;
    private final Encounter encounter;
    private final JButton expandButton;
    private final JPanel southPanel;
    private boolean expanded = false;

    public EncounterCard(EncounterStatsManager encounterStatsManager, Encounter encounter) {
        this.encounterStatsManager = encounterStatsManager;
        this.encounter = encounter;
        int totalCount = encounterStatsManager.getEncounterCounts().getCount();
        if (totalCount == 0) {
            totalCount = 1;
        }
        int count = encounterStatsManager.getEncounterCounts().getCount(encounter);

        setLayout(new BorderLayout());

        // Create and add name to north
        JLabel encounterNameLabel = new JLabel(encounter.getName());
        encounterNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        encounterNameLabel.setVerticalAlignment(SwingConstants.CENTER);
        encounterNameLabel.setForeground(Color.WHITE);
        add(encounterNameLabel, BorderLayout.NORTH);

        // create and add icons to west
        JLabel encounterIcon = new JLabel();
        BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/Giant_clam_(pearl).png");
        Image img = icon.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        encounterIcon.setIcon(new ImageIcon(img));
        encounterIcon.setSize(25, 25);
        add(encounterIcon, BorderLayout.WEST);

        // create and add progress bar to center
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
        ProgressBar progressBar = new ProgressBar();
        progressBar.setForeground(Color.GREEN);
        progressBar.setBackground(Color.GRAY);
        progressBar.setValue(count);
        progressBar.setCenterLabel(String.valueOf(count));
        progressBar.setMaximumValue(totalCount);
        progressBar.setRightLabel(String.valueOf(totalCount));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        centerPanel.add(progressBar, c);
        add(centerPanel, BorderLayout.CENTER);

        // create and add fixed-size optional dropdown to east
        Dimension dropdownDimension = new Dimension(40, 5);
        JPanel eastPanel = new JPanel(new GridBagLayout());
        eastPanel.setPreferredSize(dropdownDimension);
        eastPanel.setMinimumSize(new Dimension(dropdownDimension.width, 0));
        eastPanel.setMaximumSize(new Dimension(dropdownDimension.width, Integer.MAX_VALUE));

        expandButton = new JButton(RIGHT_ARROW);
        SwingUtil.removeButtonDecorations(expandButton);
        if (encounter.getNpcIds().size() > 1) {
            eastPanel.add(expandButton);
            expandButton.addActionListener(e -> {
                toggleCard();
            });
        }

        add(eastPanel, BorderLayout.EAST);


        southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        southPanel.setVisible(expanded);
        for (int npcId :  encounter.getNpcIds()) {
            // add subtype cards here
            JLabel test = new JLabel("" +  npcId);
            southPanel.add(test);
        }
        add(southPanel, BorderLayout.SOUTH);
    }

    private void toggleCard() {
        expanded = !expanded;
        southPanel.setVisible(expanded);
        expandButton.setText(expanded ? DOWN_ARROW : RIGHT_ARROW);
    }
}
