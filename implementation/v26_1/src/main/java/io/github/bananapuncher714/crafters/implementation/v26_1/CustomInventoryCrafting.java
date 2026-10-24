package io.github.bananapuncher714.crafters.implementation.v26_1;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.util.RayTraceResult;

import com.google.common.collect.Sets;

import io.github.bananapuncher714.crafters.display.CraftDisplay;
import io.github.bananapuncher714.crafters.implementation.api.InventoryData;
import io.github.bananapuncher714.crafters.implementation.api.PublicCraftingInventory;
import io.github.bananapuncher714.crafters.implementation.v26_1.ContainerManager_v26_1.SelfContainer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;

/**
 * The important class, this is universal and what makes crafting tables public
 * 
 * @author BananaPuncher714
 */
public class CustomInventoryCrafting extends TransientCraftingContainer implements PublicCraftingInventory {
    private static Method TO_BUKKIT_ITEMSTACK;
    
    static {
        try {
            TO_BUKKIT_ITEMSTACK = CraftItemStack.class.getDeclaredMethod( "asBukkitCopy", ItemStack.class );
            TO_BUKKIT_ITEMSTACK.setAccessible( true );
        } catch ( NoSuchMethodException e ) {
            try {
                TO_BUKKIT_ITEMSTACK = CraftItemStack.class.getDeclaredMethod( "asBukkitCopy", ItemInstance.class );
                TO_BUKKIT_ITEMSTACK.setAccessible( true );
            } catch ( NoSuchMethodException e1 ) {
                e1.printStackTrace();
            }
        }
    }
    
    private static org.bukkit.inventory.ItemStack getBukkitItemStackFrom( ItemStack item ) {
        try {
            return ( org.bukkit.inventory.ItemStack ) TO_BUKKIT_ITEMSTACK.invoke( null, item );
        } catch ( IllegalAccessException | InvocationTargetException e ) {
            e.printStackTrace();
            return null;
        }
    }
    
	Set< AbstractContainerMenu > containers = Sets.newHashSet();
	private List< ItemStack > items;
	private UUID id;
	private Location bloc;
	private CraftDisplay display;
	private BlockFace face = BlockFace.SELF;
	private ContainerManager_v26_1 manager;
	protected SelfContainer selfContainer;
	
	public CustomInventoryCrafting( Location workbenchLoc, ContainerManager_v26_1 manager, SelfContainer container, int i, int j ) {
		super( container, i, j );
		id = UUID.randomUUID();
		bloc = workbenchLoc;
		selfContainer = container;
		this.manager = manager;
		setDefaults();
		display = new CraftDisplay( this );
	}
	
	private void setDefaults() {
		items = this.getContents();
	}
	
	@Override
	public void setItem( int index, ItemStack item ) {
		// Instead of updating one container, update all the containers
		// That are looking at the table, basically the viewers
		
		items.set( index, item );
		for ( AbstractContainerMenu container : containers ) {
			container.slotsChanged( this );
		}
		
		updateFace();
		
		// Update the armorstand grid
		display.update();
	}
	
	@Override
	public ItemStack removeItem( int slot, int count ) {
		ItemStack itemstack = ContainerHelper.removeItem( items, slot, count );
		if ( !itemstack.isEmpty() ) {
			for ( AbstractContainerMenu container : containers ) {
				container.slotsChanged( this );
			}
		}
		
		updateFace();
		
		// Update the armorstand grid
		display.update();
		return itemstack;
	}
	
	// This is to fetch a nice list of Bukkit ItemStacks from the list of NMS ItemStacks
	@Override
	public List< org.bukkit.inventory.ItemStack > getBukkitItems() {
		List< org.bukkit.inventory.ItemStack > bukkitItems = new ArrayList< org.bukkit.inventory.ItemStack >();
		for ( ItemStack item : items ) {
			bukkitItems.add( getBukkitItemStackFrom( item ) );
		}
		return bukkitItems;
	}

	@Override
	public InventoryData getData() {
	    return new InventoryData( getBukkitItems(), face );
	}
	
	@Override
	public org.bukkit.inventory.ItemStack getResult() {
		if ( this.resultInventory != null ) {
			return getBukkitItemStackFrom( resultInventory.getItem( 0 ) );
		}
		return null;
	}
	
	protected void setItems( List< org.bukkit.inventory.ItemStack > items ) {
		int index = 0;
		for ( org.bukkit.inventory.ItemStack item : items ) {
			this.items.set( index++, CraftItemStack.asNMSCopy( item ) );
		}
		
		// Want to update the result without having to use a real player
		if ( this.resultInventory instanceof ResultContainer ) {
			CustomContainerWorkbench container = new CustomContainerWorkbench( 0, manager.mockPlayer.getBukkitEntity(), bloc, this, ( ResultContainer ) resultInventory );
			
			container.setTitle( ContainerManager_v26_1.WORKBENCH_TITLE );
			container.slotsChanged( this );
			
			CraftingInventory crafting = ( CraftingInventory ) container.getBukkitView().getTopInventory();
			Bukkit.getPluginManager().callEvent( new PrepareItemCraftEvent( crafting, container.getBukkitView(), false ) );
		}
		
		updateFace();
		
		display.forceUpdate();
	}
	
	// Add another viewer
	protected void addContainer( AbstractContainerMenu container ) {
		containers.add( container );
	}
	
	// Remove a container that stopped viewing it
	protected void removeContainer( AbstractContainerMenu container ) {
		containers.remove( container );
	}
	
	protected void setLocation( Location newLoc ) {
		bloc = newLoc;
	}
	
	protected void setFacing( BlockFace face ) {
	    if ( this.face != face ) {
	        this.face = face;
	        display.forceUpdate();
	    }
	}
	
	@Override
	public BlockFace getFace() {
	    return face;
	}
	
	protected void updateFace() {
	    if ( isEmpty() ) {
	        face = BlockFace.SELF;
	    } else if ( face == BlockFace.SELF ) {
	        if ( containers.isEmpty() ) {
                // No face, and no containers... default to south facing
                face = BlockFace.SOUTH;
	        } else {
	            // Grab the current viewer
	            HumanEntity entity = selfContainer.getBukkitView().getPlayer();
	            RayTraceResult result = entity.rayTraceBlocks( 10 );
	            BlockFace clicked = result.getHitBlockFace();
	            
	            if ( clicked == BlockFace.UP || clicked == BlockFace.DOWN ) {
	                // No help here, get their facing direction from their yaw
	                double yaw = entity.getLocation().getYaw();
	                
	                int re = ( int ) ( ( yaw + 495 ) % 360 ) / 90;
	                face = new BlockFace[] { BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH }[ re ];
	            } else {
	                face = clicked;
	            }
	        }
	    }
	    
	    display.forceUpdate();
	}
	
	@Override
	public UUID getUUID() {
		return id;
	}
	
	@Override
	public Location getLocation() {
		return bloc;
	}
	
	@Override
	public CraftDisplay getCraftDisplay() {
		return display;
	}
	
	@Override
	public PublicCraftingInventory move( Location location ) {
		display.stop();
		if ( manager.get( bloc ) == this ) {
			manager.benches.remove( bloc );
		}
		bloc = location;
		CustomInventoryCrafting whatsHere = manager.put( location, this );
		display = new CraftDisplay( this );
		display.update( true );
		return whatsHere;
	}
	
	@Override
	public void remove() {
		display.stop();
		for ( ItemStack item : items ) {
			org.bukkit.inventory.ItemStack is = CraftItemStack.asBukkitCopy( item );
			if ( is.getType() != Material.AIR ) {
				bloc.getWorld().dropItem( bloc.clone().add( .5, .9, .5 ), is );
			}
		}
		items.clear();
	}
	
	@Override
	public void update() {
		if ( !bloc.getBlock().getType().name().equalsIgnoreCase( "CRAFTING_TABLE" ) ) {
			remove();
			manager.benches.remove( bloc );
		} else {
			manager.put( bloc, this );
			display.update();
		}
	}
}
