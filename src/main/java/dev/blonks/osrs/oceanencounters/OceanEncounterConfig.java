package dev.blonks.osrs.oceanencounters;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(OceanEncounterConfig.CONFIG_GROUP)
public interface OceanEncounterConfig extends Config
{

	String CONFIG_GROUP = "oceanEncounterStats";

	@ConfigSection(
		name = "Encounter Tracking",
			description = "Settings for encounter tracking",
			position = 1
	)
	String ENCOUNTER_TRACKING = "encounterTracking";

	@ConfigItem(
			keyName = "encounterQuantity",
			name = "Track Seen",
			description = "",
			section = ENCOUNTER_TRACKING
	)
	default boolean encounterQuantity() {
		return true;
	}

	@ConfigItem(
			keyName = "encounterLoot",
			name = "Track Loot",
			description = "",
			section = ENCOUNTER_TRACKING
	)
	default boolean encounterLoot() {
		return true;
	}

	@ConfigItem(
			keyName = "encounterExtras",
			name = "Track Extras",
			description = "An option to configure tracking additional encounter details to a local file." +
					"This includes things like player location, speed, and heading on spawn, as well as the " +
					"encounter location.",
			section = ENCOUNTER_TRACKING
	)
	default boolean encounterExtras() {
		return true;
	}

	@ConfigSection(
			name = "Next Roll",
			description = "",
			position = 2
	)
	String ROLL_TRACKING = "rollTracking";

	@ConfigItem(
			keyName = "displayRollTimer",
			name = "Display Next Roll Timer",
			description = "",
			section = ROLL_TRACKING
	)
	default boolean displayRollTimer() {
		return true;
	}

	enum NextRollDisplayType {
		INFOBOX,
		OVERLAY
	}

	@ConfigItem(
			keyName = "displayType",
			name = "Display Type",
			description = "Config option to choose how you want your next roll timer to be displayed",
			section = ROLL_TRACKING
	)
	default NextRollDisplayType displayType() {
		return NextRollDisplayType.OVERLAY;
	}

	enum NextRollDisplayTime {
		NAVIGATING,
		ALWAYS
	}
	@ConfigItem(
			keyName = "nextRollDisplayTime",
			name = "Display When",
			description = "",
			section = ROLL_TRACKING
	)
	default NextRollDisplayTime nextRollDisplayTime() {
		return NextRollDisplayTime.NAVIGATING;
	}

	@ConfigSection(
			name = "Debug",
			description = "",
			position = 3,
			closedByDefault = true
	)
	String DEBUG = "debug";

	@ConfigItem(
			keyName = "encountersExport",
			name = "Encounters",
			description = "Ocean Encounters",
			section = DEBUG
	)
	default String encounters() {
		return "";
	}
}
