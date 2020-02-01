package io.github.bananapuncher714.crafters.implementation.v1_11_R1;

import net.minecraft.server.v1_11_R1.InventoryCraftResult;
import net.minecraft.server.v1_11_R1.InventorySubcontainer;

public class CustomAnvilSubcontainer extends InventorySubcontainer {
	InventoryCraftResult result;
	
	public CustomAnvilSubcontainer( String customName, boolean hasCustomName, int amountOfSlots ) {
		super( customName, hasCustomName, amountOfSlots );
	}
}
