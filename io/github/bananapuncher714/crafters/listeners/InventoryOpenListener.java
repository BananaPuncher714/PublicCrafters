package io.github.bananapuncher714.crafters.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.display.CraftDisplay;
import io.github.bananapuncher714.crafters.display.ItemDisplay;
import io.github.bananapuncher714.crafters.implementation.API.PublicCraftingInventory;

/**
 * Handles whenever a player clicks on a crafting table or an entity that's part of a {@link PublicCraftingInventory} or {@link CraftDisplay};
 * Created on 2017-12-09
 * 
 * @author BananaPuncher714
 */
public class InventoryOpenListener implements Listener {
	private final PublicCrafters plugin;
	
	public InventoryOpenListener( PublicCrafters main ) {
		plugin = main;
	}
	
	@EventHandler( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	private void onPlayerInteractEvent( PlayerInteractEvent event ) {
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
		// Check to see if the block is an admin table, and if the play can access it
		if ( plugin.getAdminTables().contains( block.getLocation() ) && !player.hasPermission( "publiccrafters.admin" ) ) {
			return;
		}
		PublicCrafters.getInstance().getManager().openWorkbench( player, block.getLocation(), invType );
	}
	
	/**
	 * See {@link ItemDisplay} for registering and unregistering entities that open a workbench on interaction.
	 * 
	 * @param event
	 * The PlayerInteractEvent
	 */
	@EventHandler( priority = EventPriority.LOWEST )
	private void onEntityInteractEvent( PlayerInteractAtEntityEvent event ) {
		Entity entity = event.getRightClicked();
		ItemDisplay display = ItemDisplay.getItemDisplay( entity.getUniqueId() );
		if ( display == null ) {
			return;
		}
		
		event.setCancelled( true );
		
		Player player = event.getPlayer();
		PlayerInteractEvent PIE = new PlayerInteractEvent( player, Action.RIGHT_CLICK_BLOCK, player.getItemInHand(), display.getCraftDisplay().getLocation().getBlock(), BlockFace.UP );
		Bukkit.getPluginManager().callEvent( PIE );
	}
	
	@EventHandler( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	private void onInventoryUpdateListener( InventoryClickEvent event ) {
		Inventory inventory = event.getInventory();
		if ( inventory == null ) {
			return;
		}
		if ( event.getRawSlot() != event.getSlot() ) {
			return;
		}
		Location location = PublicCrafters.getInstance().getManager().getLocation( inventory );
		if ( location != null ) {
			HumanEntity human = event.getWhoClicked();
			if ( human instanceof Player ) {
				Player player = ( Player ) human;
				plugin.getManager().animate( player );
			}
		}
	}

	public static InventoryType getTypeFromMaterial( Material material ) {
		String name = material.name();
		switch ( name ) {
		case "CRAFTING_TABLE":
		case "WORKBENCH": return InventoryType.WORKBENCH;
//		case "ENCHANTMENT_TABLE": return InventoryType.ENCHANTING;
//		case ANVIL: return InventoryType.ANVIL;
		default: return null;
		}
	}
}
