package io.github.bananapuncher714.crafters.implementation.v1_11_R1;

import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryType;

import io.github.bananapuncher714.crafters.implementation.v1_11_R1.ContainerManager_v1_11_R1.SelfContainer;
import net.minecraft.server.v1_11_R1.Blocks;
import net.minecraft.server.v1_11_R1.ChatMessage;
import net.minecraft.server.v1_11_R1.Container;
import net.minecraft.server.v1_11_R1.EntityHuman;
import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.IInventory;
import net.minecraft.server.v1_11_R1.ITileEntityContainer;
import net.minecraft.server.v1_11_R1.InventoryCraftResult;
import net.minecraft.server.v1_11_R1.PlayerInventory;

public class CustomTileEntityContainerSelector implements ITileEntityContainer {
	private Location bloc;
	private InventoryType type;
	private ContainerManager_v1_11_R1 manager;

	public CustomTileEntityContainerSelector( ContainerManager_v1_11_R1 manager, Location blockLoc, InventoryType type ) {
		this.manager = manager;
		this.type = type;
		bloc = blockLoc;
	}

	public String getName() {
		if ( type == InventoryType.WORKBENCH ) {
			return "crafting_table";
		} else if ( type == InventoryType.ANVIL ) {
			return "anvil";
		} else {
			return "banana";
		}
	}

	public boolean hasCustomName() {
		return false;
	}

	public IChatBaseComponent getScoreboardDisplayName() {
		if ( type == InventoryType.WORKBENCH ) {
			return new ChatMessage( "crafting_table.name" );
		} else if ( type == InventoryType.ANVIL ) {
			return new ChatMessage( Blocks.ANVIL.a() + ".name" );
		} else {
			return new ChatMessage( "banana" );
		}
	}

	/**
	 * This is an ITileEntityContainer method that returns a new container for whatever tile entity
	 */
	@Override
	public Container createContainer( PlayerInventory paramPlayerInventory, EntityHuman ent ) {		
		if ( type == InventoryType.WORKBENCH ) {
			return getWorkbenchContainer( ent );
		} else if ( type == InventoryType.ANVIL ) {
			return getAnvilContainer( paramPlayerInventory, ent );
		} else {
			return null;
		}
	}

	/**
	 * No idea what this is for, maybe localized names?
	 */
	@Override
	public String getContainerName() {
		if ( type == InventoryType.WORKBENCH ) {
			return "minecraft:crafting_table";
		} else if ( type == InventoryType.ANVIL ) {
			return "minecraft:anvil";
		} else {
			return "custom:banana";
		}
	}
	
	protected Container getWorkbenchContainer( EntityHuman ent ) {
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
	
	protected Container getAnvilContainer( PlayerInventory inventory, EntityHuman human ) {
		CustomAnvilSubcontainer crafting = manager.anvils.get( bloc );
		if ( crafting == null ) {
			crafting = new CustomAnvilSubcontainer( "Repair", true, 2 );
			manager.anvils.put( bloc, crafting );
		}
		InventoryCraftResult result = crafting.result;
		if ( result == null ) {
			result = new InventoryCraftResult();
			crafting.result = result;
		}
		Container anvil = new CustomContainerAnvil( human.getBukkitEntity(), bloc, crafting, result );
		return anvil;
	}
}
