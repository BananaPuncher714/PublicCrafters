package io.github.bananapuncher714.crafters.listeners;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.file.CraftInventoryLoader;
import io.github.bananapuncher714.crafters.implementation.API.CraftInventoryManager;

import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Meant to handle loading and unloading tables as the chunks load and unload;
 * Created on 2017-12-09
 * 
 * @author BananaPuncher714
 */
public class ChunkListener implements Listener {
	private CraftInventoryManager manager;
	
	public ChunkListener( CraftInventoryManager manager ) {
		this.manager = manager;
	}
	
	@EventHandler
	public void onChunkLoadEvent( ChunkLoadEvent event ) {
		Chunk chunk = event.getChunk();
		Map< Location, List< ItemStack > > itemMap = CraftInventoryLoader.loadChunk( PublicCrafters.getInstance().getSaveFolder(), chunk.getWorld(), chunk.getX(), chunk.getZ() );
		for ( Location location : itemMap.keySet() ) {
			manager.load( location, itemMap.get( location ) );
		}
	}

	/**
	 * Gonna have to disable this for now, seeing as I can't determine if its a lazy unload or not.
	 */
	@EventHandler( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onChunkUnloadEvent( ChunkUnloadEvent event ) {
		if ( event.getChunk().isLoaded() ) {
			return;
		}
		manager.unload( event.getChunk() );
	}
}
