package dev.blonks.osrs.oceanencounters;

import com.google.inject.Binder;

import javax.inject.Inject;

import dev.blonks.osrs.oceanencounters.module.ComponentManager;
import dev.blonks.osrs.oceanencounters.module.OceanEncounterModule;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Ocean Encounter Stats"
)
public class OceanEncounterPlugin extends Plugin
{
	@Inject
	private ComponentManager componentManager;


	@Override
	public void configure(Binder binder) {
		binder.install(new OceanEncounterModule());
	}

	@Override
	protected void startUp() throws Exception
	{
		componentManager.onPluginStart();
	}

	@Override
	protected void shutDown() throws Exception
	{
		componentManager.onPluginStop();
	}

}
