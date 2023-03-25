package io.github.bananapuncher714.crafters.display;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.util.ReflectionUtil;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import io.github.bananapuncher714.nbteditor.NBTEditor.MinecraftVersion;

public class VirtualItemDisplay extends ItemDisplay {
    private static Map< Location, Object > entities = new HashMap< Location, Object >();

    public VirtualItemDisplay( CraftDisplay container, Location loc, ItemStack item, int slot ) {
        super( container, loc.clone().add( -.5, .5, -.5 ), item, slot );
    }

    @Override
    public void init() {
        for ( Player player : location.getWorld().getPlayers() ) {
            spawn( location, player, handPose, item );
            update( location, player, item );
        }
        if ( location.getWorld().getPlayers().isEmpty() ) {
            spawn( location, null, handPose, item );
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
        for ( Entry< Location, Object > entry : entities.entrySet() ) {
            if ( entry.getKey().getWorld() == player.getWorld() ) {
                Object object = entry.getValue();
                respawn( player, object );
                update( player, object );
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

    public static void spawn( Location loc, Player p, EulerAngle handPose, ItemStack item ) {
        try {
            Object armorStand;
            if ( !entities.containsKey( loc ) ) {
                Object worldServer = ReflectionUtil.getMethod( "getWorldHandle" ).invoke( loc.getWorld() );
                armorStand = ReflectionUtil.constructArmorStand( worldServer );

                ReflectionUtil.getMethod( "setLocation" ).invoke( armorStand, loc.getX() + .5, loc.getY() - .5, loc.getZ() + .5, 0f, 0f );
                ReflectionUtil.getMethod( "setSmall" ).invoke( armorStand, true );
                ReflectionUtil.getMethod( "setNoGravity" ).invoke( armorStand, NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_8 ) );
                ReflectionUtil.getMethod( "setInvisible" ).invoke( armorStand, true );
                if ( NBTEditor.getMinecraftVersion() != MinecraftVersion.v1_8 ) {
                    ReflectionUtil.getMethod( "setInvulnerable" ).invoke( armorStand, true );
                    ReflectionUtil.getMethod( "setMarker" ).invoke( armorStand, PublicCrafters.getInstance().isMarker() );
                }


                ArmorStand bukkitStand = ( ArmorStand ) ReflectionUtil.getMethod( "getBukkitEntity" ).invoke( armorStand );
                bukkitStand.setItemInHand( item );
                NBTEditor.set( bukkitStand, 1, "DisabledSlots" );
                NBTEditor.set( bukkitStand, true, "Invulnerable" );
                NBTEditor.set( bukkitStand, PublicCrafters.getInstance().isMarker(), "Marker" );
                bukkitStand.setRightArmPose( handPose );

                entities.put( loc, armorStand );
            } else {
                armorStand = entities.get( loc );
            }

            if ( p != null ) {
                Object packet;
                if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_19_R1 ) ) {
                    packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntity" ) ).newInstance( armorStand, 0 );
                } else {
                    packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntityLiving" ) ).newInstance( armorStand );
                }
                Object metadataPacket;
                if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_19_R2 ) ) {
                    metadataPacket = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( armorStand ),
                            ReflectionUtil.getMethod( "getDataWatcherItems" ).invoke( ReflectionUtil.getMethod( "getDataWatcher" ).invoke( armorStand ) ) );
                } else {
                    metadataPacket = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( armorStand ),
                            ReflectionUtil.getMethod( "getDataWatcher" ).invoke( armorStand ), true );
                }

                Object playerConnection = ReflectionUtil.getField().get( ReflectionUtil.getMethod( "getHandle" ).invoke( p ) );
                ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, packet );
                ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, metadataPacket );
            }

        } catch ( Exception exception ) {
            exception.printStackTrace();
        }
    }

    private static void respawn( Player player, Object armorStand ) {
        try {
            Object packet;
            if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_19_R1 ) ) {
                packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntity" ) ).newInstance( armorStand, 0 );
            } else {
                packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutSpawnEntityLiving" ) ).newInstance( armorStand );
            }
            Object metadataPacket;
            if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_19_R2 ) ) {
                metadataPacket = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( armorStand ),
                        ReflectionUtil.getMethod( "getDataWatcherItems" ).invoke( ReflectionUtil.getMethod( "getDataWatcher" ).invoke( armorStand ) ) );
            } else {
                metadataPacket = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityMetadata" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( armorStand ),
                        ReflectionUtil.getMethod( "getDataWatcher" ).invoke( armorStand ), true );
            }

            Object playerConnection = ReflectionUtil.getField().get( ReflectionUtil.getMethod( "getHandle" ).invoke( player ) );
            ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, packet );
            ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, metadataPacket );
        } catch ( Exception exception ) {
            exception.printStackTrace();
        }
    }

    private static void update( Player player, Object armorStand ) {
        try {
            Object packet;
            if ( NBTEditor.getMinecraftVersion() == MinecraftVersion.v1_8 ) {
                packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityEquipment" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( armorStand ), 0, ReflectionUtil.getMethod( "getEquipment" ).invoke( armorStand, 0 ) );
            } else if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_16 ) ) {
                List< Object > equipment = new ArrayList< Object >();
                equipment.add( ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "Pair" ) ).newInstance( ReflectionUtil.getMethod( "valueOf" ).invoke( null, "MAINHAND" ), ReflectionUtil.getMethod( "getEquipment" ).invoke( armorStand, ReflectionUtil.getMethod( "valueOf" ).invoke( null, "MAINHAND" ) ) ) );
                packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityEquipment" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( armorStand ), equipment );
            } else {
                packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityEquipment" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( armorStand ), ReflectionUtil.getMethod( "valueOf" ).invoke( null, "MAINHAND" ), ReflectionUtil.getMethod( "getEquipment" ).invoke( armorStand, ReflectionUtil.getMethod( "valueOf" ).invoke( null, "MAINHAND" ) ) );
            }
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

            Object packet;
            if ( NBTEditor.getMinecraftVersion() == MinecraftVersion.v1_8 ) {
                packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityEquipment" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( stand ), 0, ReflectionUtil.getMethod( "getEquipment" ).invoke( stand, 0 ) );
            } else if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_16 ) ) {
                List< Object > equipment = new ArrayList< Object >();
                equipment.add( ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "Pair" ) ).newInstance( ReflectionUtil.getMethod( "valueOf" ).invoke( null, "MAINHAND" ), ReflectionUtil.getMethod( "getEquipment" ).invoke( stand, ReflectionUtil.getMethod( "valueOf" ).invoke( null, "MAINHAND" ) ) ) );
                packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityEquipment" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( stand ), equipment );
            } else {
                packet = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityEquipment" ) ).newInstance( ReflectionUtil.getMethod( "getId" ).invoke( stand ), ReflectionUtil.getMethod( "valueOf" ).invoke( null, "MAINHAND" ), ReflectionUtil.getMethod( "getEquipment" ).invoke( stand, ReflectionUtil.getMethod( "valueOf" ).invoke( null, "MAINHAND" ) ) );
            }
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
            Object packet;
            Constructor< ? > cons = ReflectionUtil.getConstructor( ReflectionUtil.getNMSClass( "PacketPlayOutEntityDestroy" ) );
            if ( cons.getParameterTypes()[ 0 ] == int[].class ) {
                packet = cons.newInstance( new int[] { ( int ) ReflectionUtil.getMethod( "getId" ).invoke( entities.get( location ) ) } );
            } else {
                packet = cons.newInstance( ReflectionUtil.getMethod( "getId" ).invoke( entities.get( location ) ) );
            }
            Object playerConnection = ReflectionUtil.getField().get( ReflectionUtil.getMethod( "getHandle" ).invoke( player ) );
            ReflectionUtil.getMethod( "sendPacket" ).invoke( playerConnection, packet );
        } catch ( Exception exception ) {
            exception.printStackTrace();
        }
    }
}
