package dev.blonks.osrs.oceanencounters.features.util;

import dev.blonks.osrs.oceanencounters.module.PluginLifecycleComponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.WorldEntity;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class LocationService implements PluginLifecycleComponent {
    private final Client client;

    @Getter
    private WorldEntity boatEntity;


    private LocalPoint lastLocalLocation;
    private LocalPoint lastLocalTarget;
	private WorldPoint lastWorldTarget;
    private int sceneBaseX = -1;
    private int sceneBaseY = -1;

    // TODO: Probably fix this up a bit to make it easier to use

    /**
     * Pre-emptively update the last location in the internal state
     * to ensure that all events that fire before the GameTick of
     * the next cycle will be using correct information
     *
     * @param gameTick
     */
    @Subscribe(priority = -100f)
    private void onGameTick(GameTick gameTick) {
        lastLocalTarget = getLocalTargetLocation();
        lastLocalLocation = getLocalLocation();
		lastWorldTarget = getWorldTargetLocation();
    }

	public LocalPoint getLocalLocation() {
		WorldEntity boat = getBoat();
		if (boat == null) {
			return null;
		}

		return boat.getLocalLocation();
	}

    public LocalPoint getLastLocalLocation() {
        lastLocalLocation = checkSceneBase(lastLocalLocation);
        return lastLocalLocation;
    }

	public LocalPoint getLocalTargetLocation() {
		WorldEntity boat = getBoat();
		if (boat == null) {
			return null;
		}

		return boat.getTargetLocation();
	}

    public LocalPoint getLastLocalTarget() {
        lastLocalTarget = checkSceneBase(lastLocalTarget);
        return lastLocalTarget;
    }

	public WorldPoint getWorldTargetLocation() {
		LocalPoint targetLoc = getLocalTargetLocation();
		if (targetLoc == null) {
			return null;
		}

		return WorldPoint.fromLocal(
			client,
			targetLoc
		);
	}

	public WorldPoint getLastWorldTargetLocation()
	{
		return lastWorldTarget;
	}

    private LocalPoint checkSceneBase(LocalPoint localLocation) {
        WorldView tlwv = client.getTopLevelWorldView();
        int baseX = tlwv.getScene().getBaseX();
        int baseY = tlwv.getScene().getBaseY();

        if (baseX == sceneBaseX && baseY == sceneBaseY) {
            return localLocation;
        }

        LocalPoint adjusted = null;
        if (localLocation != null) {
            int xAdjust = baseX - sceneBaseX;
            int yAdjust = baseY - sceneBaseY;

            adjusted = new LocalPoint(
                    localLocation.getX() - (xAdjust * Perspective.LOCAL_TILE_SIZE),
                    localLocation.getY() - (yAdjust * Perspective.LOCAL_TILE_SIZE),
                    tlwv
            );
        }

        sceneBaseX = client.getTopLevelWorldView().getScene().getBaseX();
        sceneBaseY = client.getTopLevelWorldView().getScene().getBaseY();
        return adjusted;
    }

	public int getOrientation()
	{
		WorldEntity boat = getBoat();
		if (boat == null)
		{
			return 0;
		}

		return boat.getOrientation();
	}

    public int getTargetOrientation() {
        WorldEntity boat = getBoat();
        if (boat == null) {
            return 0;
        }

        return boat.getTargetOrientation();
    }

    public WorldEntity getBoat() {
        if (client.getLocalPlayer() == null) {
            return null;
        }
        if (client.getLocalPlayer().getWorldView().isTopLevel()) {
            return null;
        }
        return client.getTopLevelWorldView().worldEntities().byIndex(
                client.getLocalPlayer().getWorldView().getId()
        );
    }

    public double getTrueSpeed() {
        return LocationService.calcTrueSpeed(lastLocalTarget, getLocalTargetLocation());
    }

    public int getRoundedQuarterTileSpeed() {
        if (lastLocalTarget == getLocalTargetLocation()) {
            log.info("Same position: {}, {}", lastLocalTarget, getLocalTargetLocation());
        }
        return LocationService.calcRoundedQuarterTileSpeed(lastLocalTarget, getLocalTargetLocation());
    }

    public int getSpeed() {
        return getRoundedQuarterTileSpeed() / 32;
    }


    /**
     * Calculate the true distance between two boat-world local points
     * @param oldBoatLocation The previous boat location
     * @param newBoatLocation The new boat location
     * @return The hypotenuse of the two coordinates.
     */
    public static double calcTrueSpeed(LocalPoint oldBoatLocation, LocalPoint newBoatLocation) {
        if (oldBoatLocation == null || newBoatLocation == null) {
            return 0.0;
        }

        if (oldBoatLocation == newBoatLocation) {
            return 0.0;
        }

        return (float) Math.hypot(
                (oldBoatLocation.getX() - newBoatLocation.getX()),
                (oldBoatLocation.getY() - newBoatLocation.getY())
        );
    }


    public static int calcRoundedQuarterTileSpeed(LocalPoint oldBoatLocation, LocalPoint newBoatLocation) {
        double trueSpeed = calcTrueSpeed(oldBoatLocation, newBoatLocation);
        return roundToQuarterTile(trueSpeed);
    }

    /**
     * Boat worldview coords are 128-based (meaning 128 boaty world tiles
     * is one overworld tile). This method rounds to the nearest quarter-tile
     * interval, meaning it rounds the boat-world tiles given to the nearest multiple
     * of 32.
     * @param boatTilesTravelled The number of boat-world tiles travelled (i.e. speed)
     * @return The number of boat tiles travelled, rounded to the nearest multiple of 32
     */
    public static int roundToQuarterTile(double boatTilesTravelled) {
        // wipes out the first 5 bits of boatTilesTravelled (i.e. rounds to multiple of 32)
        int quarterTileFloor = ((int) boatTilesTravelled) & ~0x1F;
        // adds 32 to the floor
        int quarterTileCeil = quarterTileFloor + 0x20;
        log.trace("{} = {} {}", boatTilesTravelled, quarterTileFloor, quarterTileCeil);

        // Round up to the ceiling if its closer
        if (quarterTileCeil - boatTilesTravelled < boatTilesTravelled - quarterTileFloor)
        {
            return quarterTileCeil;
        }

        return quarterTileFloor;
    }
}
