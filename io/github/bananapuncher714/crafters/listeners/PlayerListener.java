package io.github.bananapuncher714.crafters.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.display.CraftResultDisplay;
import io.github.bananapuncher714.crafters.display.ItemDisplay;
import io.github.bananapuncher714.crafters.display.VirtualCraftResultDisplay;
import io.github.bananapuncher714.crafters.display.VirtualItemDisplay;
import io.github.bananapuncher714.crafters.events.CraftResultDisplayCreateEvent;
import io.github.bananapuncher714.crafters.events.ItemDisplayCreateEvent;

public class PlayerListener implements Listener {
	private final PublicCrafters plugin;
	
	public PlayerListener( PublicCrafters plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	private void onPlayerJoinEvent( PlayerJoinEvent event ) {
		if ( !plugin.isVirtual() ) {
			return;
		}
		Player player = event.getPlayer();
		VirtualItemDisplay.spawnAll( player );
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onItemDisplayCreateEvent( ItemDisplayCreateEvent event ) {
		if ( !plugin.isVirtual() ) {
			return;
		}
		
		ItemDisplay display = event.getItemDisplay();
		VirtualItemDisplay vDisplay = new VirtualItemDisplay( display.getCraftDisplay(), display.getLocation(), display.getItem(), display.getSlot() );
		event.setItemDisplay( vDisplay );
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	private void onCraftResultCreateEvent( CraftResultDisplayCreateEvent event ) {
		if ( !plugin.isVirtual() ) {
			return;
		}
		CraftResultDisplay display = event.getDisplay();
		VirtualCraftResultDisplay vDisplay = new VirtualCraftResultDisplay( display.getCraftDisplay(), display.getLocation(), display.getItem() );
		event.setCraftResultDisplay( vDisplay );
	}
}
