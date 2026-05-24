package dev.blonks;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("oceanEncounterStats")
public interface OceanEncounterStatsConfig extends Config
{
	@ConfigItem(
		keyName = "greeting",
		name = "Welcome Greeting",
		description = "The message to show to the user when they login"
	)
	default String greeting()
	{
		return "Hello";
	}

	@ConfigItem(
			keyName = "encountersExport",
			name = "Encounters",
			description = "Ocean Encounters"
	)
	default String encounters() {
		return "";
	}
}
