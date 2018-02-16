package io.github.bananapuncher714.crafters.implementation.v1_12_R1;

import org.bukkit.Location;

import net.minecraft.server.v1_12_R1.ChatMessage;
import net.minecraft.server.v1_12_R1.Container;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IInventory;
import net.minecraft.server.v1_12_R1.ITileEntityContainer;
import net.minecraft.server.v1_12_R1.InventoryCraftResult;
import net.minecraft.server.v1_12_R1.PlayerInventory;

public class CustomTileEntityContainerWorkbench implements ITileEntityContainer {
	private Location bloc;
	private ContainerManager_v1_12_R1 manager;
	
	public CustomTileEntityContainerWorkbench( ContainerManager_v1_12_R1 manager, Location blockLoc ) {
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
		
		IInventory inventory = crafting.resultInventory;
		
		InventoryCraftResult result;
		if ( inventory instanceof InventoryCraftResult || inventory == null ) {
			result = new InventoryCraftResult();
			crafting.resultInventory = result;
		} else {
			result = ( InventoryCraftResult ) inventory;
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
