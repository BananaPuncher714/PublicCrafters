package io.github.bananapuncher714.crafters.display;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.util.ReflectionUtil;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import io.github.bananapuncher714.nbteditor.NBTEditor.MinecraftVersion;

public class VirtualCraftResultDisplay extends CraftResultDisplay {
	private static Map< Location, Object > entities = new HashMap< Location, Object >();
	private static Map< Location, Object > armorstands = new HashMap< Location, Object >();
	
	public VirtualCraftResultDisplay( CraftDisplay container, Location loc, ItemStack item ) {
		super( container, loc, item );
	}
	
	@Override
	public void init() {
		for ( Player player : location.getWorld().getPlayers() ) {
			spawn( location, player, item );
		}
		if ( location.getWorld().getPlayers().isEmpty() ) {
			spawn( location, null, item );
		}
	}
	
	@Override
	public void remove() {
		for ( Player player : location.getWorld().getPlayers() ) {
			kill( location, player );
		}
		entities.remove( location );
	}
	
	public static void spawnAll( Player player ) {
		for ( Location key : entities.keySet() ) {
			if ( player.getWorld() == key.getWorld() ) {
				Object item = entities.get( key );
				Object armorstand = armorstands.get( key );
				respawn( player, item, armorstand );
			}
		}
	}

	public static void despawnAll( World world, Player player ) {
		for ( Location location : entities.keySet() ) {
			if ( location.getWorld() == world ) {
				kill( location, player );
			}
		}
	}
	
	public static void spawn( Location loc, Player p, ItemStack item ) {
		try {
			Object itemEntity;
			Object armorStand;
			if ( !entities.containsKey( loc ) ) {
				Object worldServer = ReflectionUtil.getMethod( "getWorldHandle" ).invoke( loc.getWorld() );
				itemEntity = ReflectionUtil.spawnItem( worldServer, loc, item );
				
				if ( !ReflectionUtil.getVersion().contains( "v1_8" ) ) {
					ReflectionUtil.getMethod( "setInvulnerable").invoke( itemEntity, true );
				}
				
				Item itemBukkitEntity = ( Item ) ReflectionUtil.getMethod( "getBukkitEntity" ).invoke( itemEntity );
				itemBukkitEntity.setVelocity( new Vector( 0, 0, 0 ) );
				NBTEditor.set( itemBukkitEntity, true, "NoGravity" );
				NBTEditor.set( itemBukkitEntity, true, "Invulnerable" );

				if ( PublicCrafters.getInstance().isShowResultName() ) {
					ItemMeta meta = item.getItemMeta();
					if ( meta.hasDisplayName() ) {
						// Display a custom name if it has one and is enabled in the config
						itemBukkitEntity.setCustomName( meta.getDisplayName() );
						itemBukkitEntity.setCustomNameVisible( true );
					}
				}
				
				armorStand = ReflectionUtil.constructArmorStand( worldServer );

				ReflectionUtil.getMethod( "setLocation").invoke( armorStand, loc.getX(), loc.getY(), loc.getZ(), 0f, 0f );
				ReflectionUtil.getMethod( "setSmall").invoke( armorStand, true );
				ReflectionUtil.getMethod( "setNoGravity").invoke( armorStand, NBTEditor.getMinecraftVersion() != MinecraftVersion.v1_8 );
				ReflectionUtil.getMethod( "setInvisible").invoke( armorStand, true );
				if ( NBTEditor.getMinecraftVersion() != MinecraftVersion.v1_8 ) {
					ReflectionUtil.getMethod( "setInvulnerable").invoke( armorStand, true );
					ReflectionUtil.getMethod( "setMarker").invoke( armorStand, PublicCrafters.getInstance().isMarker() );
				}
				
				ArmorStand bukkitStand = ( ArmorStand ) ReflectionUtil.getMethod( "getBukkitEntity" ).invoke( armorStand );
				NBTEditor.set( bukkitStand, 1, "DisabledSlots" );
				NBTEditor.set( bukkitStand, true, "Invulnerable" );
				NBTEditor.set( bukkitStand, true, "Marker" );
				
				bukkitStand.setPassenger( itemBukkitEntity );
			} else {
				itemEntity = entities.get( loc );
				armorStand = armorstands.get( loc );
			}

			if ( p != null ) {
	            Object packet;
	            if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_21_R1 ) ) {
	                packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntity" ) ).newInstance( itemEntity, ReflectionUtil.getEntityTrackerEntryFor( itemEntity ) );
	            } else {
	                packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntity" ) ).newInstance( itemEntity, 2 );
	            }
	            Object metadata;
	            if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_19_R2 ) ) {
	                metadata = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( itemEntity ), ReflectionUtil.getMethod( "getDataWatcherItems" ).invoke( ReflectionUtil.getMethod( "getDataWatcher" ).invoke( itemEntity ) ) );
	            } else {
	                metadata = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( itemEntity ), ReflectionUtil.getMethod( "getDataWatcher" ).invoke( itemEntity ), true );
	            }

				Object packet2;
				if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_19_R1 ) ) {
		            if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_21_R1 ) ) {
		                packet2 = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntity" ) ).newInstance( armorStand, ReflectionUtil.getEntityTrackerEntryFor( armorStand ) );
		            } else {
		                packet2 = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntity" ) ).newInstance( armorStand, 0 );
		            }			
				} else {
					packet2 = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntityLiving" ) ).newInstance( armorStand );
				}
				Object metadata2;
				if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_19_R2 ) ) {
					metadata2 = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( armorStand ), ReflectionUtil.getMethod( "getDataWatcherItems" ).invoke( ReflectionUtil.getMethod( "getDataWatcher" ).invoke( armorStand ) ) );
				} else {
					metadata2 = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( armorStand ), ReflectionUtil.getMethod( "getDataWatcher" ).invoke( armorStand ), true );
				}
				
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
			Object packet;
			if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_21_R1 ) ) {
			    packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntity" ) ).newInstance( itemEntity, ReflectionUtil.getEntityTrackerEntryFor( itemEntity ) );
			} else {
			    packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntity" ) ).newInstance( itemEntity, 2 );
			}
			Object metadata;
			if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_19_R2 ) ) {
                metadata = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( itemEntity ), ReflectionUtil.getMethod( "getDataWatcherItems" ).invoke( ReflectionUtil.getMethod( "getDataWatcher" ).invoke( itemEntity ) ) );
            } else {
                metadata = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( itemEntity ), ReflectionUtil.getMethod( "getDataWatcher" ).invoke( itemEntity ), true );
            }

			Object packet2;
			if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_19_R1 ) ) {
                if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_21_R1 ) ) {
                    packet2 = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntity" ) ).newInstance( armorStand, ReflectionUtil.getEntityTrackerEntryFor( armorStand ) );
                } else {
                    packet2 = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntity" ) ).newInstance( armorStand, 0 );
                }				
			} else {
				packet2 = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntityLiving" ) ).newInstance( armorStand );
			}
			Object metadata2;
			if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_19_R2 ) ) {
				metadata2 = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( armorStand ), ReflectionUtil.getMethod( "getDataWatcherItems" ).invoke( ReflectionUtil.getMethod( "getDataWatcher" ).invoke( armorStand ) ) );
			} else {
				metadata2 = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( armorStand ), ReflectionUtil.getMethod( "getDataWatcher" ).invoke( armorStand ), true );
			}
			
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
			Object[] packets;
			Constructor< ? > cons = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityDestroy" ) );
			if ( cons.getParameterTypes()[ 0 ] == int[].class ) {
				packets = new Object[ 1 ];
				packets[ 0 ] = cons.newInstance( new int[] { ( int ) ReflectionUtil.getMethod( "getId" ).invoke( entities.get( location ) ), ( int ) ReflectionUtil.getMethod( "getId" ).invoke( armorstands.get( location ) ) } );
			} else {
				packets = new Object[ 2 ];
				packets[ 0 ] = cons.newInstance( ReflectionUtil.getMethod( "getId" ).invoke( entities.get( location ) ) );
				packets[ 1 ] = cons.newInstance( ReflectionUtil.getMethod( "getId" ).invoke( armorstands.get( location ) ) );
			}
			
			Object playerConnection = ReflectionUtil.getField().get( ReflectionUtil.getMethod( "getHandle" ).invoke( player ) );
			for ( Object obj : packets ) {
				ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, obj );
			}
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
}
