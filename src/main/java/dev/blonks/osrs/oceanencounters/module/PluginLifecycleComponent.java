package dev.blonks.osrs.oceanencounters.module;

import dev.blonks.osrs.oceanencounters.OceanEncounterConfig;

public interface PluginLifecycleComponent {

    default boolean isEnabled(OceanEncounterConfig config) {
        return true;
    }

    default void startup() {

    }

    default void shutdown() {

    }
}
