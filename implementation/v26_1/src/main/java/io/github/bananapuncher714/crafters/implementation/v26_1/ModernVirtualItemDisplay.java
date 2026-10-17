package io.github.bananapuncher714.crafters.implementation.v26_1;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftMetaBlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;

import com.mojang.math.Transformation;

import io.github.bananapuncher714.crafters.display.AbstractItemDisplay;
import io.github.bananapuncher714.crafters.display.CraftDisplay;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;

public class ModernVirtualItemDisplay extends AbstractItemDisplay {
    private static Set< ModernVirtualItemDisplay > displays = new HashSet< ModernVirtualItemDisplay >();
    
    protected Entity displayEntity = null;
    protected Location location;
    
    public ModernVirtualItemDisplay( CraftDisplay container, ItemStack item, double height, int slot ) {
        super( container, item, slot );
        
        int row = slot / 3;
        int col = slot % 3;
        location = container.getLocation().clone().add( 0.3125 + ( col * 0.1875 ), 0.4375 + height, 0.3125 + ( row * 0.1875 ) );
    }

    @Override
    public void init() {
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
        for ( Player player : location.getWorld().getPlayers() ) {
            kill( player, displayEntity );
        }
        
        displayEntity = null;
        
        displays.remove( this );
    }

    public static void spawnAll( Player player ) {
        for ( ModernVirtualItemDisplay display : displays ) {
            if ( display.getCraftDisplay().getLocation().getWorld() == player.getWorld() ) {
                respawn( player, display.displayEntity );
            }
        }
    }

    public static void despawnAll( World world, Player player ) {
        for ( ModernVirtualItemDisplay display : displays ) {
            if ( display.getCraftDisplay().getLocation().getWorld() == player.getWorld() ) {
                kill( player, display.displayEntity );
            }
        }
    }

    private void spawn( Player player ) {
        if ( displayEntity == null ) {
            
            Vector diff = new Vector( location.getBlockX() + 0.5, location.getY(), location.getBlockZ() + 0.5 ).subtract( location.toVector() );
            
            Matrix4f matrix = new Matrix4f();
            matrix.translate( ( float ) diff.getX(), 0, ( float ) diff.getZ() );
            switch ( getCraftDisplay().getInventory().getFace() ) {
            case EAST:
                matrix.rotate( ( float ) Math.PI / 2, 0, 1, 0 );
                break;
            case NORTH:
                matrix.rotate( ( float ) Math.PI, 0, 1, 0 );
                break;
            case WEST:
                matrix.rotate( ( float ) Math.PI * 1.5f, 0, 1, 0 );
                break;
            default:
                break;
            }
            matrix.translate( ( float ) -diff.getX(), 0, ( float ) -diff.getZ() );
            matrix.scale( 0.1875f );
            
            Display disp;
            ItemMeta meta = item.getItemMeta();
            if ( item.getType().isBlock() ) {
                net.minecraft.world.entity.Display.BlockDisplay blockDisp = new net.minecraft.world.entity.Display.BlockDisplay( BuiltInRegistries.ENTITY_TYPE.get( Identifier.withDefaultNamespace( "block_display" ) ).get().value(), ( ( CraftWorld ) location.getWorld() ).getHandle() );
                if ( meta instanceof CraftMetaBlockState ) {
                    BlockState state = ( ( CraftMetaBlockState ) meta ).getBlockState();
                    blockDisp.setBlockState( ( ( CraftBlockState ) state ).getHandle() );
                } else {
                    blockDisp.setBlockState( CraftBlockType.bukkitToMinecraft( item.getType() ).defaultBlockState() );
                }
                disp = blockDisp;

                // Block displays start from the bottom left rather than the center
                matrix.rotate( ( float ) Math.PI, 0, 1, 0 );
                matrix.translate( -0.5f, -0.5f, -0.5f );
            } else {
                net.minecraft.world.entity.Display.ItemDisplay itemDisp = new net.minecraft.world.entity.Display.ItemDisplay( BuiltInRegistries.ENTITY_TYPE.get( Identifier.withDefaultNamespace( "item_display" ) ).get().value(), ( ( CraftWorld ) location.getWorld() ).getHandle() );
                itemDisp.setItemStack( CraftItemStack.asNMSCopy( item ) );
                disp = itemDisp;
                
                // Flip it
                matrix.rotate( ( float ) Math.PI, 0, 0, 1 );
                // Rotate the item so it's facing upwards
                matrix.rotate( ( float ) Math.toRadians( -90 ), 1, 0, 0 );
            }
            
            disp.setPos( location.getX(), location.getY(), location.getZ() );
            disp.setBrightnessOverride( Brightness.FULL_BRIGHT );
            
            com.mojang.math.Transformation transformation = new com.mojang.math.Transformation( matrix );
            disp.setTransformation( new Transformation( transformation.translation(), transformation.leftRotation(), transformation.scale(), transformation.rightRotation() ) );
            displayEntity = disp;
        }
        
        if ( player != null ) {
            respawn( player, displayEntity );
        }
    }
    
    private static void respawn( Player player, Entity item ) {
        ServerPlayer serverPlayer = ( ( CraftPlayer ) player ).getHandle();

        serverPlayer.connection.send( item.getAddEntityPacket( ReflectionUtil.getServerEntityFor( item ) ) );
        item.refreshEntityData( serverPlayer );
    }

    public static void kill( Player player, Entity item ) {
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket( item.getId() );
        ( ( CraftPlayer ) player ).getHandle().connection.send( packet );
    }
}
