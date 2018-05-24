package io.github.bananapuncher714.crafters.listeners;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.display.ItemDisplay;
import io.github.bananapuncher714.crafters.display.CraftDisplay;
import io.github.bananapuncher714.crafters.implementation.API.PublicCraftingInventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Handles whenever a player clicks on a crafting table or an entity that's part of a {@link PublicCraftingInventory} or {@link CraftDisplay};
 * Created on 2017-12-09
 * 
 * @author BananaPuncher714
 */
public class InventoryOpenListener implements Listener {
	PublicCrafters plugin;
	
	public InventoryOpenListener( PublicCrafters main ) {
		plugin = main;
	}
	
	@EventHandler( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onPlayerInteractEvent( PlayerInteractEvent event ) {
		if ( event.getAction() != Action.RIGHT_CLICK_BLOCK ) {
			return;
		}
		
		Block block = event.getClickedBlock();
		Material type = block.getType();
		InventoryType invType = getTypeFromMaterial( type );
		if ( invType == null ) {
			return;
		}
		
		Player player = event.getPlayer();
		if ( player.isSneaking() || ( plugin.isPrivateByDefault() ^ plugin.isPrivate( player.getUniqueId() ) ) ) {
			return;
		}
		
		event.setCancelled( true );
		PublicCrafters.getInstance().getManager().openWorkbench( player, block.getLocation(), invType );
	}
	
	/**
	 * See {@link ItemDisplay} for registering and unregistering entities that open a workbench on interaction.
	 * 
	 * @param event
	 * The PlayerInteractEvent
	 */
	@EventHandler( priority = EventPriority.LOWEST )
	public void onEntityInteractEvent( PlayerInteractAtEntityEvent event ) {
		Entity entity = event.getRightClicked();
		ItemDisplay display = ItemDisplay.getItemDisplay( entity.getUniqueId() );
		if ( display == null ) {
			return;
		}
		
		event.setCancelled( true );
		
		Player player = event.getPlayer();
		PlayerInteractEvent PIE = new PlayerInteractEvent( player, Action.RIGHT_CLICK_BLOCK, player.getItemInHand(), display.getCraftDisplay().getLocation().getBlock(), BlockFace.UP );
		Bukkit.getPluginManager().callEvent( PIE );
		if ( PIE.isCancelled() ) {
			return;
		}
		
		if ( player.isSneaking() || plugin.isPrivate( player.getUniqueId() ) ) {
			return;
		}
		
		PublicCrafters.getInstance().getManager().openWorkbench( player, display.getCraftDisplay().getInventory().getLocation(), InventoryType.WORKBENCH );
	}

	public static InventoryType getTypeFromMaterial( Material material ) {
		switch ( material ) {
		case WORKBENCH: return InventoryType.WORKBENCH;
//		case ANVIL: return InventoryType.ANVIL;
		default: return null;
		}
	}
}
