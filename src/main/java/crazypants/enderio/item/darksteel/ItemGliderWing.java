package crazypants.enderio.item.darksteel;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.material.ItemCapacitor;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.util.Lang;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

public class ItemGliderWing extends Item implements IResourceTooltipProvider {

  private static final int SUBITEMS = 4;
  private static final IIcon[] ICONS = new IIcon[SUBITEMS];
  private static final String[] ICONNAMES = {
    "enderio:itemGliderWing",
    "enderio:itemGliderWings",
    "enderio:itemCrudeBlade",
    "enderio:itemSharpBlade"
  };
  private static final String[] UNLOCNAMES = {
    ModObject.itemGliderWing.unlocalisedName, 
    ModObject.itemGliderWings.unlocalisedName,
    ModObject.itemCrudeBlade.unlocalisedName,
    ModObject.itemSharpBlade.unlocalisedName
  };
  
  public static ItemGliderWing create() {
    ItemGliderWing result = new ItemGliderWing();
    result.init();
    return result;
  }

  protected ItemGliderWing() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(UNLOCNAMES[0]);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  protected void init() {
    GameRegistry.registerItem(this, UNLOCNAMES[0]);
  }

  @Override
  public IIcon getIconFromDamage(int damage) {
    damage = MathHelper.clamp_int(damage, 0, 3);
    return ICONS[damage];
  }

  @Override
  public void registerIcons(IIconRegister register) {
    for (int i = 0; i < SUBITEMS; i++) {
      ICONS[i] = register.registerIcon(ICONNAMES[i]);
    }
    itemIcon = ICONS[0];
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int damage = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, 3);
    return "item." + UNLOCNAMES[damage];
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < SUBITEMS; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }

 
}
