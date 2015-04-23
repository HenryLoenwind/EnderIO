package crazypants.enderio.machine.farm.farmers;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.util.BlockCoord;

public abstract class AgricraftFarmerBase implements IFarmerJoe {

  public AgricraftFarmerBase() {
  }

  public boolean init() {
    return false;
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    return false;
  }

  @Override
  public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    return false;
  }

  @Override
  public boolean canPlant(ItemStack stack) {
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    return null;
  }

  public boolean canBePlantedNormally(ItemStack stack) {
    return true;
  }

}
