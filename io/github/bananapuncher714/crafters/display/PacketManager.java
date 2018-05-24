package io.github.bananapuncher714.crafters.display;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import io.github.bananapuncher714.crafters.util.NBTEditor;
import io.github.bananapuncher714.crafters.util.ReflectionUtil;

public class PacketManager {
	private static Map< Location, Object > entities = new HashMap< Location, Object >();

	public static void spawnAll( Player player ) {
		for ( Object object : entities.values() ) {
			respawn( player, object );
			update( player, object );
		}
	}

	public static void spawn( Location loc, Player p, EulerAngle handPose ) {
		if ( entities.containsKey( loc ) ) {
			return;
		}

		try {
			Object worldServer = ReflectionUtil.getMethod( "getWorldHandle" ).invoke( loc.getWorld() );
			Object armorStand = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "EntityArmorStand" ) ).newInstance( worldServer );

			ReflectionUtil.getMethod( "setLocation").invoke( armorStand, loc.getX() + .5, loc.getY() - .5, loc.getZ() + .5, 0f, 0f );
			ReflectionUtil.getMethod( "setMarker").invoke( armorStand, true );
			ReflectionUtil.getMethod( "setSmall").invoke( armorStand, true );
			ReflectionUtil.getMethod( "setNoGravity").invoke( armorStand, true );
			ReflectionUtil.getMethod( "setInvisible").invoke( armorStand, true );
			ReflectionUtil.getMethod( "setInvulnerable").invoke( armorStand, true );

			ArmorStand bukkitStand = ( ArmorStand ) ReflectionUtil.getMethod( "getBukkitEntity" ).invoke( armorStand );
			NBTEditor.setEntityTag( bukkitStand, 1, "DisabledSlots" );
			NBTEditor.setEntityTag( bukkitStand, ( byte ) 1, "Invulnerable" );
			bukkitStand.setRightArmPose( handPose );

			Object packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntityLiving" ) ).newInstance( armorStand );

			Object playerConnection = ReflectionUtil.getField().get( ReflectionUtil.getMethod( "getHandle" ).invoke( p ) );
			ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, packet );

			entities.put( loc, armorStand );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}

	private static void respawn( Player player, Object armorStand ) {
		try {
			Object packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntityLiving" ) ).newInstance( armorStand );

			Object playerConnection = ReflectionUtil.getField().get( ReflectionUtil.getMethod( "getHandle" ).invoke( player ) );
			ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, packet );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}

	private static void update( Player player, Object armorStand ) {
		try {
			Object packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityEquipment" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( armorStand ), ReflectionUtil.getMethod( "valueOf" ).invoke( null, "MAINHAND" ), ReflectionUtil.getMethod( "getEquipment" ).invoke( armorStand, ReflectionUtil.getMethod( "valueOf" ).invoke( null, "MAINHAND" ) ) );
			Object playerConnection = ReflectionUtil.getField().get( ReflectionUtil.getMethod( "getHandle" ).invoke( player ) );
			ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, packet );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
	
	public static void update( Location location, Player player, ItemStack item ) {
		if ( !entities.containsKey( location ) ) {
			return;
		}
		try {
			Object stand = entities.get( location );

			Object packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityEquipment" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( stand ), ReflectionUtil.getMethod( "valueOf" ).invoke( null, "MAINHAND" ), ReflectionUtil.getMethod( "asNMSCopy" ).invoke( null, item ) );
			Object playerConnection = ReflectionUtil.getField().get( ReflectionUtil.getMethod( "getHandle" ).invoke( player ) );
			ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, packet );
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

		entities.remove( location );
	}
}
