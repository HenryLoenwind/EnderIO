package crazypants.enderio.item.darksteel;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.IShearable;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IResourceTooltipProvider;

public class ItemDiamondEdgedShears extends ItemShears implements IResourceTooltipProvider {

  public static boolean isEquipped(EntityPlayer player) {
    if(player == null) {
      return false;
    }
    ItemStack equipped = player.getCurrentEquippedItem();
    if(equipped == null) {
      return false;
    }
    return equipped.getItem() == DarkSteelItems.itemDiamondEdgedShears;
  }

  public static ItemDiamondEdgedShears create() {
    ItemDiamondEdgedShears res = new ItemDiamondEdgedShears();
    res.init();
    return res;
  }

  protected void init() {
    GameRegistry.registerItem(this, getUnlocalizedName());
  }

  protected ItemDiamondEdgedShears() {
    super();
    this.setMaxDamage(this.getMaxDamage() * Config.diamondEdgedShearsDurabilityFactor);
    setCreativeTab(EnderIOTab.tabEnderIO);
    String str = "diamondEdged_shears";
    setUnlocalizedName(str);
    setTextureName("enderIO:" + str);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
    ItemStack is = new ItemStack(this);
    par3List.add(is);
  }

  /*
   * Determines the speed to break the given block with. Higher numbers are faster, 1 is used when breaking
   * block with the wrong tool.
   *
   *  As we don't want to duplicate the check for the types of blocks shears can break, we just boost the speed
   *  when it's higher than "wrong tool slow".
   *
   * @see net.minecraft.item.ItemShears#func_150893_a(net.minecraft.item.ItemStack, net.minecraft.block.Block)
   */
  @Override
  public float func_150893_a(ItemStack p_150893_1_, Block p_150893_2_) {
    float tmp = super.func_150893_a(p_150893_1_, p_150893_2_);
    return tmp > 2.0 ? tmp * Config.diamondEdgedShearsEffeciencyFactor : tmp;
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity) {
    if (entity.worldObj.isRemote) {
      return false;
    }

    if (entity instanceof IShearable) {
      boolean result = false;
      AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(
          entity.posX - Config.diamondEdgedShearsEntityArea, entity.posY - Config.diamondEdgedShearsEntityArea, entity.posZ - Config.diamondEdgedShearsEntityArea,
          entity.posX + Config.diamondEdgedShearsEntityArea, entity.posY + Config.diamondEdgedShearsEntityArea, entity.posZ + Config.diamondEdgedShearsEntityArea);
      for (Object object : entity.worldObj.getEntitiesWithinAABB(entity.getClass(), bb)) {
        if (itemstack.stackSize > 0 && object instanceof EntityLivingBase) {
          if (super.itemInteractionForEntity(itemstack, player,
              (EntityLivingBase) object)) {
            result = true;
          }
        }
      }
      return result;
    }
    return false;
  }

  @Override
  public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
    if (player.worldObj.isRemote) {
      return false;
    }

    Block block = player.worldObj.getBlock(x, y, z);
    if (block instanceof IShearable && ((IShearable)block).isShearable(itemstack, player.worldObj, x, y, z)) {
      for (int dx = -Config.diamondEdgedShearsBlockArea; dx <= Config.diamondEdgedShearsBlockArea; dx++) {
        for (int dy = -Config.diamondEdgedShearsBlockArea; dy <= Config.diamondEdgedShearsBlockArea; dy++) {
          for (int dz = -Config.diamondEdgedShearsBlockArea; dz <= Config.diamondEdgedShearsBlockArea; dz++) {
            if (itemstack.stackSize > 0) { // stop on broken shears. not allowed to "break" out...
              Block block2 = player.worldObj.getBlock(x+dx, y+dy, z+dz);
              if (block2 instanceof IShearable) {
                IShearable target2 = (IShearable)block2;
                if (target2.isShearable(itemstack, player.worldObj, x+dx, y+dy, z+dz)) {
                  super.onBlockStartBreak(itemstack, x+dx, y+dy, z+dz, player);
                  if (dx != 0 || dy != 0 || dz != 0) {
                    player.worldObj.setBlockToAir(x+dx, y+dy, z+dz);
                  }
                }
              }
            }
          }
        }
      }
    }
    return false;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }

}
