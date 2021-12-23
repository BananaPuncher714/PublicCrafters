package io.github.bananapuncher714.crafters.implementation.v1_16_R3;

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
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventory;
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
import net.minecraft.server.v1_16_R3.ChatMessage;
import net.minecraft.server.v1_16_R3.Container;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumProtocolDirection;
import net.minecraft.server.v1_16_R3.InventoryCraftResult;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.NetworkManager;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import net.minecraft.server.v1_16_R3.TileInventory;
import net.minecraft.server.v1_16_R3.WorldServer;

public class ContainerManager_v1_16_R3 implements CraftInventoryManager {
	protected Map< Location, CustomInventoryCrafting > benches = new HashMap< Location, CustomInventoryCrafting >(); 
	protected final EntityPlayer mockPlayer;
	
	public ContainerManager_v1_16_R3() {
		MinecraftServer server = MinecraftServer.getServer();
		WorldServer world = server.getWorlds().iterator().next();
		mockPlayer = new EntityPlayer( server, world, new GameProfile( new UUID( 0, 0 ), "" ), new PlayerInteractManager( world ) );
		
		mockPlayer.playerConnection = new PlayerConnection( server, new NetworkManager( EnumProtocolDirection.CLIENTBOUND ), mockPlayer ) {
			@Override
			public void sendPacket( Packet< ? > packet ) {}
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
		InventoryCraftResult result = new InventoryCraftResult();
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
		TileInventory tileEntity = new TileInventory( new CustomTileEntityContainerWorkbench( this, loc ), new ChatMessage( "container.crafting", new Object[ 0 ] ) );
		( ( CraftPlayer ) player ).getHandle().openContainer( tileEntity );
	}
	
	@Override
	public void animate( Player player ) {
		EntityPlayer NMSPlayer = ( ( CraftPlayer ) player ).getHandle();
		broadcastPacket( player, new PacketPlayOutAnimation( NMSPlayer, ThreadLocalRandom.current().nextInt( 2 ) == 1 ? 0 : 3 ) );
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
			EntityPlayer NMSPlayer = ( ( CraftPlayer ) player ).getHandle();
			NMSPlayer.playerConnection.sendPacket( packet );
		}
	}
	
	protected static class SelfContainer extends Container {
		private CustomContainerWorkbench container;
		
		protected SelfContainer( int id ) {
			super( null, id );
		}
		
		protected void setContainer( CustomContainerWorkbench container ) {
			this.container = container;
		}
		
		@Override
		public boolean canUse( EntityHuman entity ) {
			return container == null ? false : container.canUse( entity );
		}

		@Override
		public InventoryView getBukkitView() {
			return container == null ? null : container.getBukkitView();
		}
	}
}
