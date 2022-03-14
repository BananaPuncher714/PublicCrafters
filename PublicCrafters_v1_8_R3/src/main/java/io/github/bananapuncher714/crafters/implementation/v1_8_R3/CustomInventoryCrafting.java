package io.github.bananapuncher714.crafters.implementation.v1_8_R3;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;

import com.google.common.collect.Sets;

import io.github.bananapuncher714.crafters.display.CraftDisplay;
import io.github.bananapuncher714.crafters.implementation.api.PublicCraftingInventory;
import io.github.bananapuncher714.crafters.implementation.v1_8_R3.ContainerManager_v1_8_R3.SelfContainer;
import net.minecraft.server.v1_8_R3.Container;
import net.minecraft.server.v1_8_R3.InventoryCraftResult;
import net.minecraft.server.v1_8_R3.InventoryCrafting;
import net.minecraft.server.v1_8_R3.ItemStack;

public class CustomInventoryCrafting extends InventoryCrafting implements PublicCraftingInventory {
	Set< Container > containers = Sets.newHashSet();
	private ItemStack[] items;
	private UUID id;
	private Location bloc;
	private CraftDisplay display;
	private ContainerManager_v1_8_R3 manager;
	protected SelfContainer selfContainer;
	
	public CustomInventoryCrafting( Location workbenchLoc, ContainerManager_v1_8_R3 manager, SelfContainer container, int i, int j ) {
		super( container, i, j );
		id = UUID.randomUUID();
		bloc = workbenchLoc;
		this.manager = manager;
		selfContainer = container;
		// We need to access the items stored in the 3 by 3 grid
		try {
			Field field = InventoryCrafting.class.getDeclaredField( "items" );
			field.setAccessible( true );
			items = ( ItemStack[] ) field.get( this );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
		display = new CraftDisplay( this );
	}

	@Override
	public void setItem( int index, ItemStack item ) {
		items[ index ] = item;
		for ( Container container : containers ) {
			container.a( this );
		}
		display.update();
	}
	
	@Override
	public ItemStack splitStack( int i, int j ) {
		if ( items[ i ] != null ) {
	      if ( items[ i ].count <= j ) {
	        ItemStack itemstack = this.items[ i ];
	        items[ i ] = null;
	        for ( Container container : containers ) {
				container.a( this );
			}
			display.update();
	        return itemstack;
	      }
	      ItemStack itemstack = items[i].cloneAndSubtract( j );
	      if ( this.items[ i ].count == 0 ) {
	        this.items[ i ] = null;
	      }
	      for ( Container container : containers ) {
				container.a( this );
			}
			display.update();
	      return itemstack;
	    }
		for ( Container container : containers ) {
			container.a( this );
		}
		display.update();
	    return null;
	}
	
	// This is to fetch a nice list of Bukkit ItemStacks from the list of NMS ItemStacks
	@Override
	public List< org.bukkit.inventory.ItemStack > getBukkitItems() {
		List< org.bukkit.inventory.ItemStack > bukkitItems = new ArrayList< org.bukkit.inventory.ItemStack >();
		for ( ItemStack item : items ) {
			bukkitItems.add( CraftItemStack.asBukkitCopy( item ) );
		}
		return bukkitItems;
	}

	@Override
	public org.bukkit.inventory.ItemStack getResult() {
		if ( this.resultInventory != null ) {
			return CraftItemStack.asBukkitCopy( resultInventory.getItem( 0 ) );
		}
		return null;
	}
	
	protected void setItems( List< org.bukkit.inventory.ItemStack > items ) {
		int index = 0;
		for ( org.bukkit.inventory.ItemStack item : items ) {
			this.items[ index++ ] = CraftItemStack.asNMSCopy( item );
		}
		
		// Want to update the result without having to use a real player
		if ( this.resultInventory instanceof InventoryCraftResult ) {
			CustomContainerWorkbench container = new CustomContainerWorkbench( manager.mockPlayer.getBukkitEntity(), bloc, this, ( InventoryCraftResult ) resultInventory );
				
			container.a( this );
			
			CraftingInventory crafting = ( CraftingInventory ) container.getBukkitView().getTopInventory();
			Bukkit.getPluginManager().callEvent( new PrepareItemCraftEvent( crafting, container.getBukkitView(), false ) );
		}
		
		display.forceUpdate();
	}
	
	// Add another viewer
	protected void addContainer( Container container ) {
		containers.add( container );
	}
	
	// Remove a container that stopped viewing it
	protected void removeContainer( Container container ) {
		containers.remove( container );
	}
	
	protected void setLocation( Location newLoc ) {
		bloc = newLoc;
	}
	
	@Override
	public UUID getUUID() {
		return id;
	}
	
	@Override
	public Location getLocation() {
		return bloc;
	}
	
	@Override
	public CraftDisplay getCraftDisplay() {
		return display;
	}
	
	@Override
	public PublicCraftingInventory move( Location location ) {
		display.stop();
		if ( manager.get( bloc ) == this ) {
			manager.benches.remove( bloc );
		}
		bloc = location;
		CustomInventoryCrafting whatsHere = manager.put( location, this );
		display = new CraftDisplay( this );
		display.update();
		return whatsHere;
	}
	
	@Override
	public void remove() {
		display.stop();
		for ( int i = 0; i < items.length; i++ ) {
			ItemStack item = items[ i ];
			items[ i ] = CraftItemStack.asNMSCopy( new org.bukkit.inventory.ItemStack( Material.AIR ) );
			org.bukkit.inventory.ItemStack is = CraftItemStack.asBukkitCopy( item );
			if ( is.getType() != Material.AIR ) {
				bloc.getWorld().dropItem( bloc.clone().add( .5, .9, .5 ), is );
			}
		}
	}
	
	@Override
	public void update() {
		if ( bloc.getBlock().getType() != Material.WORKBENCH ) {
			remove();
			manager.benches.remove( bloc );
		} else {
			manager.put( bloc, this );
			display.update();
		}
	}
}
