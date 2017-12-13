package io.github.bananapuncher714.crafters.implementation.v1_9_R2;


import org.bukkit.Location;

import net.minecraft.server.v1_9_R2.ChatMessage;
import net.minecraft.server.v1_9_R2.Container;
import net.minecraft.server.v1_9_R2.EntityHuman;
import net.minecraft.server.v1_9_R2.IChatBaseComponent;
import net.minecraft.server.v1_9_R2.IInventory;
import net.minecraft.server.v1_9_R2.ITileEntityContainer;
import net.minecraft.server.v1_9_R2.InventoryCraftResult;
import net.minecraft.server.v1_9_R2.PlayerInventory;

public class CustomTileEntityContainerWorkbench implements ITileEntityContainer {
	private Location bloc;
	private ContainerManager_v1_9_R2 manager;
	
	public CustomTileEntityContainerWorkbench( ContainerManager_v1_9_R2 manager, Location blockLoc ) {
		this.manager = manager;
		bloc = blockLoc;
	}
	
	public String getName() {
		return "crafting_table";
	}

	public boolean hasCustomName() {
		return false;
	}

	public IChatBaseComponent getScoreboardDisplayName() {
		return new ChatMessage( "crafting_table.name" );
	}

	/**
	 * This is an ITileEntityContainer method that returns a new container for whatever tile entity
	 */
	@Override
	public Container createContainer( PlayerInventory paramPlayerInventory, EntityHuman ent ) {		
		CustomInventoryCrafting crafting = manager.benches.get( bloc );
		if ( crafting == null ) {
			crafting = new CustomInventoryCrafting( bloc, manager, null, 3, 3 );
			manager.put( bloc, crafting );
		}
		
		IInventory result = crafting.resultInventory;
		if ( result == null ) {
			result = new InventoryCraftResult();
			crafting.resultInventory = result;
		}
		
		Container container = new CustomContainerWorkbench( ent.getBukkitEntity(), bloc, crafting, result );
		crafting.addContainer( container );
		
		return container;
	}

	/**
	 * No idea what this is for, maybe localized names?
	 */
	@Override
	public String getContainerName() {
		return "minecraft:crafting_table";
	}
}
