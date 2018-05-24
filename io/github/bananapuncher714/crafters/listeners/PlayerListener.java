package io.github.bananapuncher714.crafters.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.display.ItemDisplay;
import io.github.bananapuncher714.crafters.display.PacketManager;
import io.github.bananapuncher714.crafters.display.VirtualItemDisplay;
import io.github.bananapuncher714.crafters.events.ItemDisplayCreateEvent;

public class PlayerListener implements Listener {
	PublicCrafters plugin;
	
	public PlayerListener( PublicCrafters plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoinEvent( PlayerJoinEvent event ) {
		if ( !plugin.isVirtual() ) {
			return;
		}
		Player player = event.getPlayer();
		PacketManager.spawnAll( player );
	}
	
	@EventHandler( priority = EventPriority.LOWEST )
	public void onItemDisplayCreateEvent( ItemDisplayCreateEvent event ) {
		if ( !plugin.isVirtual() ) {
			return;
		}
		ItemDisplay display = event.getItemDisplay();
		VirtualItemDisplay vDisplay = new VirtualItemDisplay( display.getCraftDisplay(), display.getLocation(), display.getItem(), display.getSlot() );
		event.setItemDisplay( vDisplay );
	}
}
