package io.github.bananapuncher714.crafters.implementation.API;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.crafters.display.CraftDisplay;

/**
 * This is a representation of a public workbench inventory;
 * Created on 2017-12-07
 * 
 * @author BananaPuncher714
 */
public interface PublicCraftingInventory {
	/**
	 * Get all the items in the inventory, may be null;
	 * 
	 * @return
	 * Returns a list of Bukkit itemstacks.
	 */
	public List< ItemStack > getBukkitItems();
	
	/**
	 * Get this table's unique ID;
	 * 
	 * @return
	 * Returns a unique ID for this session, does not persist across reloads
	 */
	public UUID getUUID();
	
	/**
	 * Get the location of the table
	 * 
	 * @return
	 * May change if the table is moved by pistons
	 */
	public Location getLocation();
	
	/**
	 * This is the {@link CraftDisplay} for this table.
	 * 
	 * @return
	 * Creates a new {@link CraftDisplay} whenever {@link #move(Location)} is called
	 */
	public CraftDisplay getCraftDisplay();
	
	/**
	 * Remove the inventory in its entirety, includes cleaning up the {@link CraftDisplay}.
	 */
	public void remove();
	
	/**
	 * Move this instance to a new location
	 * 
	 * @param location
	 * The new location of the table
	 * @return
	 * The table that occupies the location currently.
	 */
	public PublicCraftingInventory move( Location location );
	
	/**
	 * Update the inventory, will remove itself if there is not a workbench at its current location.
	 */
	public void update();
	
	/**
	 * Get the ItemStack of the crafting recipe's result
	 * @return
	 * A Bukkit ItemStack
	 */
	public ItemStack getResult();
}
