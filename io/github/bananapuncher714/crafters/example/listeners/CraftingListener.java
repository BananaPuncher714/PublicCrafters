package io.github.bananapuncher714.crafters.example.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.bananapuncher714.crafters.PublicCraftersMain;
import io.github.bananapuncher714.crafters.display.CraftDisplay;
import io.github.bananapuncher714.crafters.display.ItemDisplay;
import io.github.bananapuncher714.crafters.events.CraftDisplayUpdateEvent;
import io.github.bananapuncher714.crafters.events.ItemDisplayCreateEvent;
import io.github.bananapuncher714.crafters.example.objects.CustomItemDisplay;

/**
 * An example listener that replaces certain ItemDisplays with a custom one
 * Created on 2017-12-10
 * 
 * @author BananaPuncher714
 */
public class CraftingListener implements Listener {
	
	@EventHandler
	public void onItemDisplayCreateEvent( ItemDisplayCreateEvent event ) {
		ItemDisplay display = event.getItemDisplay();
		if ( display.getSlot() != 4 ) {
			return;
		}
		if ( display.getCraftDisplay().getInventory().getResult().getType() != Material.DIAMOND_CHESTPLATE ) {
			return;
		}
		if ( display.getItem().getType() == Material.DIAMOND ) {
			event.setItemDisplay( new CustomItemDisplay( display.getCraftDisplay(), display.getLocation(), display.getItem(), display.getSlot() ) );
		}
	}
	
	@EventHandler
	public void onCraftDisplayUpdateEvent( CraftDisplayUpdateEvent event ) {
		CraftDisplay craftDisplay = event.getDisplay();
		ItemDisplay display = craftDisplay.getItemDisplays().get( 4 );
		if ( display == null ) {
			return;
		}
		Material result = craftDisplay.getInventory().getResult().getType();
		if ( ( result != Material.DIAMOND_CHESTPLATE && display instanceof CustomItemDisplay ) || ( !( display instanceof CustomItemDisplay ) && display.getItem().getType() == Material.DIAMOND && result == Material.DIAMOND_CHESTPLATE ) ) {
			event.getDisplay().update( 1, 1, true );
		}
	}
}