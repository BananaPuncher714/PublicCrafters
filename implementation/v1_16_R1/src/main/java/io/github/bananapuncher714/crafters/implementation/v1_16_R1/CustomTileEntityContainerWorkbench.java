package io.github.bananapuncher714.crafters.implementation.v1_16_R1;

import org.bukkit.Location;

import io.github.bananapuncher714.crafters.implementation.v1_16_R1.ContainerManager_v1_16_R1.SelfContainer;
import net.minecraft.server.v1_16_R1.Container;
import net.minecraft.server.v1_16_R1.EntityHuman;
import net.minecraft.server.v1_16_R1.IInventory;
import net.minecraft.server.v1_16_R1.ITileEntityContainer;
import net.minecraft.server.v1_16_R1.InventoryCraftResult;
import net.minecraft.server.v1_16_R1.PlayerInventory;

public class CustomTileEntityContainerWorkbench implements ITileEntityContainer {
	private Location bloc;
	private ContainerManager_v1_16_R1 manager;
	
	public CustomTileEntityContainerWorkbench( ContainerManager_v1_16_R1 manager, Location blockLoc ) {
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
