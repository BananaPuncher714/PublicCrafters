package io.github.bananapuncher714.crafters.implementation.v1_21_R1;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.util.Utils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.AutoRecipeStackManager;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerWorkbench;
import net.minecraft.world.inventory.InventoryClickType;
import net.minecraft.world.inventory.InventoryCraftResult;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SlotResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeCrafting;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.World;

/**
 * @author BananaPuncher714
 */
public class CustomContainerWorkbench extends ContainerWorkbench {
	protected InventoryCraftResult resultInventory;
	protected CustomInventoryCrafting craftInventory;
	protected ContainerAccess containerAccess;
	protected World world;
	protected HumanEntity viewer;
	private List< Slot > theseSlots;
	
	public CustomContainerWorkbench( int id, HumanEntity player, Location blockLocation, CustomInventoryCrafting crafting, InventoryCraftResult result ) {
		super( id, ( ( CraftHumanEntity ) player ).getHandle().fY() );
		
		try {
			Field slots = this.getClass().getField( "i" );
			theseSlots = ( List< Slot > ) slots.get( this );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
		theseSlots.clear();
		
		try {
			Field slots = Container.class.getField( "l" );
			( ( NonNullList< ItemStack > ) slots.get( this ) ).clear();
			slots = Container.class.getField( "o" );
			( ( NonNullList< ItemStack > ) slots.get( this ) ).clear();
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
		
		containerAccess = ContainerAccess.a;
		viewer = player;
		resultInventory = result;
		craftInventory = crafting;
		
		world = ( ( CraftWorld ) blockLocation.getWorld() ).getHandle();

		a( new SlotResult( ( ( CraftHumanEntity ) player ).getHandle(), craftInventory, resultInventory, 0, 124, 35 ) );
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				a( new Slot( craftInventory, j + i * 3, 30 + j * 18, 17 + i * 18 ) );
			}
		}
		for ( int i = 0; i < 3; i++ ) {
			for (int j = 0; j < 9; j++) {
				a( new Slot( ( ( CraftHumanEntity ) player ).getHandle().fY(), j + i * 9 + 9, 8 + j * 18, 84 + i * 18 ) );
			}
		}
		for ( int i = 0; i < 9; i++ ) {
			a( new Slot( ( ( CraftHumanEntity ) player ).getHandle().fY(), i, 8 + i * 18, 142 ) );
		}
		
		try {
            Field titleField = Container.class.getDeclaredField( "title" );
            titleField.setAccessible( true );
            titleField.set( this, ContainerManager_v1_21_R1.WORKBENCH_TITLE );
            a( craftInventory );
            titleField.set( this, null );
        } catch ( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e ) {
            e.printStackTrace();
        }
	}
	
	public void setInventoryCrafting( CustomInventoryCrafting crafting ) {
		craftInventory = crafting;
	}
	
	@Override
	public boolean a( ItemStack itemstack, Slot slot ) {
		return isNotResultSlot( slot );
	}
	
	@Override
	public void a( int i, int j, InventoryClickType inventoryclicktype, EntityHuman entityhuman ) {
		craftInventory.selfContainer.setContainer( this );
		super.a( i, j, inventoryclicktype, entityhuman );
	}
	
	@Override
	public void a( IInventory iinventory ) {
		// Crafting
		a( this, world, ( ( CraftHumanEntity ) viewer ).getHandle(), craftInventory, resultInventory, null );
	}
	
	@Override
	public void l() {
		resultInventory.a();
		craftInventory.a();
	}
	
	public boolean isNotResultSlot( Slot slot ) {
		return slot.c != resultInventory;
	}
	
	/**
	 * This might be for when the inventory closes?
	 */
	@Override
	public void a( EntityHuman entity ) {
		craftInventory.selfContainer.setContainer( this );
		super.a( entity );
		// Make sure the craft inventory stops watching this container
		craftInventory.removeContainer( this );
		
		if ( PublicCrafters.getInstance().isDropItem() ) {
			a( entity, craftInventory );
			l();
			a( craftInventory );
			craftInventory.update();
		}
	}
	
	@Override
	public boolean b( EntityHuman entity ) {
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
	public boolean a( RecipeHolder< RecipeCrafting > holder ) {
		return holder.b().a( craftInventory.aE_(), ( ( CraftHumanEntity ) viewer ).getHandle().dO() );
	}

	// getGridWidth
	@Override
	public int n() {
		return craftInventory.f();
	}

	// getGridHeight
	@Override
	public int o() {
		return craftInventory.g();
	}
}
