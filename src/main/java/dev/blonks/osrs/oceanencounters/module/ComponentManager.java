package dev.blonks.osrs.oceanencounters.module;

import dev.blonks.osrs.oceanencounters.OceanEncounterConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.GameEventManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ComponentManager {
    private final EventBus eventBus;
    private final OverlayManager overlayManager;
    private final InfoBoxManager infoBoxManager;
    private final GameEventManager gameEventManager;
    private final OceanEncounterConfig config;
    private final Set<PluginLifecycleComponent> components;
    private final Map<PluginLifecycleComponent, Boolean> states = new HashMap<>();

    public void onPluginStart() {
        eventBus.register(this);
        components.forEach(c -> states.put(c, false));
        revalidateComponentStates();
    }

    public void onPluginStop() {
        eventBus.unregister(this);
        components.stream()
                .filter(states::get)
                .forEach(this::tryShutdown);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged e) {
        if (!OceanEncounterConfig.CONFIG_GROUP.equals(e.getGroup())) {
            return;
        }

        revalidateComponentStates();
    }

    private void revalidateComponentStates() {
        components.forEach(c -> {
            boolean shouldEnable = c.isEnabled(config);
            boolean isEnabled = states.get(c);
            if (shouldEnable == isEnabled) {
                return;
            }
            if (shouldEnable) {
                tryStartup(c);
            } else {
                tryShutdown(c);
            }
        });
    }

    private void tryStartup(PluginLifecycleComponent component) {
        if (states.get(component)) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Enabling component {}", component.getClass().getName());
        }

        try {
            component.startup();
            eventBus.register(component);
            if (component instanceof Overlay) {
                overlayManager.add((Overlay) component);
            }
            if (component instanceof InfoBox) {
                infoBoxManager.addInfoBox((InfoBox) component);
            }

            gameEventManager.simulateGameEvents(component);
            states.put(component, true);
        } catch (Throwable e) {
            log.error("Failed to start component {}", component.getClass().getName(), e);
        }
    }

    private void tryShutdown(PluginLifecycleComponent component) {
        eventBus.unregister(component);
        if (component instanceof Overlay) {
            overlayManager.remove((Overlay) component);
        }
        if (component instanceof InfoBox) {
            infoBoxManager.removeInfoBox((InfoBox) component);
        }

        if (!states.get(component)) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Shutting down component {}", component.getClass().getName());
        }

        try {
            component.shutdown();
        } catch (Throwable e) {
            log.error("Failed to cleanly shutdown component {}", component.getClass().getName(), e);
        } finally {
            states.put(component, false);
        }
    }
}
