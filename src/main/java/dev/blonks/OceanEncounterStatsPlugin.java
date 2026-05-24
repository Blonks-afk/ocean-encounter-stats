package dev.blonks;

import com.google.inject.Provides;
import javax.inject.Inject;

import dev.blonks.encounter.CurrentStats;
import dev.blonks.encounter.Encounter;
import dev.blonks.encounter.EncounterStats;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.ArrayList;
import java.util.List;

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
	private CurrentStats currentStats = new CurrentStats();

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
		currentStats.setTicksSinceLast(currentStats.getTicksSinceLast() + 1);
		var player = client.getLocalPlayer();
		if (player == null) return;
		WorldPoint tlwp = getTopLevelWorldPoint(client);
		if (tlwp == null) return;

		if (lastWorldPoint != null) {
			// have previous tracking, update stats

			int onBoat = client.getVarbitValue(VarbitID.SAILING_PLAYER_IS_ON_PLAYER_BOAT);
			if (onBoat == 1) {

				int distanceTravelled = lastWorldPoint.distanceTo(tlwp);
				currentStats.setTilesSinceLast(currentStats.getTilesSinceLast() + distanceTravelled);

				int helmStatus = client.getVarbitValue(VarbitID.SAILING_SIDEPANEL_HELM_STATUS);
				int playerAtHelm = client.getVarbitValue(VarbitID.SAILING_SIDEPANEL_PLAYER_AT_HELM);
				int freesail = client.getVarbitValue(VarbitID.KEEP_SAILING_BOAT_WHEN_LEAVING_HELM);

				if (distanceTravelled == 0) {
					// update stationary ticks
					currentStats.setStationaryTicksSinceLast(currentStats.getStationaryTicksSinceLast() + 1);
				}

				if (helmStatus == 1 && freesail == 1) {
					// helm is unmanned and freesail setting is enabled
					currentStats.setTicksFreeSteering(currentStats.getTicksFreeSteering() + 1);
				}

				if (playerAtHelm == 1) {
					// player is at helm
					currentStats.setTicksSteering(currentStats.getTicksSteering() + 1);
				}

				if (helmStatus > 2) {
					// helm is manned by a crewmate
					currentStats.setTicksCrewSteering(currentStats.getTicksCrewSteering() + 1);
				}
			} else {
				// player is on land
				currentStats.setTicksOnMainland(currentStats.getTicksOnMainland() + 1);
			}


		}
		lastWorldPoint = tlwp;

		int regionId = tlwp.getRegionID();
		if (regionId != lastRegionId) {
			if (lastRegionId != 0) {
				currentStats.setRegionBordersSinceLast(currentStats.getRegionBordersSinceLast() + 1);
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
		if (currentStats.getTicksSinceLast() <= DEBOUNCE_TICKS) {
			return;
		}
		Encounter encounterType = getEncounterType(e.getNpc().getId());

		if (encounterType != null) {
			EncounterStats stat = currentStats.createEncounterStats(encounterType);
			String eventConfig = configManager.getConfiguration("oceanEncounterStats", "encountersExport", String.class);
			eventConfig += "\n" + stat.toString();
			configManager.setConfiguration("oceanEncounterStats", "encountersExport", eventConfig);
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "Ocean Encounter", stat.toString(), "Ocean Encounter");
			stats.add(stat);
		}
	}

	private Encounter getEncounterType(int npcId) {
		Encounter type = null;
		switch (npcId)
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
				type = Encounter.CASTAWAY;
				break;
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_BEGINNER:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_EASY:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_MEDIUM:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_HARD:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_ELITE:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CASKET_MASTER:
				type = Encounter.CASKET;
				break;
			case NpcID.SAILING_CHANCE_ENCOUNTER_CLUE_TURTLE:
				type = Encounter.TURTLE;
				break;
			case NpcID.SAILING_CHANCE_ENCOUNTERS_CLAM_OPEN:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_CLAM_PEARL:
				type = Encounter.CLAM;
				break;
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_WOOD:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_OAK:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_TEAK:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_MAHOGANY:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_CAMPHOR:
			case NpcID.SAILING_CHANCE_ENCOUNTER_LOST_GOODS_IRONWOOD:
				type = Encounter.CRATE;
				break;
			case NpcID.SAILING_CHANCE_ENCOUNTERS_GLOW:
				type = Encounter.GLOW;
				break;
			case NpcID.SAILING_CHANCE_ENCOUNTERS_WIND_HIGH:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_WIND_MED:
			case NpcID.SAILING_CHANCE_ENCOUNTERS_WIND_LOW:
				type = Encounter.WIND;
				break;
			case NpcID.SAILING_CHANCE_ENCOUNTERS_OCEAN_MAN:
				type = Encounter.MAN;
				break;
		}
		return type;
	}

	@Provides
    OceanEncounterStatsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OceanEncounterStatsConfig.class);
	}
}
