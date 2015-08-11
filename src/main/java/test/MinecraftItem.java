package test;

import net.minecraft.item.ItemStack;

public class MinecraftItem implements Item {
  String modID;
  String itemName;
  int itemMeta;

  // modID="EnderIO" itemName="itemAlloy" itemMeta="1"
  // oreDictionary="dustCoal"
  
  /* (non-Javadoc)
   * @see test.Item#getItemStack()
   */
  @Override
  public ItemStack getItemStack() {
    return null;
  }
}
