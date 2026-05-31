package io.github.bananapuncher714.crafters.implementation.v26_1;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.mojang.authlib.GameProfile;

import io.github.bananapuncher714.crafters.CraftInventoryLoader;
import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.implementation.api.CraftInventoryManager;
import io.github.bananapuncher714.crafters.implementation.api.PublicCraftingInventory;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;

public class ContainerManager_v26_1 implements CraftInventoryManager {
    protected static final Component WORKBENCH_TITLE = Component.translatable( "container.crafting" );
    
	protected Map< Location, CustomInventoryCrafting > benches = new HashMap< Location, CustomInventoryCrafting >(); 
	protected final ServerPlayer mockPlayer;
	
	public ContainerManager_v26_1() {
		MinecraftServer server = MinecraftServer.getServer();
		ServerLevel world = server.overworld();
		GameProfile profile = new GameProfile( new UUID( 0, 0 ), "" );
		mockPlayer = new ServerPlayer( server, world, profile, ClientInformation.createDefault() );
		
		mockPlayer.connection = new ServerGamePacketListenerImpl( server, new Connection( PacketFlow.SERVERBOUND ) {
		    @Override
		    public void setListenerForServerboundHandshake( PacketListener listener ) {};
		}, mockPlayer, CommonListenerCookie.createInitial( profile, false ) ) {
			@Override
			public boolean shouldHandleMessage( Packet< ? > packet ) {
			    return true;
			}
		};
		
		mockPlayer.getBukkitEntity().setOp( true );
	}
	
	protected CustomInventoryCrafting put( Location loc, CustomInventoryCrafting cont ) {
		CustomInventoryCrafting crafting = benches.get( loc );
		if ( cont == null ) {
			benches.remove( loc );
			return crafting;
		}
		benches.put( loc, cont );
		return crafting;
	}
	
	public Location getLocation( Inventory inventory ) {
		if ( inventory == null ) {
			return null;
		}
		if ( !( inventory instanceof CraftInventory ) ) {
			return null;
		}
		try {
			Field ic = CraftInventory.class.getDeclaredField( "inventory" );
			ic.setAccessible( true );

			Object crafting = ic.get( inventory );
			if ( crafting instanceof CustomInventoryCrafting ) {
				CustomInventoryCrafting table = ( CustomInventoryCrafting ) crafting;
				return table.getLocation();
			}
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
		return null;
	}
	
	public PublicCraftingInventory get( Location loc ) {
		return benches.containsKey( loc ) ? benches.get( loc ) : null;
	}
	
	@Override
	public void remove( Location location ) {
		if ( benches.containsKey( location ) ) {
			PublicCraftingInventory bench = benches.get( location );
			bench.remove();
			benches.remove( location );
		}
	}
	
	@Override
	public void stopAll() {
		for ( PublicCraftingInventory inventory : benches.values() ) {
			inventory.getCraftDisplay().stop();
			CraftInventoryLoader.save( PublicCrafters.getInstance().getSaveFolder(), inventory );
		}
		
		mockPlayer.getBukkitEntity().setOp( false );
	}
	
	@Override
	public void load( Location location, List< ItemStack > items ) {
		// Is 0 really ok as an id?!?
		CustomInventoryCrafting crafting = new CustomInventoryCrafting( location, this, new SelfContainer( 0 ), 3, 3 );
		ResultContainer result = new ResultContainer();
		crafting.resultInventory = result;
		
		crafting.setItems( items );
		
		benches.put( location, crafting );
	}
	
	@Override
	public boolean unload( Chunk chunk ) {
		Set< Location > locations = new HashSet< Location >();
		boolean found = false;
		for ( Location location : benches.keySet() ) {
			if ( location.getChunk().getX() == chunk.getX() && location.getChunk().getX() == chunk.getZ() || location.getWorld() == chunk.getWorld() ) {
				CustomInventoryCrafting crafting = benches.get( location );
				locations.add( location );
				crafting.getCraftDisplay().stop();
				CraftInventoryLoader.save( PublicCrafters.getInstance().getSaveFolder(), crafting );
			}
		}
		found = !locations.isEmpty();
		for ( Location location : locations ) {
			benches.remove( location );
		}
		return found;
	}

	@Override
	public void openWorkbench( Player player, Location loc, InventoryType type ) {
		SimpleMenuProvider tileEntity = new SimpleMenuProvider( new CustomTileEntityContainerWorkbench( this, loc ), WORKBENCH_TITLE );
		( ( CraftPlayer ) player ).getHandle().openMenu( tileEntity );
	}
	
	@Override
	public void animate( Player player ) {
		ServerPlayer nmsPlayer = ( ( CraftPlayer ) player ).getHandle();
		broadcastPacket( player, new ClientboundAnimatePacket( nmsPlayer, ThreadLocalRandom.current().nextInt( 2 ) == 1 ? 0 : 3 ) );
	}
	
	private void broadcastPacket( Player origin, Packet< ? > packet ) {
		Location location = origin.getLocation();
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			if ( player == origin ) {
				continue;
			}
			Location ploc = player.getLocation();
			if ( ploc.getWorld() != location.getWorld() ) {
				continue;
			}
			if ( ploc.distanceSquared( location ) > 128 ) {
				continue;
			}
			ServerPlayer nmsPlayer = ( ( CraftPlayer ) player ).getHandle();
			nmsPlayer.connection.sendPacket( packet );
		}
	}
	
	protected static class SelfContainer extends AbstractContainerMenu {
		private AbstractContainerMenu container;
		
		protected SelfContainer( int id ) {
			super( null, id );
		}
		
		protected void setContainer( AbstractContainerMenu container ) {
			this.container = container;
		}
		
		@Override
		public InventoryView getBukkitView() {
			return container == null ? null : container.getBukkitView();
		}

		// canUse
        @Override
        public boolean stillValid( net.minecraft.world.entity.player.Player entity ) {
            return container == null ? false : container.stillValid( entity );
        }

        @Override
        public net.minecraft.world.item.ItemStack quickMoveStack( net.minecraft.world.entity.player.Player arg0, int arg1 ) {
            return net.minecraft.world.item.ItemStack.EMPTY;
        }
	}
}
