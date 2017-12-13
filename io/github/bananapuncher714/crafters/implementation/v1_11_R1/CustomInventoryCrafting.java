package io.github.bananapuncher714.crafters.implementation.v1_11_R1;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;

import com.google.common.collect.Sets;

import io.github.bananapuncher714.crafters.display.CraftDisplay;
import io.github.bananapuncher714.crafters.implementation.API.PublicCraftingInventory;
import net.minecraft.server.v1_11_R1.Container;
import net.minecraft.server.v1_11_R1.ContainerUtil;
import net.minecraft.server.v1_11_R1.CraftingManager;
import net.minecraft.server.v1_11_R1.InventoryCrafting;
import net.minecraft.server.v1_11_R1.ItemStack;

public class CustomInventoryCrafting extends InventoryCrafting implements PublicCraftingInventory {
	Set< Container > containers = Sets.newHashSet();
	private List< ItemStack > items;
	private UUID id;
	private Location bloc;
	private CraftDisplay display;
	private ContainerManager_v1_11_R1 manager;
	
	public CustomInventoryCrafting( Location workbenchLoc, ContainerManager_v1_11_R1 manager, Container container, int i, int j ) {
		super( container, i, j );
		id = UUID.randomUUID();
		bloc = workbenchLoc;
		this.manager = manager;
		// We need to access the items stored in the 3 by 3 grid
		try {
			Field field = InventoryCrafting.class.getDeclaredField( "items" );
			field.setAccessible( true );
			items = ( List< ItemStack > ) field.get( this );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
		display = new CraftDisplay( this );
	}

	@Override
	public void setItem( int index, ItemStack item ) {
		// Instead of updating one container, update all the containers
		// That are looking at the table, basically the viewers
		items.set( index, item );
		for ( Container container : containers ) {
			container.a( this );
		}
		// Update the armorstand grid
		display.update();
	}
	
	@Override
	public ItemStack splitStack( int i, int j ) {
		ItemStack itemstack = ContainerUtil.a( items, i, j );
		if ( !itemstack.isEmpty() ) {
			for ( Container container : containers ) {
				container.a( this );
			}
		}
		// Update the armorstand grid
		display.update();
		return itemstack;
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
		return CraftItemStack.asBukkitCopy( resultInventory.getItem( 0 ) );
	}
	
	protected void setItems( List< org.bukkit.inventory.ItemStack > items ) {
		int index = 0;
		for ( org.bukkit.inventory.ItemStack item : items ) {
			this.items.set( index++, CraftItemStack.asNMSCopy( item ) );
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
		for ( ItemStack item : items ) {
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
