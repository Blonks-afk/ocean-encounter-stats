package dev.blonks.osrs.oceanencounters.features.nextroll;

import dev.blonks.osrs.oceanencounters.OceanEncounterConfig;
import dev.blonks.osrs.oceanencounters.features.encounters.events.EncounterSpawned;
import dev.blonks.osrs.oceanencounters.features.encounters.events.InkStoutChatMessage;
import dev.blonks.osrs.oceanencounters.features.util.Constants;
import dev.blonks.osrs.oceanencounters.features.util.LocationService;
import dev.blonks.osrs.oceanencounters.module.PluginLifecycleComponent;
import java.util.ArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class NextRollTracker implements PluginLifecycleComponent
{
	// Set debounce to 110, just before the next possible spawn at 115/120

	private final Client client;
	private final LocationService locationService;

	private boolean queueEncounter = false;

	@Getter
	private int ticksRemaining = 0;
	@Getter
	private int ticksMoving = 0;
	@Getter
	private List<Integer> disembarkPenalties = new ArrayList<>();
	@Getter
	private int ticksDisembarked = 0;
	@Getter
	private boolean disembarkedFlag = false;
	/**
	 * Simple flag to track on a given login session whether the player has
	 * been on their boat. Typically used to decide whether the overlay
	 * with disembark penalty should be rendered or not.
	 */
	@Getter
	private boolean beenOnBoat = false;

	private int currentTickCycle = Constants.SAILING_TICK_PER_ENCOUNTER_ROLL_WITH_KRAKEN;

	@Override
	public boolean isEnabled(OceanEncounterConfig config)
	{
		return config.displayRollTimer();
	}

	@Override
	public void startup()
	{
		resetState();
	}

	@Override
	public void shutdown()
	{
		resetState();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		List<GameState> validStates = List.of(
			GameState.LOGGED_IN,
			GameState.LOADING
		);
		if (!validStates.contains(event.getGameState()))
		{
			resetState();
			beenOnBoat = false;
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		currentTickCycle = determineTickCycle();

		if (client.getLocalPlayer() == null)
		{
			return;
		}
		// Track when players disembark
		if (client.getLocalPlayer().getWorldView().isTopLevel())
		{
			ticksDisembarked++; // Track disembarked ticks to know when overlay should hide
			if (!disembarkedFlag && beenOnBoat) {
				// the first tick after players have disembarked from their boat
				int penalty = Math.min(currentTickCycle - ticksRemaining, 20);
				if (penalty > 0) {
					disembarkPenalties.add(penalty);
					// TODO: Replace with one variable, track remaining with modulo 115/120
					ticksRemaining += penalty;
					ticksMoving -= penalty;
				}
				disembarkedFlag = true;
			}
			return;
		}
		// Player is on a boat, reset some of the flags and trackers
		beenOnBoat = true;
		disembarkedFlag = false;
		ticksDisembarked = 0;

		int speed = locationService.getSpeed();
		if (speed != 0.0)
		{
			ticksRemaining--;
			ticksMoving++;
		}

		// only after incrementing counters, process any queued encounters
		if (queueEncounter)
		{
			queueEncounter = false;
			client.addChatMessage(
				ChatMessageType.NPC_SAY,
				"Ocean Encounters",
				String.format("Spawned after moving for %d ticks!", ticksMoving),
				"Ocean Encounters"
			);
			resetState();
		}

		// After all processing of events is complete, update any counters if they are at their upper/lower bounds
		if (ticksRemaining <= 0)
		{
			ticksRemaining = currentTickCycle;
		}
	}

	/**
	 * Queue processing when an encounter NPC is spawned. If the player has a
	 * Kraken Ink Stout keg on their boat, this will end up just hitting the debounce
	 * and doing nothing. It should naturally trend towards being more accurate
	 * as more encounter spawns are seen.
	 *
	 * @param e
	 */
	@Subscribe
	private void onEncounterSpawned(EncounterSpawned e)
	{
		if (ticksMoving > Constants.ENCOUNTER_DEBOUNCE_TICKS)
		{
			queueEncounter = true;
		}
	}

	/**
	 * Queue encounter spawn processing when the ink stout chat message procs.
	 * Since this is directly from the server, we can disregard any local assumptions
	 * about duration so no need for debounce
	 * @param event
	 */
	@Subscribe
	private void onInkStoutChatMessage(InkStoutChatMessage event)
	{
		queueEncounter = true;
	}

	/**
	 * TODO: Implement 115 vs 120 logic for kraken ink stout
	 */
	private void resetState()
	{
		ticksRemaining = currentTickCycle;
		ticksMoving = 0;
		queueEncounter = false;
		disembarkPenalties.clear();
		beenOnBoat = false;
	}

	private int determineTickCycle()
	{
		// TODO: Implement 115 vs 120 logic for kraken ink stout
		return Constants.SAILING_TICK_PER_ENCOUNTER_ROLL_WITH_KRAKEN;
	}
}
