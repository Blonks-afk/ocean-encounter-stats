package dev.blonks;

import com.google.inject.Provides;
import javax.inject.Inject;

import dev.blonks.encounter.Encounter;
import dev.blonks.encounter.EncounterStats;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@PluginDescriptor(
	name = "Ocean Encounter Stats"
)
public class OceanEncounterStatsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OceanEncounterStatsConfig config;

	@Inject
	private OceanEncounterStatsOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ConfigManager configManager;

	@Getter
	private int ticksSinceLast = 0;
	@Getter
	private int regionBordersSinceLast = 0;
	@Getter
	private int tilesSinceLast = 0;
	@Getter
	private List<EncounterStats> stats;
	private int lastRegionId = 0;
	private WorldPoint lastWorldPoint = null;
	private final int DEBOUNCE_TICKS = 50;

	@Override
	protected void startUp() throws Exception
	{
		stats = new ArrayList<>();
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	private void onGameTick(GameTick tick) {
		ticksSinceLast++;
		var player = client.getLocalPlayer();
		if (player == null) return;
		WorldPoint tlwp = getTopLevelWorldPoint(client);
		if (tlwp == null) return;

		if (lastWorldPoint != null) {
			tilesSinceLast += lastWorldPoint.distanceTo(tlwp);
		}
		lastWorldPoint = tlwp;

		int regionId = tlwp.getRegionID();
		if (regionId != lastRegionId) {
			if (lastRegionId != 0) {
				regionBordersSinceLast++;
			}
			lastRegionId = regionId;
		}
	}

	public LocalPoint getTopLevelLocalPoint(Client client)
	{
		Player player = client.getLocalPlayer();
		if (player == null) return null;
		WorldView wv = player.getWorldView();
		if (wv.isTopLevel())
		{
			return player.getLocalLocation();
		}

		return client.getTopLevelWorldView()
				.worldEntities()
				.byIndex(wv.getId())
				.transformToMainWorld(client.getLocalPlayer().getLocalLocation());
	}

	public WorldPoint getTopLevelWorldPoint(Client client)
	{
		LocalPoint loc = getTopLevelLocalPoint(client);
		if (loc == null) return null;

		return WorldPoint.fromLocal(
				client,
				getTopLevelLocalPoint(client)
		);
	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned e) {
		if (ticksSinceLast <= DEBOUNCE_TICKS) {
			return;
		}
		Encounter encounterType = null;
		switch (e.getNpc().getId())
		{
			case NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_MAN1_ADRIFT:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_MAN1_PASSENGER:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_MAN2_ADRIFT:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_MAN2_PASSENGER:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN1_ADRIFT:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN1_PASSENGER:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN2_ADRIFT:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN2_PASSENGER:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN3_ADRIFT:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WOMAN3_PASSENGER:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WILSON_ADRIFT:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_WILSON_PASSENGER:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_DOG1_ADRIFT:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_RESCUE_DOG1_PASSENGER:
				encounterType = Encounter.CASTAWAY;
				break;
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_BEGINNER:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_EASY:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_MEDIUM:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_HARD:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_ELITE:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_MASTER:
				encounterType = Encounter.CASKET;
				break;
			case NpcID.SAILING_CHANCE_ENCOUNTER_CLUE_TURTLE:
				encounterType = Encounter.TURTLE;
				break;
			case NpcID.SAILING_CHANCE_ENCOUNTERS_CLAM_OPEN:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_CLAM_PEARL:
				encounterType = Encounter.CLAM;
				break;
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_WOOD:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_OAK:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_TEAK:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_MAHOGANY:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CAMPHOR:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_IRONWOOD:
				encounterType = Encounter.CRATE;
				break;
			case NpcID.SAILING_CHANCE_ENCOUNTERS_GLOW:
				encounterType = Encounter.GLOW;
				break;
			case NpcID.SAILING_CHANCE_ENCOUNTERS_WIND_HIGH:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_WIND_MED:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_WIND_LOW:
				encounterType = Encounter.WIND;
				break;
			case NpcID.SAILING_CHANCE_ENCOUNTERS_OCEAN_MAN:
				encounterType = Encounter.MAN;
				break;
		}

		if (encounterType != null) {
			EncounterStats stat = new EncounterStats(encounterType, ticksSinceLast, regionBordersSinceLast, tilesSinceLast);
			String eventConfig = configManager.getConfiguration("oceanEncounterStats", "encountersExport", String.class);
			eventConfig += "\n" + stat.toString();
			configManager.setConfiguration("oceanEncounterStats", "encountersExport", eventConfig);
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "Ocean Encounter", stat.toString(), "Ocean Encounter");
			stats.add(stat);
			ticksSinceLast = 0;
			regionBordersSinceLast = 0;
			tilesSinceLast = 0;
		}
	}

	@Provides
    OceanEncounterStatsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OceanEncounterStatsConfig.class);
	}
}
