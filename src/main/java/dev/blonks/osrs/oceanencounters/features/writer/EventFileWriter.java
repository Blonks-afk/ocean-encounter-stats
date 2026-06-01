package dev.blonks.osrs.oceanencounters.features.writer;

import dev.blonks.osrs.oceanencounters.OceanEncounterConfig;
import dev.blonks.osrs.oceanencounters.features.encounters.events.EncounterSpawned;
import dev.blonks.osrs.oceanencounters.module.PluginLifecycleComponent;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.http.api.RuneLiteAPI;

import javax.inject.Singleton;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Singleton
public class EventFileWriter implements PluginLifecycleComponent {
    private static final File PLUGIN_DIR = new File(RuneLite.RUNELITE_DIR, "ocean-encounters");
    private static final File LOG_FILE = new File(PLUGIN_DIR, "encounters.log");

    private List<EncounterSpawned> encountersSpawned;

    @Override
    public boolean isEnabled(OceanEncounterConfig config) {
        return config.encounterExtras();
    }

    @Subscribe
    public void onEncounterSpawned(EncounterSpawned encounterSpawned) {
        try {
            PLUGIN_DIR.mkdirs();
            final BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true));
            writer.append(RuneLiteAPI.GSON.toJson(encounterSpawned));
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            log.warn("Error writing encounter to log file {}", LOG_FILE.getAbsolutePath(), e);
        }
    }

}
