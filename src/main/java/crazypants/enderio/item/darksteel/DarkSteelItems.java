package crazypants.enderio.item.darksteel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

import crazypants.enderio.config.Config;
import net.minecraftforge.client.MinecraftForgeClient;

public class DarkSteelItems {

  public static ItemGliderWing itemGliderWing;

  public static ItemDarkSteelArmor itemDarkSteelHelmet;
  public static ItemDarkSteelArmor itemDarkSteelChestplate;
  public static ItemDarkSteelArmor itemDarkSteelLeggings;
  public static ItemDarkSteelArmor itemDarkSteelBoots;
  public static ItemDarkSteelSword itemDarkSteelSword;
  public static ItemDarkSteelPickaxe itemDarkSteelPickaxe;
  public static ItemDarkSteelAxe itemDarkSteelAxe;
  public static ItemDarkSteelShears itemDarkSteelShears;

  public static ItemDiamondEdgedShears itemDiamondEdgedShears;

  private DarkSteelItems() {
  }

  public static void createDarkSteelArmorItems() {
    itemGliderWing = ItemGliderWing.create();

    itemDarkSteelHelmet = ItemDarkSteelArmor.create(0);
    itemDarkSteelChestplate = ItemDarkSteelArmor.create(1);
    itemDarkSteelLeggings = ItemDarkSteelArmor.create(2);
    itemDarkSteelBoots = ItemDarkSteelArmor.create(3);

    itemDarkSteelSword = ItemDarkSteelSword.create();
    itemDarkSteelPickaxe = ItemDarkSteelPickaxe.create();
    itemDarkSteelAxe = ItemDarkSteelAxe.create();
    itemDarkSteelShears = ItemDarkSteelShears.create();
    
    itemDiamondEdgedShears = ItemDiamondEdgedShears.create();
  }

  public static void addLoot() {
    if(Config.lootTheEnder) {
      ItemStack sword = new ItemStack(itemDarkSteelSword, 1, 0);
      ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(sword, 1, 1, 5));
      ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(new WeightedRandomChestContent(sword, 1, 1, 5));
      ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(new WeightedRandomChestContent(sword, 1, 1, 4));
      ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(new WeightedRandomChestContent(sword, 1, 1, 4));
    }

    if(Config.lootDarkSteelBoots) {
      ItemStack boots = new ItemStack(itemDarkSteelBoots, 1, 0);
      ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(boots, 1, 1, 5));
      ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(new WeightedRandomChestContent(boots, 1, 1, 5));
    }
  }

  @SideOnly(Side.CLIENT)
  public static void registerItemRenderer() {
    PoweredItemRenderer dsr = new PoweredItemRenderer();
    MinecraftForgeClient.registerItemRenderer(itemDarkSteelBoots, dsr);
    MinecraftForgeClient.registerItemRenderer(itemDarkSteelLeggings, dsr);
    MinecraftForgeClient.registerItemRenderer(itemDarkSteelChestplate, dsr);
    MinecraftForgeClient.registerItemRenderer(itemDarkSteelHelmet, dsr);
    MinecraftForgeClient.registerItemRenderer(itemDarkSteelSword, dsr);
    MinecraftForgeClient.registerItemRenderer(itemDarkSteelPickaxe, dsr);
    MinecraftForgeClient.registerItemRenderer(itemDarkSteelAxe, dsr);
    MinecraftForgeClient.registerItemRenderer(itemDarkSteelShears, dsr);
  }
}
