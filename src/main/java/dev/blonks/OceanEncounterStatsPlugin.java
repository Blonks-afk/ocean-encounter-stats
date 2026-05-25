package dev.blonks;

import com.google.inject.Provides;
import javax.inject.Inject;

import dev.blonks.encounter.Encounter;
import dev.blonks.encounter.EncounterStats;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ChatMessage;
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

	/**
	 * Tracks the moving tick count of the current encounter
	 */
	@Getter
	private int ticksMoving = 0;
	/**
	 * Tracks the moving tick count of the previous encounter until the NPC spawns
	 */
	@Getter
	private int lastTicksMoving = 0;
	/**
	 * Track the number of encounters actually seen
	 */
	@Getter
	private int encounterCount = 0;
	/**
	 * Track the number of ink stout messages
	 */
	@Getter
	private int messageCount = 0;

	@Getter
	private int speed;

	@Getter
	private Map<Encounter, Integer> encounters = new HashMap<>();
	@Getter
	private List<EncounterStats> encounterStats = new ArrayList<>();
	private LocalPoint lastPoint = null;

	@Getter
	private int nextEncounterRoll = 115;



	@Override
	protected void startUp() throws Exception
	{
		for (Encounter encounter : Encounter.values()) {
			encounters.put(encounter, 0);
		}
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	private void onGameTick(GameTick tick) {
		LocalPoint newPoint = client
				.getTopLevelWorldView().worldEntities()
				.byIndex(client.getLocalPlayer().getWorldView().getId())
				.getTargetLocation();

		if (lastPoint != null) {
			double trueSpeed = (float) Math.hypot(
					(lastPoint.getX() - newPoint.getX()),
					(lastPoint.getY() - newPoint.getY())
			);
			speed = roundToQuarterTile(trueSpeed) / 32;
			if (speed != 0.0) {
				ticksMoving++;
			}
		}
		lastPoint = newPoint;

		int rem = ticksMoving % 115;
		nextEncounterRoll = 115 - rem;
	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned e) {
		Encounter encounterType = getEncounterType(e.getNpc().getId());
		if (encounterType == null) return;
		// duplicate entity spawn, ignore
		if (messageCount == encounterCount) return;

		// Bump encounter count stat
		encounters.put(encounterType, encounters.get(encounterType)+1);
		EncounterStats newStats = new EncounterStats(encounterType, lastTicksMoving);
		encounterStats.add(newStats);
		encounterCount++;
		writeToConfig(newStats);
	}

	private void writeToConfig(EncounterStats stats) {
		String configVal = configManager.getConfiguration("oceanEncounterStats", "encountersExport", String.class);
		configVal += "\n{\"type\":\"" + stats.getEncounter().getName() +
				"\", \"ticks\":\"" + stats.getTicksMoving() + "\"}";
		configManager.setConfiguration("oceanEncounterStats", "encountersExport", configVal);
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "Ocean Encounters", stats.getEncounter().getName() + ", " + String.valueOf(stats.getTicksMoving()), "Ocean Encounters");
	}

	@Subscribe
	private void onChatMessage(ChatMessage e) {
		if (e.getMessage().contains("ink stout:")) {
			messageCount++;
			lastTicksMoving = ticksMoving;
			ticksMoving = 0;
		}
	}

	private static int roundToQuarterTile(double trueSpeed)
	{
		int quarterTileFloor = ((int) trueSpeed) & ~0x1F;
		int quarterTileCeil = quarterTileFloor + 0x20;
		log.trace("{} = {} {}", trueSpeed, quarterTileFloor, quarterTileCeil);

		if (quarterTileCeil - trueSpeed < trueSpeed - quarterTileFloor)
		{
			return quarterTileCeil;
		}

		return quarterTileFloor;
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
