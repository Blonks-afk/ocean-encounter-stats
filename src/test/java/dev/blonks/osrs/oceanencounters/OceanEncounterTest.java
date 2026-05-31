package dev.blonks.osrs.oceanencounters;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class OceanEncounterTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(OceanEncounterPlugin.class);
		RuneLite.main(args);
	}
}