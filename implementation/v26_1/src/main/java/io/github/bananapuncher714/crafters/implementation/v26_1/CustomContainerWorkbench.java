package io.github.bananapuncher714.crafters.implementation.v26_1;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.util.Utils;
import net.minecraft.core.NonNullList;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

/**
 * @author BananaPuncher714
 */
public class CustomContainerWorkbench extends CraftingMenu {
	protected ResultContainer resultInventory;
	protected CustomInventoryCrafting craftInventory;
	protected ContainerLevelAccess containerAccess;
	protected Level world;
	protected HumanEntity viewer;
	
	protected boolean placingRecipe = false;
	
	public CustomContainerWorkbench( int id, HumanEntity player, Location blockLocation, CustomInventoryCrafting crafting, ResultContainer result ) {
		super( id, ( ( CraftHumanEntity ) player ).getHandle().getInventory() );

		lastSlots.clear();
		slots.clear();
		remoteSlots.clear();
		
		containerAccess = ContainerLevelAccess.NULL;
		viewer = player;
		craftInventory = crafting;
		resultInventory = result;
		world = ( ( CraftWorld ) blockLocation.getWorld() ).getHandle();
		
		addResultSlot( owner(), 124, 35 );
		addCraftingGridSlots( 30, 17 );
		addStandardInventorySlots( ( ( CraftHumanEntity ) player ).getHandle().getInventory(), 8, 84 );
		
		try {
            Field titleField = AbstractContainerMenu.class.getDeclaredField( "title" );
            titleField.setAccessible( true );
            titleField.set( this, ContainerManager_v26_1.WORKBENCH_TITLE );
            slotsChanged( craftInventory );
            titleField.set( this, null );
        } catch ( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e ) {
            e.printStackTrace();
        }
	}
	
	protected int initialize( int i, CustomInventoryCrafting crafting, ResultContainer result ) {
	    craftInventory = crafting;
	    resultInventory = result;
	    
	    return i;
	}
	
	// AbstractCraftingMenu#addResultSlot
	@Override
	protected Slot addResultSlot( Player player, int x, int y ) {
	    if ( craftInventory == null ) {
	        return super.addResultSlot( player, x, y );
	    }
	    return addSlot( new ResultSlot( player, craftInventory, resultInventory, 0, x, y ) );
	}
	
	@Override
	protected void addCraftingGridSlots( int left, int top ) {
	    if ( craftInventory == null ) {
	        super.addCraftingGridSlots( left, top );
	    } else {
    	    for ( int k = 0; k < getGridWidth(); k++ ) {
    	        for ( int l = 0; l < getGridHeight(); l++ ) {
                    addSlot( new Slot( craftInventory, l + k * getGridWidth(), left + l * 18, top + k * 18 ) );
    	        }
    	    }
	    }
	}

	public void setInventoryCrafting( CustomInventoryCrafting crafting ) {
		craftInventory = crafting;
	}
	
	@Override
	public boolean canTakeItemForPickAll( ItemStack itemstack, Slot slot ) {
		return isNotResultSlot( slot ) && super.canTakeItemForPickAll( itemstack, slot );
	}
	
	@Override
	public void clicked( int slotIndex, int buttonNum, ContainerInput inventoryclicktype, Player entityhuman ) {
		craftInventory.selfContainer.setContainer( this );
		super.clicked( slotIndex, buttonNum, inventoryclicktype, entityhuman );
	}
	
	@Override
	public void slotsChanged( Container iinventory ) {
		// Crafting
	    if ( !placingRecipe ) {
	        slotChangedCraftingGrid( this, ( ServerLevel ) world, owner(), craftInventory, resultInventory, null );
	    }
	}
	
	@Override
	public void beginPlacingRecipe() {
	    placingRecipe = true;
	}
	
	@Override
	public void finishPlacingRecipe( ServerLevel server, RecipeHolder< CraftingRecipe > recipeHolder ) {
	    placingRecipe = false;
	    slotChangedCraftingGrid( this, server, owner(), craftInventory, resultInventory, recipeHolder );
	}
	
	private boolean isNotResultSlot( Slot slot ) {
		return slot.container != resultInventory;
	}
	
	@Override
	public void removed( Player entity ) {
		craftInventory.selfContainer.setContainer( this );
		super.removed( entity );
		// Make sure the craft inventory stops watching this container
		craftInventory.removeContainer( this );
		
		if ( PublicCrafters.getInstance().isDropItem() ) {
			clearContainer( entity, craftInventory );
			slotsChanged( craftInventory );
			craftInventory.update();
		}
	}
	
	@Override
	public boolean stillValid( Player entity ) {
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
	public RecipeBookMenu.PostPlaceAction handlePlacement( boolean useMaxItems, boolean allowDroppingItemsToClear, RecipeHolder< ? > recipeholder, ServerLevel worldserver, Inventory playerinventory ) {
	    this.beginPlacingRecipe();

	    RecipeBookMenu.PostPlaceAction postPlaceAction;
	    try {
	        List< Slot > list = getInputGridSlots();
	        postPlaceAction = ServerPlaceRecipe.placeRecipe( new ServerPlaceRecipe.CraftingMenuAccess< CraftingRecipe >() {
	            @Override
	            public void fillCraftSlotsStackedContents( StackedItemContents stackeditemcontents ) {
	                fillCraftSlotsStackedContents( stackeditemcontents );
	            }

	            @Override
	            public void clearCraftingContent() {
	                resultInventory.clearContent();
	                craftInventory.clearContent();
	            }

	            @Override
	            public boolean recipeMatches( RecipeHolder< CraftingRecipe > recipe ) {
	                return recipe.value().matches( craftInventory.asCraftInput(), owner().level() );
	            }
	        }, getGridWidth(), getGridHeight(), list, list, playerinventory, ( RecipeHolder< CraftingRecipe > ) recipeholder, useMaxItems, allowDroppingItemsToClear );
	    } finally {
	        this.finishPlacingRecipe( worldserver, ( RecipeHolder< CraftingRecipe > ) recipeholder );
	    }

	    return postPlaceAction;
	}
	
	@Override
	public void fillCraftSlotsStackedContents( StackedItemContents stackeditemcontents ) {
	    craftInventory.fillStackedContents( stackeditemcontents );
	}
}
