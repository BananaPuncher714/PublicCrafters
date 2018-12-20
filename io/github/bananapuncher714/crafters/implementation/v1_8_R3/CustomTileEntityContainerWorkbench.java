package io.github.bananapuncher714.crafters.implementation.v1_8_R3;

import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.Container;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IInventory;
import net.minecraft.server.v1_8_R3.ITileEntityContainer;
import net.minecraft.server.v1_8_R3.InventoryCraftResult;
import net.minecraft.server.v1_8_R3.PlayerInventory;

import org.bukkit.Location;

import io.github.bananapuncher714.crafters.implementation.v1_8_R3.ContainerManager_v1_8_R3.SelfContainer;

public class CustomTileEntityContainerWorkbench implements ITileEntityContainer {
	private Location bloc;
	private ContainerManager_v1_8_R3 manager;
	
	public CustomTileEntityContainerWorkbench( ContainerManager_v1_8_R3 manager, Location blockLoc ) {
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
			crafting = new CustomInventoryCrafting( bloc, manager, new SelfContainer(), 3, 3 );
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
