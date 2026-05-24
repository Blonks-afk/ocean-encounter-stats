package dev.blonks;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class OceanEncounterStatsTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(OceanEncounterStatsPlugin.class);
		RuneLite.main(args);
	}
}