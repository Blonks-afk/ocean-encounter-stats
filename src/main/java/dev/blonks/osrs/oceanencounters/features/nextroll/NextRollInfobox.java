package dev.blonks.osrs.oceanencounters.features.nextroll;

import dev.blonks.osrs.oceanencounters.OceanEncounterConfig;
import dev.blonks.osrs.oceanencounters.OceanEncounterPlugin;
import dev.blonks.osrs.oceanencounters.module.PluginLifecycleComponent;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Slf4j
@Singleton
public class NextRollInfobox extends InfoBox implements PluginLifecycleComponent {

    private final Client client;
    private final NextRollTracker tracker;

    @Inject
    public NextRollInfobox(OceanEncounterPlugin plugin, Client client, NextRollTracker tracker) {
        super(null, plugin);
        setImage(ImageUtil.loadImageResource(getClass(), "/Giant_clam_(pearl).png"));

        this.client = client;
        this.tracker = tracker;
    }

    @Override
    public boolean isEnabled(OceanEncounterConfig config) {
        if (!config.displayRollTimer()) {
            return false;
        }
        return OceanEncounterConfig.NextRollDisplayType.INFOBOX.equals(config.displayType());
    }

    @Override
    public String getText() {
        return String.format("%d (%d)", tracker.getTicksRemaining(), tracker.getTicksMoving());
    }

    @Override
    public Color getTextColor() {
        return Color.WHITE;
    }

    @Override
    public boolean render() {
        return !client.getLocalPlayer().getWorldView().isTopLevel();
    }
}
