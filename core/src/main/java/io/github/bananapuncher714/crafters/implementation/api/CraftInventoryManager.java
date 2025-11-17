package io.github.bananapuncher714.crafters.implementation.api;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link PublicCraftingInventory} manager; Organizes, loads, and saves all the inventories;
 * Created on 2017-12-07
 * 
 * @author BananaPuncher714
 */
public interface CraftInventoryManager {
	/**
	 * Get the {@link PublicCraftingInventory} at the specified location.
	 * 
	 * @param location
	 * Any location, does not need to be a crafting table.
	 * @return
	 * null if no inventory has been created.
	 */
	public PublicCraftingInventory get( Location location );
	
	/**
	 * Get the location of an {@link PublicCraftingInventory} from a crafting inventory
	 * 
	 * @param inventory
	 * A public crafting table inventory
	 * @return
	 * null if the inventory isn't a public crafting inventory
	 */
	public Location getLocation( Inventory inventory );
	
	/**
	 * Delete the {@link PublicCraftingInventory} at the specified location, if one exists.
	 * @param location
	 * The location of a possible inventory.
	 */
	public void remove( Location location );
	
	/**
	 * Open a public workbench for the specified player at the location provided
	 * 
	 * @param player
	 * The player to open for
	 * @param location
	 * The location; will create a new {@link PublicCraftingInventory} if none exists
	 */
	public void openWorkbench( Player player, Location location, InventoryType type );
	
	/**
	 * Stop and save all the loaded {@link PublicCraftingInventory}
	 */
	public void stopAll();
	
	/**
	 * Load a {@link PublicCraftingInventory} with the given items
	 * 
	 * @param location
	 * Any location without an inventory already
	 * @param items
	 * Must be a list of 9 items
	 */
	public void load( Location location, List< ItemStack > items );
	
	/**
	 * Save all the tables inside the specified chunk; this will delete the tables in that chunk until they're loaded back in.
	 * 
	 * @param chunk
	 * The chunk to save them in;
	 * @return
	 * Whether or not any tables were found and saved.
	 */
	public boolean unload( Chunk chunk );
	
	/**
	 * Plays an arm swing animation for all other nearby players, randomly chooses left or right arm.
	 * 
	 * @param player
	 * The player who is supposed to swing their arm.
	 */
	public void animate( Player player );
}
