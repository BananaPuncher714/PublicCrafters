package io.github.bananapuncher714.crafters.implementation.v1_9_R2;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;

import io.github.bananapuncher714.crafters.PublicCrafters;
import net.minecraft.server.v1_9_R2.Container;
import net.minecraft.server.v1_9_R2.CraftingManager;
import net.minecraft.server.v1_9_R2.EntityHuman;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.IInventory;
import net.minecraft.server.v1_9_R2.InventoryClickType;
import net.minecraft.server.v1_9_R2.ItemStack;
import net.minecraft.server.v1_9_R2.Items;
import net.minecraft.server.v1_9_R2.PacketPlayOutSetSlot;
import net.minecraft.server.v1_9_R2.Slot;
import net.minecraft.server.v1_9_R2.SlotResult;
import net.minecraft.server.v1_9_R2.World;

public class CustomContainerWorkbench extends Container {
	IInventory resultInventory;
	CustomInventoryCrafting craftInventory;
	World world;
	HumanEntity viewer;
	
	public CustomContainerWorkbench( HumanEntity player, Location blockLocation, CustomInventoryCrafting crafting, IInventory result ) {
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
	public ItemStack b(EntityHuman entityhuman, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot)this.c.get(i);
		if ((slot != null) && (slot.hasItem())) {
			ItemStack itemstack1 = slot.getItem();

			itemstack = itemstack1.cloneItemStack();
			if (i == 0) {
				itemstack1.getItem().b(itemstack1, world, entityhuman);
				if (!a(itemstack1, 10, 46, true)) {
					return null;
				}
				slot.a(itemstack1, itemstack);
			} else if ((i >= 10) && (i < 37)) {
				if (!a(itemstack1, 37, 46, false)) {
					return null;
				}
			} else if ((i >= 37) && (i < 46)) {
				if (!a(itemstack1, 10, 37, false)) {
					return null;
				}
			} else if (!a(itemstack1, 10, 46, false)) {
				return null;
			}
			slot.f();
			if ( itemstack1.count == itemstack.count ) {
				return null;
			}
			slot.a( entityhuman, itemstack1 );
			if (i == 0) {
				entityhuman.drop(itemstack1, false);
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
	
	@Override
	public ItemStack a( int i, int j, InventoryClickType inventoryclicktype, EntityHuman entityhuman ) {
		craftInventory.selfContainer.setContainer( this );
		return super.a( i, j, inventoryclicktype, entityhuman );
	}
	
	public void setCraftResult() {
	    CraftingManager.getInstance().lastCraftView = getBukkitView();
	    ItemStack craftResult = CraftingManager.getInstance().craft( craftInventory, world );
	    resultInventory.setItem( 0, craftResult );
	    if ( listeners.isEmpty() ) {
	    	return;
	    }
	    if ( ( craftResult != null ) && ( craftResult.getItem() == Items.FILLED_MAP ) ) {
	    	return;
	    }
	    for ( Object listener : listeners ) {
	    	EntityPlayer player = ( EntityPlayer ) listener;
	    	player.playerConnection.sendPacket( new PacketPlayOutSetSlot( player.activeContainer.windowId, 0, craftResult ) );
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
		
		if ( craftInventory.transaction.isEmpty() && PublicCrafters.getInstance().isDropItem() ) {
			if ( !world.isClientSide ) {
				for (int i = 0; i < 9; i++ ) {
					ItemStack itemstack = craftInventory.splitWithoutUpdate( i );
					if ( itemstack != null ) {
						entity.drop( itemstack, false );
					}
				}
				
				setCraftResult();
				craftInventory.update();
			}
		}
	}
	
	@Override
	@Deprecated
	public boolean a( EntityHuman entity ) {
		return canReach( entity.getBukkitEntity() );
	}
	
	public boolean canReach( HumanEntity entity ) {
		if ( !checkReachable ) {
			return true;
		}
		return craftInventory.getLocation().getBlock().getType() == Material.WORKBENCH;
	}
	
	@Override
	@Deprecated
	public InventoryView getBukkitView() {
		return new CraftInventoryView( viewer, new CraftInventoryCrafting( craftInventory, resultInventory ), this );
	}

	protected HumanEntity getViewer() {
		return viewer;
	}
}
