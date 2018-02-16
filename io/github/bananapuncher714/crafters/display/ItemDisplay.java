package io.github.bananapuncher714.crafters.display;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.crafters.PublicCraftersMain;
import io.github.bananapuncher714.crafters.util.NBTEditor;
import io.github.bananapuncher714.crafters.util.ReflectionUtil;
import io.github.bananapuncher714.crafters.util.Utils;

/**
 * The basic unit that is responsible for managing the items that appear on the crafting table
 * If you want to change what appears, you should extend this class and listen for the ItemDisplayCreateEvent;
 * Created on 2017-12-07
 *  
 * @author BananaPuncher714
 *
 */
public class ItemDisplay {
	private static Map< UUID, ItemDisplay > displays = new HashMap< UUID, ItemDisplay >();
	
	public static final EulerAngle BLOCK_HAND_POSE;
	public static final EulerAngle ITEM_HAND_POSE;
	
	static {
		// Determine what hand pose is fit for each version
		
		Set< String > versions = new HashSet< String >();
		versions.add( "v1_8_R3" );
		versions.add( "v1_10_R1" );
		versions.add( "v1_9_R2" );
		
		String version = ReflectionUtil.getVersion();
		if ( versions.contains( version ) ) {
			BLOCK_HAND_POSE = new EulerAngle( Math.toRadians( -43 ), Math.toRadians( -41.5 ), Math.toRadians( 19.5 ) );
			ITEM_HAND_POSE = new EulerAngle( Math.toRadians( -20 ), Math.toRadians( 0), Math.toRadians( 0 ) );
		} else {
			BLOCK_HAND_POSE = new EulerAngle( Math.toRadians( -15 ), Math.toRadians( -45 ), 0 );
			ITEM_HAND_POSE = new EulerAngle( 0, 0, 0 );
		}
	}
	
	private UUID uuid;

	protected final int slot;
	protected final ItemStack item;
	protected final Location location;
	protected final CraftDisplay parent;
	protected EulerAngle handPose;
	
	public ItemDisplay( CraftDisplay container, Location loc, ItemStack item, int slot ) {
		this.item = item;
		this.slot = slot;
		
		handPose = PublicCraftersMain.getInstance().getAngleForMaterial( item.getType() );
		if ( handPose == null ) {
			if ( item.getType().isBlock() ) {
				handPose = BLOCK_HAND_POSE;
			} else {
				handPose = ITEM_HAND_POSE;
			}
		}

		location = loc.clone();
		
		Vector vector = PublicCraftersMain.getInstance().getOffsetForMaterial( item.getType() );
		if ( vector != null ) {
			location.add( vector );
		}
		
		parent = container;
	}
	
	/**
	 * This is the important method when creating a subclass.
	 */
	public void init() {
		ArmorStand itemDisplay = getModelStand( location );
		itemDisplay.setItemInHand( item );
		
		if ( item.getType().isBlock() ) {
			itemDisplay.setRightArmPose( BLOCK_HAND_POSE );
		} else {
			itemDisplay.setRightArmPose( ITEM_HAND_POSE );
		}
		
		uuid = itemDisplay.getUniqueId();
		registerEntity( uuid );
	}

	/**
	 * This is a method you will want to override too, it is called whenever an ItemDisplay is no longer needed, and on server stop.
	 */
	public void remove() {
		Entity display = Utils.getEntityByUUID( uuid, location.getWorld() );
		if ( display != null ) {
			display.remove();
		}
		unregisterEntity( uuid );
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public CraftDisplay getCraftDisplay() {
		return parent;
	}
	
	public int getSlot() {
		return slot;
	}
	
	/**
	 * You will also want to register and unregister your armorstands or entities that you create;
	 * All this does is open the crafting table if the entity is clicked on.
	 * @param uuid
	 * The entity's UUID
	 */
	protected void registerEntity( UUID uuid ) {
		displays.put( uuid, this );
	}
	
	/**
	 * Unregister an entity from opening the crafting table by right clicking
	 * @param uuid
	 * The UUID of the entity
	 */
	protected void unregisterEntity( UUID uuid ) {
		displays.remove( uuid, this );
	}
	
	/**
	 * Get an ItemDisplay based off an entity that is right clicked
	 * 
	 * @param uuid
	 * The UUID of an entity
	 * @return
	 * The ItemDisplay that the entity is linked to
	 */
	public static ItemDisplay getItemDisplay( UUID uuid ) {
		return displays.get( uuid );
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
		model.setMarker( PublicCraftersMain.getInstance().isMarker() );
		NBTEditor.setEntityTag( model, 1, "DisabledSlots" );
		NBTEditor.setEntityTag( model, ( byte ) 1, "Invulnerable" );
		return model;
	}
}
