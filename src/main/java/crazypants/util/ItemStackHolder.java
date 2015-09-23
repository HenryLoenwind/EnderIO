package crazypants.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemStackHolder {
  private final Item item;
  private final int stackSize;
  private final int itemDamage;
  private final NBTTagCompound stackTagCompound;

  public static final ItemStackHolder NULL = new ItemStackHolder();

  private ItemStackHolder() {
    this.item = null;
    this.stackSize = 0;
    this.itemDamage = -1;
    this.stackTagCompound = null;
  }

  public ItemStackHolder(ItemStack itemStack) {
    this.item = itemStack.getItem();
    this.stackSize = itemStack.stackSize;
    this.itemDamage = itemStack.getItemDamage();
    if (itemStack.stackTagCompound != null) {
      this.stackTagCompound = (NBTTagCompound) itemStack.stackTagCompound.copy();
    } else {
      this.stackTagCompound = null;
    }
  }

  public ItemStack getItemStack() {
    if (item == null) {
      return null;
    }
    ItemStack itemStack = new ItemStack(item, stackSize, itemDamage);
    if (stackTagCompound != null) {
      itemStack.stackTagCompound = (NBTTagCompound) stackTagCompound.copy();
    }
    return itemStack;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((item == null) ? 0 : item.hashCode());
    result = prime * result + itemDamage;
    result = prime * result + stackSize;
    result = prime * result + ((stackTagCompound == null) ? 0 : stackTagCompound.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj instanceof ItemStackHolder) {
      ItemStackHolder other = (ItemStackHolder) obj;
      if (item != other.item) {
        return false;
      }
      if (itemDamage != other.itemDamage) {
        return false;
      }
      if (stackSize != other.stackSize) {
        return false;
      }
      if (stackTagCompound == null) {
        if (other.stackTagCompound != null) {
          return false;
        }
      } else if (!stackTagCompound.equals(other.stackTagCompound)) {
        return false;
      }
    } else if (obj instanceof ItemStack) {
      ItemStack other = (ItemStack) obj;
      if (item != other.getItem()) {
        return false;
      }
      if (itemDamage != other.getItemDamage()) {
        return false;
      }
      if (stackSize != other.stackSize) {
        return false;
      }
      if (stackTagCompound == null) {
        if (other.stackTagCompound != null) {
          return false;
        }
      } else if (!stackTagCompound.equals(other.stackTagCompound)) {
        return false;
      }
    }
    return true;
  }

}