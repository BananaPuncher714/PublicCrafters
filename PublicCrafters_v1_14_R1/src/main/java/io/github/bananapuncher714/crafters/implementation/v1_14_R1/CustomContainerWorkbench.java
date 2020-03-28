package io.github.bananapuncher714.crafters.implementation.v1_14_R1;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_14_R1.event.CraftEventFactory;
import org.bukkit.entity.HumanEntity;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.util.ReflectionUtil;
import io.github.bananapuncher714.crafters.util.Utils;
import net.minecraft.server.v1_14_R1.ContainerRecipeBook;
import net.minecraft.server.v1_14_R1.ContainerWorkbench;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.IInventory;
import net.minecraft.server.v1_14_R1.InventoryClickType;
import net.minecraft.server.v1_14_R1.InventoryCraftResult;
import net.minecraft.server.v1_14_R1.InventoryCrafting;
import net.minecraft.server.v1_14_R1.ItemStack;
import net.minecraft.server.v1_14_R1.NonNullList;
import net.minecraft.server.v1_14_R1.PlayerInventory;
import net.minecraft.server.v1_14_R1.Slot;
import net.minecraft.server.v1_14_R1.SlotResult;
import net.minecraft.server.v1_14_R1.World;
import net.minecraft.server.v1_14_R1.AutoRecipeStackManager;
import net.minecraft.server.v1_14_R1.Container;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.IRecipe;
import net.minecraft.server.v1_14_R1.PacketPlayOutSetSlot;
import net.minecraft.server.v1_14_R1.RecipeCrafting;
import net.minecraft.server.v1_14_R1.Recipes;
import net.minecraft.server.v1_14_R1.Containers;

/**
 * @author BananaPuncher714
 */
public class CustomContainerWorkbench extends ContainerRecipeBook< InventoryCrafting > {
	protected InventoryCraftResult resultInventory;
	protected CustomInventoryCrafting craftInventory;
	protected World world;
	protected HumanEntity viewer;
	private List< Slot > theseSlots;
	
	public CustomContainerWorkbench( int id, HumanEntity player, Location blockLocation, CustomInventoryCrafting crafting, InventoryCraftResult result ) {
		super( Containers.CRAFTING, id );
		
		try {
			Field slots = this.getClass().getField( "items" );
			( ( NonNullList< ItemStack > ) slots.get( this ) ).clear();
		} catch ( Exception exception ) {
			try {
				Field c = this.getClass().getField( "b" );
				( ( NonNullList< ItemStack > ) c.get( this ) ).clear();
			} catch ( Exception anotherException ) {
				anotherException.printStackTrace();
			}
		}
		
		theseSlots = new ArrayList< Slot >();
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
	
	// Overrides 1.12
	public ItemStack b( EntityHuman entityhuman, int i ) {
		return shiftClick( entityhuman, i );
	}
	
	// Overrides 1.12.2
	public ItemStack shiftClick(EntityHuman entityhuman, int i) {
		ItemStack itemstack = ItemStack.a;
		Slot slot = ( Slot ) theseSlots.get(i);
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
				slot.d();
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
	public boolean a( ItemStack itemstack, Slot slot ) {
		return isNotResultSlot( slot );
	}
	
	@Override
	public ItemStack a( int i, int j, InventoryClickType inventoryclicktype, EntityHuman entityhuman ) {
		craftInventory.selfContainer.setContainer( this );
		return super.a( i, j, inventoryclicktype, entityhuman );
	}
	
	@Override
	public void a( IInventory iinventory ) {
		// Crafting
		a( windowId, world, ( ( CraftHumanEntity ) viewer ).getHandle(), craftInventory, resultInventory, this );
	}
	
	public boolean isNotResultSlot( Slot slot ) {
		return slot.inventory != resultInventory;
	}
	
	/**
	 * This might be for when the inventory closes?
	 */
	@Override
	public void b( EntityHuman entity ) {
		PlayerInventory playerinventory = entity.inventory;
		craftInventory.selfContainer.setContainer( this );
		if ( !playerinventory.getCarried().isEmpty() ) {
			entity.drop( playerinventory.getCarried(), false );
			playerinventory.setCarried( ItemStack.a );
		}
		// Make sure the craft inventory stops watching this container
		craftInventory.removeContainer( this );
		
		if ( craftInventory.transaction.isEmpty() && PublicCrafters.getInstance().isDropItem() ) {
			if ( !world.isClientSide ) {
				for (int i = 0; i < 9; i++ ) {
					ItemStack itemstack = craftInventory.getItem( i );
					craftInventory.setItem( i, null );
					if ( itemstack != null ) {
						entity.drop( itemstack, false );
					}
				}
			}
		}
	}
	
	// Overrides 1.12
	public boolean a( EntityHuman entity ) {
		return canUse( entity );
	}
	
	// Overrides 1.12.2
	public boolean canUse( EntityHuman entity ) {
		if ( !checkReachable ) {
			return true;
		}
		// Should make more efficient
		return craftInventory.getLocation().getBlock().getType() == Utils.getWorkbenchMaterial();
	}

	@Override
	public CraftInventoryView getBukkitView() {
		return new CraftInventoryView( viewer, new CraftInventoryCrafting( craftInventory, resultInventory ), this );
	}

	protected HumanEntity getViewer() {
		return viewer;
	}
	

	@Override
	public void a( AutoRecipeStackManager manager ) {
		craftInventory.a( manager );
	}

	@Override
	public boolean a( IRecipe< ? super InventoryCrafting > irecipe ) {
		return irecipe.a( craftInventory, ( ( CraftHumanEntity ) viewer ).getHandle().world );
	}

	@Override
	public void e() {
		craftInventory.clear();
		resultInventory.clear();
	}

	// getResultSlotIndex
	@Override
	public int f() {
		return 0;
	}

	// getGridWidth
	@Override
	public int g() {
		return craftInventory.g();
	}

	// getGridHeight
	@Override
	public int h() {
		return craftInventory.f();
	}
	
	protected static void a( int i, World world, EntityHuman entityhuman, InventoryCrafting inventorycrafting, InventoryCraftResult inventorycraftresult, Container container ) {
		if ( !world.isClientSide ) {
			EntityPlayer entityplayer = ( EntityPlayer ) entityhuman;
			ItemStack itemstack = ItemStack.a;
			Optional< RecipeCrafting > optional = world.getMinecraftServer().getCraftingManager().craft( Recipes.CRAFTING, inventorycrafting, world );

			if (optional.isPresent()) {
				RecipeCrafting recipecrafting = ( RecipeCrafting )optional.get();

				if ( inventorycraftresult.a( world, entityplayer, recipecrafting ) ) {
					itemstack = recipecrafting.a(inventorycrafting);
				}
			} 
			itemstack = CraftEventFactory.callPreCraftEvent(inventorycrafting, inventorycraftresult, itemstack, container.getBukkitView(), false);

			inventorycraftresult.setItem( 0, itemstack );
			entityplayer.playerConnection.sendPacket( new PacketPlayOutSetSlot( i, 0, itemstack ) );
		} 
	}
}
