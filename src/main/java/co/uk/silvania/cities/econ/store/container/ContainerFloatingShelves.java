package co.uk.silvania.cities.econ.store.container;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import co.uk.silvania.cities.core.CityConfig;
import co.uk.silvania.cities.econ.store.entity.TileEntityFloatingShelves;
import co.uk.silvania.cities.network.FloatingShelvesPricePacket;

public class ContainerFloatingShelves extends Container {
	
	public TileEntityFloatingShelves te;
	private IInventory adminShopInventory;
	public static int tabButton;
	
	private int field_94536_g;
	private int field_94535_f = -1;
	private final Set field_94537_h = new HashSet();
	
	public ContainerFloatingShelves(InventoryPlayer invPlayer, TileEntityFloatingShelves te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 8, 50));
		addSlotToContainer(new Slot(te, 1, 8, 72));
		addSlotToContainer(new Slot(te, 2, 8, 94));
		addSlotToContainer(new Slot(te, 3, 8, 116));
		bindPlayerInventory(invPlayer);
	}

	
	protected void bindPlayerInventory(InventoryPlayer invPlayer) {
		//C = vertical inventory slots, "columns"
		//R = horizontal inventory slots, "rows"
		for (int c = 0; c < 3; c++) {
			for (int r = 0; r < 9; r++) {
				addSlotToContainer(new Slot(invPlayer, r + c * 9 + 9, 8 + r * 18, 141 + c * 18));
			}
		}
		//H = hotbar slots
		for (int h = 0; h <9; h++) {
			addSlotToContainer(new Slot(invPlayer, h, 8 + h * 18, 199));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityPlayer) {
		return true;
	}
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack stack = null;
        Slot slotObject = (Slot) inventorySlots.get(slot);

        //null checks and checks if the item can be stacked (maxStackSize > 1)
        if (slotObject != null && slotObject.getHasStack()) {
                ItemStack stackInSlot = slotObject.getStack();
                stack = stackInSlot.copy();

                //merges the item into player inventory since its in the tileEntity
                if (slot < 9) {
                        if (!this.mergeItemStack(stackInSlot, 0, 35, true)) {
                                return null;
                        }
                }
                //places it into the tileEntity is possible since its in the player inventory
                else if (!this.mergeItemStack(stackInSlot, 0, 4, false)) {
                        return null;
                }

                if (stackInSlot.stackSize == 0) {
                        slotObject.putStack(null);
                } else {
                        slotObject.onSlotChanged();
                }

                if (stackInSlot.stackSize == stack.stackSize) {
                        return null;
                }
                slotObject.onPickupFromSlot(player, stackInSlot);
        }
        return stack;
	}
	
	@Override
    public void putStackInSlot(int slot, ItemStack item) {
        this.getSlot(slot).putStack(item);
    }
	
	public IInventory getFloatingShelvesInventory() {
		return this.adminShopInventory;
	}
	
	@Override
	public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer entityPlayer) {
		boolean test = false;
		if (!entityPlayer.worldObj.isRemote) {
			if (CityConfig.debugMode) {
				System.out.println("Slot clicked! Checking UUID vs stored one.");
				System.out.println(entityPlayer.getUniqueID().toString() + " = Current user UUID");
				System.out.println(te.ownerUuid + " = Stored UUID");
				System.out.println(te.ownerName + " = Stored Username");
			}
			if (te.ownerUuid.contains(entityPlayer.getUniqueID().toString())) {
				if (CityConfig.debugMode) {
					System.out.println("Owner is clicking the slot  (server)");
				}
				test = true;
				super.slotClick(par1, par2, par3, entityPlayer);
			} else {
				if (CityConfig.debugMode) {
					System.out.println("[server] You are not the owner! Bad " + entityPlayer.getDisplayName() + "!");
				}
			}
		} else {
			super.slotClick(par1, par2, par3, entityPlayer);
			System.out.println("Test: " + test);
		}
		return null;
	}
}