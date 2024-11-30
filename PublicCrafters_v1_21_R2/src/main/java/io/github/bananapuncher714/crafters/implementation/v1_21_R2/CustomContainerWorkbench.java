package io.github.bananapuncher714.crafters.implementation.v1_21_R2;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.util.Utils;
import net.minecraft.core.NonNullList;
import net.minecraft.recipebook.AutoRecipe;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerRecipeBook;
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
	
	protected boolean placingRecipe = false;
	
	public CustomContainerWorkbench( int id, HumanEntity player, Location blockLocation, CustomInventoryCrafting crafting, InventoryCraftResult result ) {
		super( id, ( ( CraftHumanEntity ) player ).getHandle().gi() );
		
		this.k.clear();
		
		try {
			Field slots = Container.class.getField( "n" );
			( ( NonNullList< ItemStack > ) slots.get( this ) ).clear();
			slots = Container.class.getField( "q" );
			( ( NonNullList< ItemStack > ) slots.get( this ) ).clear();
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
		
		containerAccess = ContainerAccess.a;
		viewer = player;
		craftInventory = crafting;
		resultInventory = result;
		world = ( ( CraftWorld ) blockLocation.getWorld() ).getHandle();
		
		a( q(), 124, 35 );
		d( 30, 17 );
		c( ( ( CraftHumanEntity ) player ).getHandle().gi(), 8, 84);
		
		try {
            Field titleField = Container.class.getDeclaredField( "title" );
            titleField.setAccessible( true );
            titleField.set( this, ContainerManager_v1_21_R2.WORKBENCH_TITLE );
            a( craftInventory );
            titleField.set( this, null );
        } catch ( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e ) {
            e.printStackTrace();
        }
	}
	
	protected int initialize( int i, CustomInventoryCrafting crafting, InventoryCraftResult result ) {
	    craftInventory = crafting;
	    resultInventory = result;
	    
	    return i;
	}
	
	// AbstractCraftingMenu#addResultSlot
	@Override
	protected Slot a( EntityHuman entityhuman, int i, int j ) {
	    if ( craftInventory == null ) {
	        return super.a( entityhuman, i, j );
	    }
	    return this.a( new SlotResult( entityhuman, craftInventory, resultInventory, 0, i, j ) );
	}
	
	// AbstractCraftingMenu#addCraftingGridSlots
	@Override
	protected void d( int i, int j ) {
	    if ( craftInventory == null ) {
	        super.d( i, j );
	    } else {
    	    for ( int k = 0; k < o(); k++ ) {
    	        for ( int l = 0; l < p(); l++ ) {
                    a( new Slot( craftInventory, l + k * o(), i + l * 18, j + k * 18 ) );
    	        }
    	    }
	    }
	}

	public void setInventoryCrafting( CustomInventoryCrafting crafting ) {
		craftInventory = crafting;
	}
	
	// canTakeItemForPickAll
	@Override
	public boolean a( ItemStack itemstack, Slot slot ) {
		return isNotResultSlot( slot );
	}
	
	@Override
	public void a( int i, int j, InventoryClickType inventoryclicktype, EntityHuman entityhuman ) {
		craftInventory.selfContainer.setContainer( this );
		super.a( i, j, inventoryclicktype, entityhuman );
	}
	
	// slotsChanged
	@Override
	public void a( IInventory iinventory ) {
		// Crafting
	    if ( !placingRecipe ) {
	        a( this, ( WorldServer ) world, q(), craftInventory, resultInventory, null );
	    }
	}
	
	// beginPlachingRecipe
	@Override
	public void l() {
	    placingRecipe = true;
	}
	
	// finishPlacingRecipe
	@Override
	public void a( WorldServer server, RecipeHolder< RecipeCrafting > recipeHolder ) {
	    placingRecipe = false;
	    a( this, server, q(), craftInventory, resultInventory, recipeHolder );
	}
	
	private boolean isNotResultSlot( Slot slot ) {
		return slot.c != resultInventory;
	}
	
	// removed
	@Override
	public void a( EntityHuman entity ) {
		craftInventory.selfContainer.setContainer( this );
		super.a( entity );
		// Make sure the craft inventory stops watching this container
		craftInventory.removeContainer( this );
		
		if ( PublicCrafters.getInstance().isDropItem() ) {
			a( entity, craftInventory );
			a( craftInventory );
			craftInventory.update();
		}
	}
	
	// stillValid
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
	
	// AbstractCraftingMenu#handlePlacement
	@Override
	public ContainerRecipeBook.a a( boolean flag, boolean flag1, RecipeHolder< ? > recipeholder, WorldServer worldserver, PlayerInventory playerinventory ) {
	    this.l();

	    ContainerRecipeBook.a containerrecipebook_a;
	    try {
	        List< Slot > list = this.n();
	        containerrecipebook_a = AutoRecipe.a( new AutoRecipe.a< RecipeCrafting >() {
	            @Override
	            public void a( StackedItemContents stackeditemcontents ) {
	                fillSlotsStackedContents( stackeditemcontents );
	            }

	            @Override
	            public void a() {
	                resultInventory.a();
	                craftInventory.a();
	            }

	            @Override
	            public boolean a( RecipeHolder<RecipeCrafting> recipeholder2 ) {
	                return recipeholder2.b().a( craftInventory.aC_(), q().dW() );
	            }
	        }, o(), p(), list, list, playerinventory, ( RecipeHolder< RecipeCrafting > )recipeholder, flag, flag1 );
	    } finally {
	        this.a( worldserver, ( RecipeHolder< RecipeCrafting > ) recipeholder );
	    }

	    return containerrecipebook_a;
	}
	
	protected void fillSlotsStackedContents( StackedItemContents stackeditemcontents ) {
	    craftInventory.fillStackedContents( stackeditemcontents );
	}
	
	// AbstractCraftingMenu#fillCraftSlotsStackedContents
	@Override
	public void a( StackedItemContents stackeditemcontents ) {
	    fillSlotsStackedContents( stackeditemcontents );
	}
}
