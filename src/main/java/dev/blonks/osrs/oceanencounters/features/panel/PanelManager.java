package dev.blonks.osrs.oceanencounters.features.panel;

import dev.blonks.osrs.oceanencounters.module.PluginLifecycleComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PanelManager implements PluginLifecycleComponent {
    private final ClientToolbar clientToolbar;
    private NavigationButton navButton;
    private MainPanel mainPanel;

    @Override
    public void startup() {
        navButton = NavigationButton.builder()
                .priority(30)
                .tooltip("Ocean Encounters")
                .panel(new MainPanel())
                .icon(ImageUtil.loadImageResource(getClass(), "/Giant_clam_(pearl).png"))
                .build();
        clientToolbar.addNavigation(navButton);
    }

    @Override
    public void shutdown() {
        clientToolbar.removeNavigation(navButton);
        navButton = null;
    }
}
