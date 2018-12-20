package io.github.bananapuncher714.crafters.listeners;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.implementation.API.PublicCraftingInventory;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * This is a relatively easy class to understand
 * All it does is detect when something happens to one of the crafting tables
 * The priority is high so that there's no accidental moving or such
 *
 * Created on 2017-12-09
 * 
 * @author BananaPuncher714
 */
public class CraftBlockListener implements Listener {
	private final PublicCrafters plugin;
	
	public CraftBlockListener( PublicCrafters plugin ) {
		this.plugin = plugin;
	}
	
	/**
	 * For these kinds of events, all you need is a simple removal. The CraftInventoryManager should handle the rest
	 * @param event
	 * The BlockBreakEvent
	 */
	@EventHandler( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	private void onBlockBreakEvent( BlockBreakEvent event ) {
		plugin.getManager().remove( event.getBlock().getLocation() );
	}
	
	@EventHandler( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	private void onBlockExplodeEvent( BlockExplodeEvent event ) {
		for ( Block block : event.blockList() ) {
			plugin.getManager().remove( block.getLocation() );
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	private void onEntityExplodeEvent( EntityExplodeEvent event ) {
		for ( Block block : event.blockList() ) {
			plugin.getManager().remove( block.getLocation() );
		}
	}
	
	/**
	 * These are a bit more complicated; First, get all the blocks BEFORE moving them
	 * Then, you can safely move them; This is to prevent overwriting crafting tables at their new location
	 * @param event
	 * The BlockPistonExtendEvent
	 */
	@EventHandler( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	private void onPistonExtendEvent( BlockPistonExtendEvent event ) {
		BlockFace face = event.getDirection();
		Set< PublicCraftingInventory > moveThese = new HashSet< PublicCraftingInventory >();
		for ( Block block : event.getBlocks() ) {
			PublicCraftingInventory crafting = plugin.getManager().get( block.getLocation() );
			if ( crafting != null ) {
				moveThese.add( crafting );
			}
		}
		for ( PublicCraftingInventory crafting : moveThese ) {
			crafting.move( crafting.getLocation().getBlock().getRelative( face ).getLocation() );
		}
	}
	
	@EventHandler( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	private void onPistonRetractEvent( BlockPistonRetractEvent event ) {
		BlockFace face = event.getDirection();
		Set< PublicCraftingInventory > moveThese = new HashSet< PublicCraftingInventory >();
		for ( Block block : event.getBlocks() ) {
			PublicCraftingInventory crafting = plugin.getManager().get( block.getLocation() );
			if ( crafting != null ) {
				moveThese.add( crafting );
			}
		}
		for ( PublicCraftingInventory crafting : moveThese ) {
			crafting.move( crafting.getLocation().getBlock().getRelative( face ).getLocation() );
		}
	}
}
