package io.github.bananapuncher714.crafters.display;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.events.CraftDisplayDestroyEvent;
import io.github.bananapuncher714.crafters.events.CraftDisplayUpdateEvent;
import io.github.bananapuncher714.crafters.events.ItemDisplayCreateEvent;
import io.github.bananapuncher714.crafters.events.ItemDisplayDestroyEvent;
import io.github.bananapuncher714.crafters.implementation.API.PublicCraftingInventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * This is a per-inventory object, it manages all the 9 slots of a crafting table;
 * Created on 2017-12-07
 * 
 * @author BananaPuncher714
 */
public class CraftDisplay implements Runnable {
	protected final List< ItemDisplay > displays = new ArrayList< ItemDisplay >();
	protected final Location blockLoc;
	protected final PublicCraftingInventory inventory;
	protected double height;

	/**
	 * Provide a PublicCraftingInventory, and the default height is used
	 * 
	 * @param inventory
	 * It must be a {@link PublicCraftingInventory}, NOT your normal Bukkit inventory
	 */
	public CraftDisplay( PublicCraftingInventory inventory ) {
		this( inventory, PublicCrafters.getInstance().getHeight() );
	}

	/**
	 * Same as {@link #CraftDisplay(PublicCraftingInventory)}, but now you can provide the height of the items
	 * 
	 * @param inventory
	 * Your {@link PublicCraftingInventory} instance
	 * @param itemHeight
	 * The height of the item that will be displayed above the table
	 */
	public CraftDisplay( PublicCraftingInventory inventory, double itemHeight ) {
		blockLoc = inventory.getLocation();
		height = itemHeight;
		this.inventory = inventory;
		for ( int i = 0; i < 9; i++ ) {
			displays.add( null );
		}
		update();
	}

	/**
	 * All this really does is run the {@link #run()} method some ticks after this is called
	 */
	public void update() {
		Bukkit.getScheduler().scheduleSyncDelayedTask( PublicCrafters.getInstance(), this, PublicCrafters.getInstance().getUpdateDelay() );
	}
	
	/**
	 * This forces an update immediately, without delay and forcefully.
	 */
	public void forceUpdate() {
		if ( inventory == null ) {
			return;
		}

		CraftDisplayUpdateEvent updateEvent = new CraftDisplayUpdateEvent( this );
		Bukkit.getPluginManager().callEvent( updateEvent );
		if ( updateEvent.isCancelled() ) {
			return;
		}

		for ( int i = 0; i < 3; i++ ) {
			for ( int j = 0; j < 3; j++ ) {
				update( i, j, true );
			}
		}
	}
	
	/**
	 * This is the passive update method; see {@link #update(int, int, boolean)}
	 * 
	 * @param col
	 * The columns of a workbench, from left to right, counting 0 to 2
	 * @param row
	 * The rows of a workbench, from top to bottom, counting 0 to 2
	 */
	public void update( int col, int row ) {
		update( col, row, false );
	}
	
	/**
	 * Don't call this from the ItemDisplayCreateEvent or the ItemDisplayDestroyEvent, as it will cause a stackOverflowException;
	 * 
	 * @param col
	 * The columns of a workbench, from left to right, counting 0 to 2
	 * @param row
	 * The rows of a workbench, from top to bottom, counting 0 to 2
	 * @param force
	 * Whether or not to force an update if the items in the current slot are similar
	 */
	public void update( int col, int row, boolean force ) {
		List< ItemStack > bukkitItems = inventory.getBukkitItems();
		int index = col + 3 * row;
		ItemStack item = bukkitItems.get( index );
		ItemDisplay display = displays.get( index );
		if ( display != null && ( display.getItem().getType() == Material.AIR || item.getType() == Material.AIR ) ) {
			ItemDisplayDestroyEvent event = new ItemDisplayDestroyEvent( display );
			Bukkit.getPluginManager().callEvent( event );
			display.remove();
			displays.set( index, null );
			return;
		}
		if ( item.getType() == Material.AIR ) {
			return;
		}
		if ( force || display == null || !item.isSimilar( display.getItem() ) ) {
			Location newLoc;
			if ( item.getType().isBlock() ) {
				newLoc = blockLoc.clone().add( .33125 + .2 * ( 2 - col ), height, .13125 + .2 * ( 2 - row ) );
			} else {
				newLoc = blockLoc.clone().add( .49125 + .2 * ( 2 - col ), height, .14125 + .2 * ( 2 - row ) );
			}
			if ( display != null ) {
				ItemDisplayDestroyEvent event = new ItemDisplayDestroyEvent( display );
				Bukkit.getPluginManager().callEvent( event );
				display.remove();
			}
			display = new ItemDisplay( this, newLoc, item, index );
			ItemDisplayCreateEvent event = new ItemDisplayCreateEvent( blockLoc.clone(), display );
			Bukkit.getPluginManager().callEvent( event );
			if ( event.isCancelled() ) {
				displays.set( index, null );
				return;
			}
			display = event.getItemDisplay();
			display.init();
			displays.set( index, display );
		}
	}
	
	/**
	 * Simply stops each of the 9 ItemDisplays this is responsible for
	 */
	public void stop() {
		Bukkit.getPluginManager().callEvent( new CraftDisplayDestroyEvent( this ) );
		for ( ItemDisplay display : displays ) {
			if ( display != null ) {
				ItemDisplayDestroyEvent event = new ItemDisplayDestroyEvent( display );
				Bukkit.getPluginManager().callEvent( event );
				display.remove();
			}
		}
	}

	public PublicCraftingInventory getInventory() {
		return inventory;
	}
	
	public List< ItemDisplay > getItemDisplays() {
		return displays;
	}
	
	public Location getLocation() {
		return blockLoc;
	}
	
	/**
	 * You shouldn't run this directly, use the {@link #update()} method instead
	 */
	@Override
	public void run() {
		if ( inventory == null ) {
			return;
		}

		CraftDisplayUpdateEvent updateEvent = new CraftDisplayUpdateEvent( this );
		Bukkit.getPluginManager().callEvent( updateEvent );
		if ( updateEvent.isCancelled() ) {
			return;
		}

		for ( int i = 0; i < 3; i++ ) {
			for ( int j = 0; j < 3; j++ ) {
				update( i, j );
			}
		}
	}
}
