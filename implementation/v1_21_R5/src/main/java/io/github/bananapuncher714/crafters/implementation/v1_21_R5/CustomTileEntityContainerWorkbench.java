package io.github.bananapuncher714.crafters.implementation.v1_21_R5;

import org.bukkit.Location;

import io.github.bananapuncher714.crafters.implementation.v1_21_R5.ContainerManager_v1_21_R5.SelfContainer;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ITileEntityContainer;
import net.minecraft.world.inventory.InventoryCraftResult;

public class CustomTileEntityContainerWorkbench implements ITileEntityContainer {
	private Location bloc;
	private ContainerManager_v1_21_R5 manager;
	
	public CustomTileEntityContainerWorkbench( ContainerManager_v1_21_R5 manager, Location blockLoc ) {
		this.manager = manager;
		bloc = blockLoc;
	}

	/**
	 * This is an ITileEntityContainer method that returns a new container for whatever tile entity
	 */
	@Override
	public Container createMenu( int id, PlayerInventory inv, EntityHuman ent ) {	
		CustomInventoryCrafting crafting = manager.benches.get( bloc );
		if ( crafting == null ) {
			crafting = new CustomInventoryCrafting( bloc, manager, new SelfContainer( id ), 3, 3 );
			manager.put( bloc, crafting );
		}
		
		IInventory inventory = crafting.resultInventory;
		
		InventoryCraftResult result;
		if ( inventory instanceof InventoryCraftResult || inventory == null ) {
			result = new InventoryCraftResult();
			crafting.resultInventory = result;
		} else {
			result = ( InventoryCraftResult ) inventory;
		}
		
		Container container = new CustomContainerWorkbench( id, ent.getBukkitEntity(), bloc, crafting, result );
		crafting.addContainer( container );
		
		return container;
	}
}
