package io.github.bananapuncher714.crafters.display;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.util.NBTEditor;
import io.github.bananapuncher714.crafters.util.ReflectionUtil;

public class VirtualCraftResultDisplay extends CraftResultDisplay {
	private static Map< Location, Object > entities = new HashMap< Location, Object >();
	private static Map< Location, Object > armorstands = new HashMap< Location, Object >();
	
	public VirtualCraftResultDisplay( CraftDisplay container, Location loc, ItemStack item ) {
		super( container, loc, item );
	}
	
	@Override
	public void init() {
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			spawn( location, player, item );
		}
		if ( Bukkit.getOnlinePlayers().isEmpty() ) {
			spawn( location, null, item );
		}
	}
	
	@Override
	public void remove() {
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			kill( location, player );
		}
		entities.remove( location );
	}
	
	public static void spawnAll( Player player ) {
		for ( Location key : entities.keySet() ) {
			Object item = entities.get( key );
			Object armorstand = armorstands.get( key );
			respawn( player, item, armorstand );
		}
	}

	public static void spawn( Location loc, Player p, ItemStack item ) {
		try {
			Object itemEntity;
			Object armorStand;
			if ( !entities.containsKey( loc ) ) {
				Object worldServer = ReflectionUtil.getMethod( "getWorldHandle" ).invoke( loc.getWorld() );
				itemEntity = ReflectionUtil.spawnItem( worldServer, loc, item );
				
				if ( !ReflectionUtil.getVersion().equalsIgnoreCase( "v1_8_R3" ) ) {
					ReflectionUtil.getMethod( "setInvulnerable").invoke( itemEntity, true );
				}
				
				Item itemBukkitEntity = ( Item ) ReflectionUtil.getMethod( "getBukkitEntity" ).invoke( itemEntity );
				itemBukkitEntity.setVelocity( new Vector( 0, 0, 0 ) );
				NBTEditor.setEntityTag( itemBukkitEntity, ( byte ) 1, "NoGravity" );
				NBTEditor.setEntityTag( itemBukkitEntity, ( byte ) 1, "Invulnerable" );

				ItemMeta meta = item.getItemMeta();
				if ( meta.hasDisplayName() ) {
					// Make the item display its name if it has a custom one
					itemBukkitEntity.setCustomName( meta.getDisplayName() );
					itemBukkitEntity.setCustomNameVisible( true );
				}
				
				armorStand = ReflectionUtil.constructArmorStand( worldServer );

				ReflectionUtil.getMethod( "setLocation").invoke( armorStand, loc.getX(), loc.getY(), loc.getZ(), 0f, 0f );
				ReflectionUtil.getMethod( "setSmall").invoke( armorStand, true );
				ReflectionUtil.getMethod( "setNoGravity").invoke( armorStand, !ReflectionUtil.getVersion().equalsIgnoreCase( "v1_8_R3" ) );
				ReflectionUtil.getMethod( "setInvisible").invoke( armorStand, true );
				if ( !ReflectionUtil.getVersion().equalsIgnoreCase( "v1_8_R3" ) ) {
					ReflectionUtil.getMethod( "setInvulnerable").invoke( armorStand, true );
					ReflectionUtil.getMethod( "setMarker").invoke( armorStand, PublicCrafters.getInstance().isMarker() );
				}
				
				ArmorStand bukkitStand = ( ArmorStand ) ReflectionUtil.getMethod( "getBukkitEntity" ).invoke( armorStand );
				NBTEditor.setEntityTag( bukkitStand, 1, "DisabledSlots" );
				NBTEditor.setEntityTag( bukkitStand, ( byte ) 1, "Invulnerable" );
				NBTEditor.setEntityTag( bukkitStand, ( byte ) 1, "Marker" );
				
				bukkitStand.setPassenger( itemBukkitEntity );
			} else {
				itemEntity = entities.get( loc );
				armorStand = armorstands.get( loc );
			}

			if ( p != null ) {
				Object packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntity" ) ).newInstance( itemEntity, 2 );
				Object metadata = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( itemEntity ), ReflectionUtil.getMethod( "getDataWatcher" ).invoke( itemEntity ), true );
				
				Object packet2 = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntityLiving" ) ).newInstance( armorStand );
				Object metadata2 = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( armorStand ), ReflectionUtil.getMethod( "getDataWatcher" ).invoke( armorStand ), true );
				
				Object mount = ReflectionUtil.getMountPacket( armorStand, itemEntity );
				
				Object playerConnection = ReflectionUtil.getField().get( ReflectionUtil.getMethod( "getHandle" ).invoke( p ) );
				ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, packet );
				ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, metadata );
				ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, packet2 );
				ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, metadata2 );
				ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, mount );
			}

			entities.put( loc, itemEntity );
			armorstands.put( loc, armorStand );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}

	private static void respawn( Player player, Object itemEntity, Object armorStand ) {
		try {
			Object packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntity" ) ).newInstance( itemEntity, 2 );
			Object metadata = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( itemEntity ), ReflectionUtil.getMethod( "getDataWatcher" ).invoke( itemEntity ), true );

			Object packet2 = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntityLiving" ) ).newInstance( armorStand );
			Object metadata2 = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( armorStand ), ReflectionUtil.getMethod( "getDataWatcher" ).invoke( armorStand ), true );
			
			Object mount = ReflectionUtil.getMountPacket( armorStand, itemEntity );
			
			Object playerConnection = ReflectionUtil.getField().get( ReflectionUtil.getMethod( "getHandle" ).invoke( player ) );
			ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, packet );
			ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, metadata );
			ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, packet2 );
			ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, metadata2 );
			ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, mount );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}

	public static void kill( Location location, Player player ) {
		if ( !entities.containsKey( location ) ) {
			return;
		}

		try {
			Object packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityDestroy" ) ).newInstance( new int[] { ( int ) ReflectionUtil.getMethod( "getId" ).invoke( entities.get( location ) ), ( int ) ReflectionUtil.getMethod( "getId" ).invoke( armorstands.get( location ) ) } );
			Object playerConnection = ReflectionUtil.getField().get( ReflectionUtil.getMethod( "getHandle" ).invoke( player ) );
			ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, packet );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
}
