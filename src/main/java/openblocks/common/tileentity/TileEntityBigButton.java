package openblocks.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import openblocks.client.gui.GuiBigButton;
import openblocks.common.container.ContainerBigButton;
import openmods.api.IHasGui;
import openmods.api.ISurfaceAttachment;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.inventory.TileEntityInventory;
import openmods.tileentity.OpenTileEntity;

public class TileEntityBigButton extends OpenTileEntity implements ISurfaceAttachment, IHasGui, IInventoryProvider {

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "bigbutton", true, 1));

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerBigButton(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiBigButton(new ContainerBigButton(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		return false;
	}

	public int getTickTime() {
		ItemStack stack = inventory.getStackInSlot(0);
		return stack == null? 1 : stack.stackSize;
	}

	@Override
	public EnumFacing getSurfaceDirection() {
		return getOrientation().down();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		inventory.writeToNBT(tag);

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}

	@Override
	@IncludeInterface
	public IInventory getInventory() {
		return inventory;
	}

}
