package io.github.bananapuncher714.crafters.display;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.item.ItemEntity;

import io.github.bananapuncher714.crafters.PublicCrafters;

public class NewVirtualCraftResultDisplay extends CraftResultDisplay {
    private static Map< Location, ItemEntity > items = new HashMap< Location, ItemEntity >();
    private static Map< Location, AreaEffectCloud > bases = new HashMap< Location, AreaEffectCloud >();
    
    public NewVirtualCraftResultDisplay( CraftDisplay container, Location loc, ItemStack item ) {
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
        items.remove( location );
    }
    
    public static void spawnAll( Player player ) {
        for ( Location key : items.keySet() ) {
            if ( player.getWorld() == key.getWorld() ) {
                ItemEntity item = items.get( key );
                AreaEffectCloud base = bases.get( key );
                respawn( player, item, base );
            }
        }
    }

    public static void despawnAll( World world, Player player ) {
        for ( Location location : items.keySet() ) {
            if ( location.getWorld() == world ) {
                kill( location, player );
            }
        }
    }
    
    public static void spawn( Location loc, Player p, ItemStack item ) {
        ItemEntity itemEntity;
        AreaEffectCloud base;

        if ( !items.containsKey( loc ) ) {
            net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy( item );
            ItemEntity itemEntity = new ItemEntity( ( ( CraftWorld ) loc.getWorld() ).getHandle(), loc.getX(), loc.getY(), loc.getZ(), nmsItem );
            itemEntity.setInvulnerable( true );
            itemEntity.setNoGravity( true );
            if ( PublicCrafters.getInstance().isShowResultName() ) {
                ItemMeta meta = item.getItemMeta();
                if ( meta.hasDisplayName() ) {
                    // Display a custom name if it has one and is enabled in the config
                    itemEntity.setCustomName( nmsItem.getItemName() );
                    itemEntity.setCustomNameVisible( true );
                }
            }

            AreaEffectCloud base = new AreaEffectCloud( ( ( CraftWorld ) loc.getWorld() ).getHandle(), loc.getX(), loc.getY(), loc.getZ() );
            base.setRadiusOnUse( 0 );
            base.setRadiusPerTick( 0 );
            base.setDurationOnUse( 0 );
            base.setRadiusPerTick( 0 );
            base.setDuration( 999999 );
            base.setRadius( 0 );
            base.setInvulnerable( true );
            base.setInvisible( true );

            items.put( loc, itemEntity );
            bases.put( loc, base );
        } else {
            itemEntity = items.get( loc );
            base = bases.get( loc );
        }

        if ( p != null ) {
            respawn( p, itemEntity, base );
        }
    }

    private static void respawn( Player player, ItemEntity itemEntity, AreaEffectCloud base ) {
        ServerPlayer serverPlayer = ( ( CraftPlayer ) player ).getHandle();

        serverPlayer.connection.send( itemEntity.getAddEntityPacket( new ServerEntity( null, itemEntity, 0, false, null, null ) ) );
        itemEntity.refreshEntityData.refreshEntityData( serverPlayer );
        serverPlayer.connection.send( base.getAddEntityPacket( new ServerEntity( null, base, 0, false, null, null ) ) );
        base.refreshEntityData( serverPlayer );

        serverPlayer.connection.send( ClientboundSetPassengersPacket( base ) );
    }
    
    public static void kill( Location location, Player player ) {
        if ( !items.containsKey( location ) ) {
            return;
        }

        ItemEntity item = items.get( key );
        AreaEffectCloud base = bases.get( key );
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket( item.getId(), base.getId() );
        ( ( CraftPlayer ) player ).getHandle().connection.send( packet );
    }
}
