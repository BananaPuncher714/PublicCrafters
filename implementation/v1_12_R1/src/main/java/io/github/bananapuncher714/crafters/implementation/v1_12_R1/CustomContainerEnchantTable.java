package io.github.bananapuncher714.crafters.implementation.v1_12_R1;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.ContainerEnchantTable;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EnumColor;
import net.minecraft.server.v1_12_R1.IInventory;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.Items;
import net.minecraft.server.v1_12_R1.NonNullList;
import net.minecraft.server.v1_12_R1.PlayerInventory;
import net.minecraft.server.v1_12_R1.Slot;

public class CustomContainerEnchantTable extends ContainerEnchantTable {
	private List< Slot > theseSlots;
	
	public CustomContainerEnchantTable( HumanEntity player, Location location, IInventory inventory ) {
		super( ( ( CraftHumanEntity ) player ).getHandle().inventory, ( ( CraftWorld ) location.getWorld() ).getHandle(), new BlockPosition( location.getBlockX(), location.getBlockY(), location.getZ() ) );
		
		try {
			Field slots = this.getClass().getField( "slots" );
			theseSlots = ( List< Slot > ) slots.get( this );
			
		} catch ( Exception exception ) {
			try {
				Field c = this.getClass().getField( "c" );
				theseSlots = ( List< Slot > ) c.get( this );
			} catch ( Exception anotherException ) {
				anotherException.printStackTrace();
			}
		}
		
		if ( inventory != null ) {
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
			
			enchantSlots = inventory;
			
			theseSlots.clear();
			
			a(new Slot(this.enchantSlots, 0, 15, 47)
		    {
		      public boolean isAllowed(ItemStack itemstack)
		      {
		        return true;
		      }
		      
		      public int getMaxStackSize()
		      {
		        return 1;
		      }
		    });
		    a(new Slot(this.enchantSlots, 1, 35, 47)
		    {
		      public boolean isAllowed(ItemStack itemstack)
		      {
		        return (itemstack.getItem() == Items.DYE) && (EnumColor.fromInvColorIndex(itemstack.getData()) == EnumColor.BLUE);
		      }
		    });
		    for (int i = 0; i < 3; i++) {
		      for (int j = 0; j < 9; j++) {
		        a(new Slot( ( ( CraftHumanEntity ) player ).getHandle().inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
		      }
		    }
		    for (int i = 0; i < 9; i++) {
		      a(new Slot( ( ( CraftHumanEntity ) player ).getHandle().inventory, i, 8 + i * 18, 142));
		    }
		}
	}
	
	@Override
	public void b( EntityHuman entity ) {
		PlayerInventory playerinventory = entity.inventory;
		if ( !playerinventory.getCarried().isEmpty() ) {
			entity.drop( playerinventory.getCarried(), false );
			playerinventory.setCarried( ItemStack.a );
		}
	}
}
