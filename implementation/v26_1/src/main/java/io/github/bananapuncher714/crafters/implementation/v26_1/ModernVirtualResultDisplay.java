package io.github.bananapuncher714.crafters.implementation.v26_1;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.display.AbstractItemDisplay;
import io.github.bananapuncher714.crafters.display.CraftDisplay;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

public class ModernVirtualResultDisplay extends AbstractItemDisplay {
    private static Set< ModernVirtualResultDisplay > displays = new HashSet< ModernVirtualResultDisplay >();
    
    private double height;
    private ItemEntity itemEntity;
    private Entity baseEntity;
    
    public ModernVirtualResultDisplay( CraftDisplay container, ItemStack item, double height ) {
        super( container, item, 10 );
        
        this.height = height;
    }
    
    @Override
    public void init() {
        Location location = getCraftDisplay().getLocation();
        for ( Player player : location.getWorld().getPlayers() ) {
            spawn( player );
        }
        if ( location.getWorld().getPlayers().isEmpty() ) {
            spawn( null );
        }
        
        displays.add( this );
    }
    
    @Override
    public void remove() {
        Location location = getCraftDisplay().getLocation();
        for ( Player player : location.getWorld().getPlayers() ) {
            kill( player );
        }
        
        itemEntity = null;
        baseEntity = null;
        
        displays.remove( this );
    }
    
    public static void spawnAll( Player player ) {
        for ( ModernVirtualResultDisplay display : displays ) {
            if ( player.getWorld() == display.getCraftDisplay().getLocation().getWorld() ) {
                display.respawn( player );
            }
        }
    }

    public static void despawnAll( World world, Player player ) {
        for ( ModernVirtualResultDisplay display : displays ) {
            if ( world == display.getCraftDisplay().getLocation().getWorld() ) {
                display.kill( player );
            }
        }
    }
    
    private void spawn( Player p ) {
        Location location = getCraftDisplay().getLocation();
        if ( itemEntity == null ) {
            net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy( item );
            itemEntity = new ItemEntity( ( ( CraftWorld ) location.getWorld() ).getHandle(), location.getX() + 0.5, location.getY(), location.getZ() + 0.5, nmsItem );
            itemEntity.setNoGravity( true );
            ReflectionUtil.setInvulnerable( itemEntity, true );
            if ( PublicCrafters.getInstance().isShowResultName() ) {
                ItemMeta meta = item.getItemMeta();
                if ( meta.hasDisplayName() ) {
                    // Display a custom name if it has one and is enabled in the config
                    itemEntity.setCustomName( nmsItem.getItemName() );
                    itemEntity.setCustomNameVisible( true );
                }
            }

            // Summon the area cloud 0.375 less so that the item appears 1/4 above the crafting table
            AreaEffectCloud cloud = new AreaEffectCloud( ( ( CraftWorld ) location.getWorld() ).getHandle(), location.getX() + .5, 1 + height + location.getY() - 0.4375, location.getZ() + .5 );
            cloud.setRadius( 0 );
            cloud.setRadiusOnUse( 0 );
            cloud.setRadiusPerTick( 0 );
            cloud.setDuration( -1 );
            cloud.setDurationOnUse( 0 );
            cloud.setInvisible( true );
            cloud.setNoGravity( true );
            ReflectionUtil.setInvulnerable( cloud, true );
            baseEntity = cloud;
            
            itemEntity.startRiding( baseEntity );
        }

        if ( p != null ) {
            respawn( p );
        }
    }

    protected void respawn( Player player ) {
        ServerPlayer serverPlayer = ( ( CraftPlayer ) player ).getHandle();

        serverPlayer.connection.send( itemEntity.getAddEntityPacket( ReflectionUtil.getServerEntityFor( itemEntity ) ) );
        itemEntity.refreshEntityData( serverPlayer );
        serverPlayer.connection.send( baseEntity.getAddEntityPacket( ReflectionUtil.getServerEntityFor( baseEntity ) ) );
        baseEntity.refreshEntityData( serverPlayer );

        serverPlayer.connection.send( new ClientboundSetPassengersPacket( baseEntity ) );
    }
    
    protected void kill( Player player ) {
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket( itemEntity.getId(), baseEntity.getId() );
        ( ( CraftPlayer ) player ).getHandle().connection.send( packet );
    }
}
