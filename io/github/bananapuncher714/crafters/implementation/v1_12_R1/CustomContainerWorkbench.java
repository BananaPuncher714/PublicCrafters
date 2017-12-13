package io.github.bananapuncher714.crafters.implementation.v1_12_R1;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_12_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;

import net.minecraft.server.v1_12_R1.Container;
import net.minecraft.server.v1_12_R1.CraftingManager;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.IInventory;
import net.minecraft.server.v1_12_R1.IRecipe;
import net.minecraft.server.v1_12_R1.InventoryCraftResult;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.PacketPlayOutSetSlot;
import net.minecraft.server.v1_12_R1.Slot;
import net.minecraft.server.v1_12_R1.SlotResult;
import net.minecraft.server.v1_12_R1.World;

public class CustomContainerWorkbench extends Container {
	InventoryCraftResult resultInventory;
	CustomInventoryCrafting craftInventory;
	World world;
	HumanEntity viewer;
	
	public CustomContainerWorkbench( HumanEntity player, Location blockLocation, CustomInventoryCrafting crafting, InventoryCraftResult result ) {
		viewer = player;
		resultInventory = result;
		craftInventory = crafting;
		world = ( ( CraftWorld ) blockLocation.getWorld() ).getHandle();

		a( new SlotResult( ( ( CraftHumanEntity ) player ).getHandle(), craftInventory, resultInventory, 0, 124, 35));
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				a( new Slot( craftInventory, j + i * 3, 30 + j * 18, 17 + i * 18 ) );
			}
		}
		for ( int i = 0; i < 3; i++ ) {
			for (int j = 0; j < 9; j++) {
				a( new Slot( ( ( CraftHumanEntity ) player ).getHandle().inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 ) );
			}
		}
		for ( int i = 0; i < 9; i++ ) {
			a( new Slot( ( ( CraftHumanEntity ) player ).getHandle().inventory, i, 8 + i * 18, 142 ) );
		}
		a( craftInventory );
	}
	
	/**
	 * The shift clicking method
	 */
	@Override
	public ItemStack shiftClick(EntityHuman entityhuman, int i) {
		ItemStack itemstack = ItemStack.a;
		Slot slot = ( Slot ) slots.get(i);
		if ((slot != null) && (slot.hasItem())) {
			ItemStack itemstack1 = slot.getItem();

			itemstack = itemstack1.cloneItemStack();
			if (i == 0) {
				itemstack1.getItem().b(itemstack1, world, entityhuman);
				if (!a(itemstack1, 10, 46, true)) {
					return ItemStack.a;
				}
				slot.a(itemstack1, itemstack);
			} else if ((i >= 10) && (i < 37)) {
				if (!a(itemstack1, 37, 46, false)) {
					return ItemStack.a;
				}
			} else if ((i >= 37) && (i < 46)) {
				if (!a(itemstack1, 10, 37, false)) {
					return ItemStack.a;
				}
			} else if (!a(itemstack1, 10, 46, false)) {
				return ItemStack.a;
			}
			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.a);
			} else {
				slot.f();
			}
			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.a;
			}
			ItemStack itemstack2 = slot.a(entityhuman, itemstack1);
			if (i == 0) {
				entityhuman.drop(itemstack2, false);
			}
		}
		return itemstack;
	}
	
	public void setInventoryCrafting( CustomInventoryCrafting crafting ) {
		craftInventory = crafting;
	}

	@Override
	public void a( IInventory inventory ) {
		setCraftResult();
	}
	
	public void setCraftResult() {
		if ( !world.isClientSide ) {
			EntityPlayer entityplayer = ( EntityPlayer ) ((CraftHumanEntity)viewer).getHandle();
			ItemStack itemstack = ItemStack.a;
			IRecipe irecipe = CraftingManager.b(craftInventory, world);
			if ((irecipe != null) && ((irecipe.c()) || (!world.getGameRules().getBoolean("doLimitedCrafting")) || (entityplayer.F().b(irecipe)))) {
				resultInventory.a( irecipe );
				itemstack = irecipe.craftItem( craftInventory );
			}
			itemstack = CraftEventFactory.callPreCraftEvent(craftInventory, itemstack, getBukkitView(), false);

			resultInventory.setItem(0, itemstack);
			entityplayer.playerConnection.sendPacket(new PacketPlayOutSetSlot(this.windowId, 0, itemstack));
			for ( Object listener : listeners ) {
				EntityPlayer player = ( EntityPlayer ) listener;
				player.playerConnection.sendPacket( new PacketPlayOutSetSlot( player.activeContainer.windowId, 0, itemstack ) );
			}
		}
	}
	
	public boolean isNotResultSlot( Slot slot ) {
		return slot.inventory != resultInventory;
	}
	
	/**
	 * This might be for when the inventory closes?
	 */
	@Override
	public void b( EntityHuman entity ) {
		super.b( entity );
		// Make sure the craft inventory stops watching this container
		craftInventory.removeContainer( this );
	}
	
	@Override
	public boolean canUse( EntityHuman entity ) {
		if ( !checkReachable ) {
			return true;
		}
		return craftInventory.getLocation().getBlock().getType() == Material.WORKBENCH;
	}

	@Override
	public InventoryView getBukkitView() {
		return new CraftInventoryView( viewer, new CraftInventoryCrafting( craftInventory, resultInventory ), this );
	}

	protected HumanEntity getViewer() {
		return viewer;
	}
}
