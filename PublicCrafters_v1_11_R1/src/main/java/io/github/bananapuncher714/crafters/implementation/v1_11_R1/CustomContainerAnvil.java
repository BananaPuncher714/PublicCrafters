package io.github.bananapuncher714.crafters.implementation.v1_11_R1;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_11_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventoryAnvil;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;

import net.minecraft.server.v1_11_R1.BlockAnvil;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.Blocks;
import net.minecraft.server.v1_11_R1.ContainerAnvil;
import net.minecraft.server.v1_11_R1.Enchantment;
import net.minecraft.server.v1_11_R1.EnchantmentManager;
import net.minecraft.server.v1_11_R1.EntityHuman;
import net.minecraft.server.v1_11_R1.IBlockData;
import net.minecraft.server.v1_11_R1.IInventory;
import net.minecraft.server.v1_11_R1.InventoryCraftResult;
import net.minecraft.server.v1_11_R1.ItemNameTag;
import net.minecraft.server.v1_11_R1.ItemStack;
import net.minecraft.server.v1_11_R1.Items;
import net.minecraft.server.v1_11_R1.Slot;
import net.minecraft.server.v1_11_R1.World;

public class CustomContainerAnvil extends ContainerAnvil {
	private IInventory result;
	private IInventory subcontainer;
	private Location bloc;
	private HumanEntity player;
	private int xpCost = 0;
	private int repairCost = 0;
	private String name;

	public CustomContainerAnvil( HumanEntity player, Location blockLocation, CustomAnvilSubcontainer contents, InventoryCraftResult result ) {
		super( ( ( CraftHumanEntity ) player ).getHandle().inventory, ( ( CraftWorld ) blockLocation.getWorld() ).getHandle(), new BlockPosition( blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ() ), ( ( CraftHumanEntity ) player ).getHandle() );

		this.c.clear();

		this.player = player;
		bloc = blockLocation;
		this.result = result;
		subcontainer = contents;

		World world = ( ( CraftWorld ) blockLocation.getWorld() ).getHandle();
		BlockPosition blockposition = new BlockPosition( bloc.getBlockX(), bloc.getBlockY(), bloc.getBlockZ() );

		a( new Slot( subcontainer, 0, 27, 47 ) );
		a( new Slot( subcontainer, 1, 76, 47 ) );
		a( new Slot( result, 2, 134, 47 ) {
			public boolean isAllowed(ItemStack itemstack) {
				return false;
			}

			public boolean isAllowed(EntityHuman entityhuman)  {
				return ((entityhuman.abilities.canInstantlyBuild) || (entityhuman.expLevel >= xpCost )) && (repairCost > 0) && (hasItem());
			}

			public ItemStack a(EntityHuman entityhuman, ItemStack itemstack) {
				if (!entityhuman.abilities.canInstantlyBuild) {
					entityhuman.levelDown( - repairCost );
				}
				ItemStack itemstack1 = subcontainer.getItem( 0 );
				if ((itemstack1.getCount() != 1) && (!entityhuman.abilities.canInstantlyBuild) && (!(itemstack1.getItem() instanceof ItemNameTag))) {
					itemstack1.setCount(itemstack1.getCount() - 1);
				} else {
					subcontainer.setItem( 0, ItemStack.a );
				}
				if ( xpCost > 0) {
					ItemStack itemstack2 = subcontainer.getItem(1);
					if ((!itemstack2.isEmpty()) && (itemstack2.getCount() > xpCost )) {
						itemstack2.subtract( xpCost );
						subcontainer.setItem(1, itemstack2);
					} else {
						subcontainer.setItem(1, ItemStack.a);
					}
				} else {
					subcontainer.setItem(1, ItemStack.a);
				}
				repairCost = 0;
				IBlockData iblockdata = world.getType(blockposition);
				if ((!entityhuman.abilities.canInstantlyBuild) && (!world.isClientSide) && (iblockdata.getBlock() == Blocks.ANVIL) && (entityhuman.getRandom().nextFloat() < 0.12F)) {
					int i = ((Integer)iblockdata.get(BlockAnvil.DAMAGE)).intValue();

					i++;
					if (i > 2) {
						world.setAir(blockposition);
						world.triggerEffect(1029, blockposition, 0);
					}
					else {
						world.setTypeAndData(blockposition, iblockdata.set(BlockAnvil.DAMAGE, Integer.valueOf(i)), 2);
						world.triggerEffect(1030, blockposition, 0);
					}
				} else if (!world.isClientSide) {
					world.triggerEffect(1030, blockposition, 0);
				}
				return itemstack;
			}
		});

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				a(new Slot( ( ( CraftHumanEntity ) player ).getHandle().inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for ( int i = 0; i < 9; i++ ) {
			a(new Slot( ( ( CraftHumanEntity ) player ).getHandle().inventory, i, 8 + i * 18, 142));
		}
	}

	// Update inventory?
	public void a( IInventory iinventory ) {
		super.a( iinventory );
		if ( iinventory == result ) {
			e();
		}
	}

	public void e() {
		EntityHuman player = ( ( CraftHumanEntity ) this.player ).getHandle();
		
		ItemStack itemstack = subcontainer.getItem(0);

		this.a = 1;
		int i = 0;
		byte b0 = 0;
		byte b1 = 0;
		if (itemstack.isEmpty()) {
			CraftEventFactory.callPrepareAnvilEvent(getBukkitView(), ItemStack.a);
			this.a = 0;
		} else {
			ItemStack itemstack1 = itemstack.cloneItemStack();
			if ((itemstack1.getCount() > 1) && (!player.abilities.canInstantlyBuild) && (!(itemstack1.getItem() instanceof ItemNameTag))) {
				itemstack1.setCount(1);
			}
			ItemStack itemstack2 = subcontainer.getItem(1);
			Map map = EnchantmentManager.a(itemstack1);
			int j = b0 + itemstack.getRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getRepairCost());

			xpCost = 0;
			if (!itemstack2.isEmpty()) {
				boolean flag = (itemstack2.getItem() == Items.ENCHANTED_BOOK) && (!Items.ENCHANTED_BOOK.h(itemstack2).isEmpty());
				if ((itemstack1.f()) && (itemstack1.getItem().a(itemstack, itemstack2))) {
					int k = Math.min(itemstack1.i(), itemstack1.k() / 4);
					if (k <= 0) {
						CraftEventFactory.callPrepareAnvilEvent(getBukkitView(), ItemStack.a);
						this.a = 0;
						return;
					}
					for (int l = 0; (k > 0) && (l < itemstack2.getCount()); l++) {
						int i1 = itemstack1.i() - k;
						itemstack1.setData(i1);
						i++;
						k = Math.min(itemstack1.i(), itemstack1.k() / 4);
						xpCost = l;
					}
				} else {
					if ((!flag) && ((itemstack1.getItem() != itemstack2.getItem()) || (!itemstack1.f()))) {
						CraftEventFactory.callPrepareAnvilEvent(getBukkitView(), ItemStack.a);
						this.a = 0;
						return;
					}
					if ((itemstack1.f()) && (!flag)) {
						int k = itemstack.k() - itemstack.i();
						int l = itemstack2.k() - itemstack2.i();
						int i1 = l + itemstack1.k() * 12 / 100;
						int j1 = k + i1;
						int k1 = itemstack1.k() - j1;
						if (k1 < 0) {
							k1 = 0;
						}
						if (k1 < itemstack1.getData())
						{
							itemstack1.setData(k1);
							i += 2;
						}
					}
					Map< Enchantment, Integer > map1 = EnchantmentManager.a(itemstack2);
					boolean flag1 = false;
					boolean flag2 = false;
					Iterator iterator = map1.keySet().iterator();
					while (iterator.hasNext()) {
						Enchantment enchantment = (Enchantment)iterator.next();
						if (enchantment != null) {
							int l1 = map.containsKey(enchantment) ? ((Integer)map.get(enchantment)).intValue() : 0;
							int i2 = ((Integer)map1.get(enchantment)).intValue();

							i2 = l1 == i2 ? i2 + 1 : Math.max(i2, l1);
							boolean flag3 = enchantment.canEnchant(itemstack);
							if ((player.abilities.canInstantlyBuild) || (itemstack.getItem() == Items.ENCHANTED_BOOK)) {
								flag3 = true;
							}
							Iterator iterator1 = map.keySet().iterator();
							while (iterator1.hasNext()) {
								Enchantment enchantment1 = (Enchantment)iterator1.next();
								if ((enchantment1 != enchantment) && (!enchantment.c(enchantment1))) {
									flag3 = false;
									i++;
								}
							}
							if (!flag3) {
								flag2 = true;
							} else {
								flag1 = true;
								if (i2 > enchantment.getMaxLevel()) {
									i2 = enchantment.getMaxLevel();
								}
								map.put(enchantment, Integer.valueOf(i2));
								int j2 = 0;
								switch ( enchantment.e() ) {
								case COMMON: 
									j2 = 1;
									break;
								case RARE: 
									j2 = 2;
									break;
								case UNCOMMON: 
									j2 = 4;
									break;
								case VERY_RARE: 
									j2 = 8;
								}
								if (flag) {
									j2 = Math.max(1, j2 / 2);
								}
								i += j2 * i2;
							}
						}
					}
					if ((flag2) && (!flag1)) {
						CraftEventFactory.callPrepareAnvilEvent(getBukkitView(), ItemStack.a);
						this.a = 0;
						return;
					}
				}
			}
			if (StringUtils.isBlank(this.l)) {
				if (itemstack.hasName()) {
					b1 = 1;
					i += b1;
					itemstack1.s();
				}
			} else if (!this.l.equals(itemstack.getName())) {
				b1 = 1;
				i += b1;
				itemstack1.g(this.l);
			}
			this.a = (j + i);
			if (i <= 0) {
				itemstack1 = ItemStack.a;
			}
			if ((b1 == i) && (b1 > 0) && (this.a >= 40)) {
				this.a = 39;
			}
			if ((this.a >= 40) && (!player.abilities.canInstantlyBuild)) {
				itemstack1 = ItemStack.a;
			}
			if (!itemstack1.isEmpty()) {
				int k2 = itemstack1.getRepairCost();
				if ((!itemstack2.isEmpty()) && (k2 < itemstack2.getRepairCost())) {
					k2 = itemstack2.getRepairCost();
				}
				if ((b1 != i) || (b1 == 0)) {
					k2 = k2 * 2 + 1;
				}
				itemstack1.setRepairCost(k2);
				EnchantmentManager.a(map, itemstack1);
			}
			CraftEventFactory.callPrepareAnvilEvent(getBukkitView(), itemstack1);
			b();
		}
	}

//	public void addSlotListener( ICrafting icrafting ) {
//		// TODO does something
//	}

	// On inventory close
	public void b( EntityHuman entityhuman ) {
		super.b(entityhuman);
	}

	public boolean a(EntityHuman entityhuman) {
		return canSee( entityhuman );
		//		if (!this.checkReachable) {
			//			return true;
			//		}
		//		return this.i.getType(this.j).getBlock() == Blocks.ANVIL;
	}

	public boolean canSee( EntityHuman human ) {
		return true;
	}

	// Shift click
	public ItemStack b( EntityHuman entityhuman, int i ) {
		ItemStack itemstack = ItemStack.a;
		Slot slot = ( Slot ) this.c.get(i);
		if ((slot != null) && (slot.hasItem())) {
			ItemStack itemstack1 = slot.getItem();

			itemstack = itemstack1.cloneItemStack();
			if (i == 2) {
				if (!a(itemstack1, 3, 39, true)) {
					return ItemStack.a;
				}
				slot.a(itemstack1, itemstack);
			} else if ((i != 0) && (i != 1)) {
				if ((i >= 3) && (i < 39) && (!a(itemstack1, 0, 2, false))) {
					return ItemStack.a;
				}
			}
			else if (!a(itemstack1, 3, 39, false)) {
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
			slot.a(entityhuman, itemstack1);
		}
		return itemstack;
	}

	// Rename item
	public void a( String s ) {
		if ( getSlot( 2 ).hasItem() ) {
			ItemStack itemstack = getSlot( 2 ).getItem();
			if ( StringUtils.isBlank( s ) ) {
				itemstack.s();
			} else {
				itemstack.g( "RENAME SUCCESSFUL" );
			}
		}
		e();
	}

//	public void b() {
//		super.b();
//		for ( int i = 0; i < this.listeners.size(); i++ ) {
//			ICrafting icrafting = ( ICrafting ) this.listeners.get(i);
//
//			//			icrafting.setContainerData(this, 0, this.a );
//		}
//	}

	public CraftInventoryView getBukkitView() {
		CraftInventory inventory = new CraftInventoryAnvil( bloc, subcontainer, result, this );
		return new CraftInventoryView( player, inventory, this);
	}
}
