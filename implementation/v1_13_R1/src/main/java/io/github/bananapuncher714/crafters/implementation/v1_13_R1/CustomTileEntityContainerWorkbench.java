package io.github.bananapuncher714.crafters.implementation.v1_13_R1;

import org.bukkit.Location;

import io.github.bananapuncher714.crafters.implementation.v1_13_R1.ContainerManager_v1_13_R1.SelfContainer;
import net.minecraft.server.v1_13_R1.Blocks;
import net.minecraft.server.v1_13_R1.ChatMessage;
import net.minecraft.server.v1_13_R1.Container;
import net.minecraft.server.v1_13_R1.EntityHuman;
import net.minecraft.server.v1_13_R1.IChatBaseComponent;
import net.minecraft.server.v1_13_R1.IInventory;
import net.minecraft.server.v1_13_R1.ITileEntityContainer;
import net.minecraft.server.v1_13_R1.InventoryCraftResult;
import net.minecraft.server.v1_13_R1.PlayerInventory;

public class CustomTileEntityContainerWorkbench implements ITileEntityContainer {
	private Location bloc;
	private ContainerManager_v1_13_R1 manager;
	
	public CustomTileEntityContainerWorkbench( ContainerManager_v1_13_R1 manager, Location blockLoc ) {
		this.manager = manager;
		bloc = blockLoc;
	}

	@Override
	public boolean hasCustomName() {
		return false;
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

	@Override
	public IChatBaseComponent getCustomName() {
		return null;
	}

	@Override
	public IChatBaseComponent getDisplayName() {
		return new ChatMessage( Blocks.CRAFTING_TABLE.m() + ".name", new Object[0] );
	}
}
