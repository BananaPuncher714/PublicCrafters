package io.github.bananapuncher714.crafters.implementation.v1_19_R2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;

import com.google.common.collect.Sets;

import io.github.bananapuncher714.crafters.display.CraftDisplay;
import io.github.bananapuncher714.crafters.implementation.api.PublicCraftingInventory;
import io.github.bananapuncher714.crafters.implementation.v1_19_R2.ContainerManager_v1_19_R2.SelfContainer;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.InventoryCraftResult;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;

/**
 * The important class, this is universal and what makes crafting tables public
 * 
 * @author BananaPuncher714
 */
public class CustomInventoryCrafting extends InventoryCrafting implements PublicCraftingInventory {
	Set< Container > containers = Sets.newHashSet();
	private List< ItemStack > items;
	private UUID id;
	private Location bloc;
	private CraftDisplay display;
	private ContainerManager_v1_19_R2 manager;
	protected SelfContainer selfContainer;
	
	public CustomInventoryCrafting( Location workbenchLoc, ContainerManager_v1_19_R2 manager, SelfContainer container, int i, int j ) {
		super( container, i, j );
		id = UUID.randomUUID();
		bloc = workbenchLoc;
		selfContainer = container;
		this.manager = manager;
		setDefaults();
		display = new CraftDisplay( this );
	}
	
	private void setDefaults() {
		items = this.getContents();
	}
	
	// setItem
	@Override
	public void a( int index, ItemStack item ) {
		// Instead of updating one container, update all the containers
		// That are looking at the table, basically the viewers
		
		items.set( index, item );
		for ( Container container : containers ) {
			container.a( this );
		}
		// Update the armorstand grid
		display.update();
	}
	
	// splitStack
	@Override
	public ItemStack a( int i, int j ) {
		ItemStack itemstack = ContainerUtil.a( items, i, j );
		if ( !itemstack.b() ) {
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
		if ( this.resultInventory != null ) {
			return CraftItemStack.asBukkitCopy( resultInventory.a( 0 ) );
		}
		return null;
	}
	
	protected void setItems( List< org.bukkit.inventory.ItemStack > items ) {
		int index = 0;
		for ( org.bukkit.inventory.ItemStack item : items ) {
			this.items.set( index++, CraftItemStack.asNMSCopy( item ) );
		}
		
		// Want to update the result without having to use a real player
		if ( this.resultInventory instanceof InventoryCraftResult ) {
			CustomContainerWorkbench container = new CustomContainerWorkbench( 0, manager.mockPlayer.getBukkitEntity(), bloc, this, ( InventoryCraftResult ) resultInventory );
			
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
		for ( ItemStack item : items ) {
			org.bukkit.inventory.ItemStack is = CraftItemStack.asBukkitCopy( item );
			if ( is.getType() != Material.AIR ) {
				bloc.getWorld().dropItem( bloc.clone().add( .5, .9, .5 ), is );
			}
		}
		items.clear();
	}
	
	@Override
	public void update() {
		if ( !bloc.getBlock().getType().name().equalsIgnoreCase( "CRAFTING_TABLE" ) ) {
			remove();
			manager.benches.remove( bloc );
		} else {
			manager.put( bloc, this );
			display.update();
		}
	}
}
