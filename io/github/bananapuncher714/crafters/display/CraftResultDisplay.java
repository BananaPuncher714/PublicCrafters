package io.github.bananapuncher714.crafters.display;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.crafters.util.NBTEditor;
import io.github.bananapuncher714.crafters.util.Utils;

public class CraftResultDisplay {
	private UUID uuid;
	private UUID itemUUID;
	
	protected final ItemStack item;
	protected final Location location;
	protected final CraftDisplay parent;
	
	public CraftResultDisplay( CraftDisplay container, Location loc, ItemStack item ) {
		this.parent = container;
		this.location = loc.clone();
		this.item = item;
	}
	
	public void init() {
		ArmorStand armorstand = getModelStand( location );
		uuid = armorstand.getUniqueId();
		
		Item itemDisplay = location.getWorld().dropItem( location, item );
		itemUUID = itemDisplay.getUniqueId();
		itemDisplay.setInvulnerable( true );
		itemDisplay.setPickupDelay( Integer.MAX_VALUE );
		NBTEditor.set( itemDisplay, -32768, "Age" );
		
		armorstand.setPassenger( itemDisplay );
	}
	
	public void remove() {
		Entity display = Utils.getEntityByUUID( uuid, location.getWorld() );
		if ( display != null ) {
			display.remove();
		}
		Entity item = Utils.getEntityByUUID( itemUUID, location.getWorld() );
		if ( item != null ) {
			item.remove();
		}
	}
	
	public Location getLocation() {
		return location.clone();
	}
	
	public CraftDisplay getCraftDisplay() {
		return parent;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	/**
	 * This is mostly for internal purposes, it creates a non-marker armorstand that is invulnerable and has all interactions disabled
	 * @param location
	 * The location where to spawn the stand
	 * @return
	 * The spawned and edited armorstand
	 */
	protected static ArmorStand getModelStand( Location location ) {
		ArmorStand model = ( ArmorStand ) location.getWorld().spawnEntity( location, EntityType.ARMOR_STAND );
		model.setVisible( false );
		model.setGravity( false );
		model.setSmall( true );
		model.setMarker( true );
		NBTEditor.setEntityTag( model, 1, "DisabledSlots" );
		NBTEditor.setEntityTag( model, ( byte ) 1, "Invulnerable" );
		return model;
	}
}
