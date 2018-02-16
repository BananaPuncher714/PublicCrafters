package io.github.bananapuncher714.crafters.listeners;

import io.github.bananapuncher714.crafters.PublicCraftersMain;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * Nothing more than a class to display the hand swing animation during crafting;
 * Created on 2017-12-09
 * 
 * @author BananaPuncher714
 */
public class InventoryUpdateListener implements Listener {
	PublicCraftersMain plugin;
	
	public InventoryUpdateListener( PublicCraftersMain plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onInventoryUpdateListener( InventoryClickEvent event ) {
		Inventory inventory = event.getInventory();
		if ( inventory == null ) {
			return;
		}
		if ( event.getRawSlot() != event.getSlot() ) {
			return;
		}
		Location location = PublicCraftersMain.getInstance().getManager().getLocation( inventory );
		if ( location != null ) {
			HumanEntity human = event.getWhoClicked();
			if ( human instanceof Player ) {
				Player player = ( Player ) human;
				plugin.getManager().animate( player );
			}
		}
	}
}
