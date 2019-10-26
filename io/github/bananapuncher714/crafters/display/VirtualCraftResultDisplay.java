package io.github.bananapuncher714.crafters.display;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.crafters.util.NBTEditor;
import io.github.bananapuncher714.crafters.util.ReflectionUtil;

public class VirtualCraftResultDisplay extends CraftResultDisplay {
	private static Map< Location, Object > entities = new HashMap< Location, Object >();
	
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
		for ( Object object : entities.values() ) {
			respawn( player, object );
		}
	}

	public static void spawn( Location loc, Player p, ItemStack item ) {
		try {
			Object itemEntity;
			if ( !entities.containsKey( loc ) ) {
				Object worldServer = ReflectionUtil.getMethod( "getWorldHandle" ).invoke( loc.getWorld() );
				itemEntity = ReflectionUtil.spawnItem( worldServer, loc, item );

				// This stuff works, but the item drifts all over the place
				// I'd need to make it ride an armorstand to keep it grounded
				// Unfortunately, the mount packet differs, and I can't be bothered to find the actual packet for 1.8
				
				if ( !ReflectionUtil.getVersion().equalsIgnoreCase( "v1_8_R3" ) ) {
					ReflectionUtil.getMethod( "setInvulnerable").invoke( itemEntity, true );
				}
				
				Item itemBukkitEntity = ( Item ) ReflectionUtil.getMethod( "getBukkitEntity" ).invoke( itemEntity );
				itemBukkitEntity.setVelocity( new Vector( 0, 0, 0 ) );
				NBTEditor.setEntityTag( itemBukkitEntity, ( byte ) 1, "NoGravity" );
				NBTEditor.setEntityTag( itemBukkitEntity, ( byte ) 1, "Invulnerable" );
			} else {
				itemEntity = entities.get( loc );
			}

			if ( p != null ) {
				Object packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntity" ) ).newInstance( itemEntity, 1 );
				Object metadata = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( itemEntity ), ReflectionUtil.getMethod( "getDataWatcher" ).invoke( itemEntity ), true );
				
				Object playerConnection = ReflectionUtil.getField().get( ReflectionUtil.getMethod( "getHandle" ).invoke( p ) );
				ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, packet );
				ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, metadata );
			}

			entities.put( loc, itemEntity );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}

	private static void respawn( Player player, Object itemEntity ) {
		try {
			Object packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntity" ) ).newInstance( itemEntity );
			Object metadata = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( itemEntity ), ReflectionUtil.getMethod( "getDataWatcher" ).invoke( itemEntity ), true );
			
			Object playerConnection = ReflectionUtil.getField().get( ReflectionUtil.getMethod( "getHandle" ).invoke( player ) );
			ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, packet );
			ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, metadata );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}

	public static void kill( Location location, Player player ) {
		if ( !entities.containsKey( location ) ) {
			return;
		}

		try {
			Object packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityDestroy" ) ).newInstance( new int[] { ( int ) ReflectionUtil.getMethod( "getId" ).invoke( entities.get( location ) ) } );
			Object playerConnection = ReflectionUtil.getField().get( ReflectionUtil.getMethod( "getHandle" ).invoke( player ) );
			ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, packet );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
}
