package io.github.bananapuncher714.crafters.implementation.v1_12_R1;

import org.bukkit.Location;

import net.minecraft.server.v1_12_R1.Container;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.IInventory;
import net.minecraft.server.v1_12_R1.PlayerInventory;
import net.minecraft.server.v1_12_R1.TileEntityEnchantTable;

public class CustomTileEntityContainerEnchantTable extends TileEntityEnchantTable {
	private Location bloc;
	private ContainerManager_v1_12_R1 manager;
	
	public CustomTileEntityContainerEnchantTable( ContainerManager_v1_12_R1 manager, Location blockLoc ) {
		this.manager = manager;
		bloc = blockLoc;
	}
	
	@Override
	public Container createContainer( PlayerInventory paramPlayerInventory, EntityHuman ent ) {		
		IInventory crafting = manager.tables.get( bloc );
		
		CustomContainerEnchantTable container = new CustomContainerEnchantTable( ent.getBukkitEntity(), bloc, crafting );
		if ( crafting == null ) {
			manager.tables.put( bloc, container.enchantSlots );
		}
			
		return container;
	}

}
