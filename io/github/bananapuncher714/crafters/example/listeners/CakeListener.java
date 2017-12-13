package io.github.bananapuncher714.crafters.example.listeners;

import java.util.HashMap;
import java.util.Map;

import io.github.bananapuncher714.crafters.PublicCraftersMain;
import io.github.bananapuncher714.crafters.display.CraftDisplay;
import io.github.bananapuncher714.crafters.events.CraftDisplayDestroyEvent;
import io.github.bananapuncher714.crafters.events.CraftDisplayUpdateEvent;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

public class CakeListener implements Listener {
	private Map< Location, CakeFloater > locations = new HashMap< Location, CakeFloater >();

	public class CakeFloater extends BukkitRunnable {
		private Location location;
		private ArmorStand stand;
		private int deg = 0;

		/**
		 * Just a simple class that spawns a floating, turning armorstand 1 block above a given location
		 * 
		 * @param location
		 * The location of whatever
		 */
		public CakeFloater( Location location ) {
			locations.put( location, this );
			this.location = location;
			stand = ( ArmorStand ) location.getWorld().spawnEntity( location.clone().add( .5, 1, .5 ), EntityType.ARMOR_STAND );
			// It's a beacon because I can't get a cake block to appear
			stand.setHelmet( new ItemStack( Material.BEACON ) );
			stand.setGravity( false );
			stand.setMarker( true );
			stand.setVisible( false );
		}

		@Override
		public void run() {
			stand.setHeadPose( new EulerAngle( 0, Math.toRadians( deg++ ), 0 ) );
		}

		@Override
		public void cancel() {
			super.cancel();
			stand.remove();
			locations.remove( location );
		}
	}

	@EventHandler
	public void onCakeCraftEvent( CraftDisplayUpdateEvent event ) {
		CraftDisplay craftDisplay = event.getDisplay();
		boolean crafted = craftDisplay.getInventory().getResult().getType() == Material.CAKE;

		CakeFloater floater = locations.get( craftDisplay.getLocation() );
		if ( crafted ) {
			if ( floater == null ) {
				floater = new CakeFloater( craftDisplay.getLocation() );
				floater.runTaskTimer( PublicCraftersMain.getInstance(), 0, 1 );
			}
		} else if ( floater != null ) {
			floater.cancel();
		}
	}
	
	@EventHandler
	public void onCakeUncraftEvent( CraftDisplayDestroyEvent event ) {
		Location blockLocation = event.getDisplay().getLocation();
		if ( locations.containsKey( blockLocation ) ) {
			CakeFloater floater = locations.get( blockLocation );
			floater.cancel();
		}
	}
	
	/**
	 * I'm not quite sure if this listener will get the CraftDisplayDestroyEvent on plugin disable, so I'm gonna add this as a failsafe.
	 */
	public void stopAll() {
		for ( CakeFloater floater : locations.values() ) {
			floater.cancel();
		}
	}
}
