package bau5.mods.projectbench.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * 
 * ContainerProjectBench
 *
 * @author _bau5
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */

public class ContainerProjectBench extends Container
{
	public TileEntityProjectBench tileEntity;
	
	public IInventory craftSupplyMatrix;
	public int craftResultSlot = 0;
	private boolean containerChanged;
	private boolean netEditingContainer = false;
		
	public ContainerProjectBench(InventoryPlayer invPlayer, TileEntityProjectBench tpb)
	{
		tileEntity = tpb;
		craftSupplyMatrix = tileEntity.craftSupplyMatrix;
		addSlotToContainer(new SlotPBCrafting(this, invPlayer.player, tileEntity, tileEntity.craftResult, 
										 tileEntity, craftResultSlot, 124, 35));
		layoutContainer(invPlayer, tileEntity);
		bindPlayerInventory(invPlayer);
		containerChanged = true;
		detectAndSendChanges();
	}
	private void layoutContainer(InventoryPlayer invPlayer, TileEntityProjectBench tpb)
	{
		int row;
		int col;
		int index = -1;
		int counter = 0;
		Slot slot = null;

		for(row = 0; row < 3; row++)
		{
			for(col = 0; col < 3; col++)
			{
				slot = new Slot(tileEntity, ++index, 30 + col * 18, 17 + row * 18);
				addSlotToContainer(slot);
				counter++;
			}
		}
		
		for(row = 0; row < 2; row++)
		{
			for(col = 0; col < 9; col++)
			{
				if(row == 1)
				{
					slot = new Slot(tileEntity, 18 + col, 8 + col * 18, 
									(row * 2 - 1) + 77 + row * 18);
					addSlotToContainer(slot);
				} else
				{
					slot = new Slot(tileEntity, 9 + col, 8 + col * 18,
							77 + row * 18);
					addSlotToContainer(slot);
				}
				counter++;
			}
		}
	}
	protected void bindPlayerInventory(InventoryPlayer invPlayer) 
	{
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9,
											8 + j * 18, 82 + i * 18 + 39));
			}
		}
		for(int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142 + 37));
		}
	}
	public void updateCrafting(boolean flag){
		tileEntity.markShouldUpdate();
	}
	
	@Override
	public ItemStack slotClick(int slot, int clickType, int clickMeta, EntityPlayer player)
	{
		if(slot <= 9 && slot > -1)
			updateCrafting(true);
		ItemStack stack = super.slotClick(slot, clickType, clickMeta, player);
		return stack;
	}
	@Override
	public boolean canInteractWith(EntityPlayer player) 
	{
		return tileEntity.isUseableByPlayer(player);
	}
	@Override
	public void putStacksInSlots(ItemStack[] par1ArrayOfItemStack) {
		tileEntity.containerInit = true;
		super.putStacksInSlots(par1ArrayOfItemStack);
		tileEntity.containerInit = false;
		tileEntity.onInventoryChanged();
	}
	@Override
	public void putStackInSlot(int slot, ItemStack itemStack) {
		tileEntity.containerInit = true;
		super.putStackInSlot(slot, itemStack);
		tileEntity.containerInit = false;
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int numSlot)
    {
        ItemStack stack = null;
        Slot slot = (Slot)this.inventorySlots.get(numSlot);

        if (slot != null && slot.getHasStack())
        {
            ItemStack stack2 = slot.getStack();
            stack = stack2.copy();
            
            if (numSlot == 0)
            {
                if (!this.mergeItemStack(stack2, 10, 55, true))
                {
                    return null;
                }
                updateCrafting(true);
            }
            //Merge crafting matrix item with supply matrix inventory
            else if(numSlot > 0 && numSlot <= 9)
            {
            	if(!this.mergeItemStack(stack2, 10, 28, false))
            	{
            		if(!this.mergeItemStack(stack2, 28, 64, false))
            		{
                		return null;
            		}
            	}
            	updateCrafting(true);
            }
            //Merge Supply matrix item with player inventory
            else if (numSlot >= 10 && numSlot <= 27)
            {
                if (!this.mergeItemStack(stack2, 28, 64, false))
                {
                    return null;
                }
            }
            //Merge player inventory item with supply matrix
            else if (numSlot >= 28 && numSlot < 64)
            {
                if (!this.mergeItemStack(stack2, 10, 28, false))
                {
                    return null;
                }
            }

            if (stack2.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (stack2.stackSize == stack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, stack2);
        }

        return stack;
    }
}

