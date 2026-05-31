package io.github.bananapuncher714.crafters.implementation.v26_1;

import org.bukkit.Location;

import io.github.bananapuncher714.crafters.implementation.v26_1.ContainerManager_v26_1.SelfContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.ResultContainer;

public class CustomTileEntityContainerWorkbench implements MenuConstructor {
	private Location bloc;
	private ContainerManager_v26_1 manager;
	
	public CustomTileEntityContainerWorkbench( ContainerManager_v26_1 manager, Location blockLoc ) {
		this.manager = manager;
		bloc = blockLoc;
	}

	/**
	 * This is an ITileEntityContainer method that returns a new container for whatever tile entity
	 */
	@Override
	public AbstractContainerMenu createMenu( int id, Inventory inv, Player ent ) {	
		CustomInventoryCrafting crafting = manager.benches.get( bloc );
		if ( crafting == null ) {
			crafting = new CustomInventoryCrafting( bloc, manager, new SelfContainer( id ), 3, 3 );
			manager.put( bloc, crafting );
		}
		
		Container inventory = crafting.resultInventory;
		
		ResultContainer result;
		if ( inventory instanceof ResultContainer || inventory == null ) {
			result = new ResultContainer();
			crafting.resultInventory = result;
		} else {
			result = ( ResultContainer ) inventory;
		}
		
		AbstractContainerMenu container = new CustomContainerWorkbench( id, ent.getBukkitEntity(), bloc, crafting, result );
		crafting.addContainer( container );
		
		return container;
	}
}
