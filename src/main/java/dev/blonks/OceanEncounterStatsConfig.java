package dev.blonks;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("oceanEncounterStats")
public interface OceanEncounterStatsConfig extends Config
{
	@ConfigItem(
			keyName = "encountersExport",
			name = "Encounters",
			description = "Ocean Encounters"
	)
	default String encounters() {
		return "";
	}
}
