package crazypants.enderio.machine.farm.farmers;

import java.util.ArrayList;
import java.util.List;

import com.InfinityRaider.AgriCraft.api.API;
import com.InfinityRaider.AgriCraft.api.APIBase;
import com.InfinityRaider.AgriCraft.api.v1.APIv1;
import com.InfinityRaider.AgriCraft.api.v1.SeedRequirementStatus;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.util.BlockCoord;

public class AgricraftFarmer extends AgricraftFarmerBase {

  public AgricraftFarmer() {
  }

  @Override
  @Optional.Method(modid = "AgriCraftAPI")
  public boolean init() {
    APIBase api = API.getAPI(1);
    if (api != null && api.getStatus().isOK() && api.getVersion() == 1) {
      agricraft = (APIv1) api;
      return true;
    } else {
      return false;
    }
  }

  private Object agricraft; // no class reference here

  @Optional.Method(modid = "AgriCraftAPI")
  private APIv1 api() {
    return (APIv1) agricraft;
  }

  @Override
  @Optional.Method(modid = "AgriCraftAPI")
  public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    if (agricraft == null || !api().isActive(farm.getWorldObj())) {
      return false;
    }

    ItemStack seeds = farm.getSeedTypeInSuppliesFor(bc);
    if (seeds == null || !api().isHandledByAgricraft(seeds)) {
      return false;
    }

    ItemStack crops = api().getCropsItems().get(0).copy();

    if (!api().isCrops(farm.getWorldObj(), bc.x, bc.y, bc.z)) {
      if (api().canPlaceCrops(farm.getWorldObj(), bc.x, bc.y, bc.z, crops)) {
        if (api().placeCrops(farm.getWorldObj(), bc.x, bc.y, bc.z, crops)) {
          // ...
        } else {
          return false;
        }
      } else {
        return false;
      }
    }

    if (!api().isEmpty(farm.getWorldObj(), bc.x, bc.y, bc.z) && !api().removeWeeds(farm.getWorldObj(), bc.x, bc.y, bc.z, true)) {
      return false;
    }

    SeedRequirementStatus status = api().canApplySeeds(farm.getWorldObj(), bc.x, bc.y, bc.z, seeds);
    if (status == SeedRequirementStatus.NEEDS_TILLING) {
      tillBlock(farm, bc);
    }

    if (api().applySeeds(farm.getWorldObj(), bc.x, bc.y, bc.z, seeds)) {
      farm.takeSeedFromSupplies(seeds, bc);
      return true;
    }

    return false;
  }

  protected boolean tillBlock(TileFarmStation farm, BlockCoord plantingLocation) {
    World worldObj = farm.getWorldObj();
    BlockCoord dirtLoc = plantingLocation.getLocation(ForgeDirection.DOWN);
    Block dirtBlock = farm.getBlock(dirtLoc);
    if ((dirtBlock == Blocks.dirt || dirtBlock == Blocks.grass) && farm.hasHoe()) {
      farm.damageHoe(1, dirtLoc);
      worldObj.setBlock(dirtLoc.x, dirtLoc.y, dirtLoc.z, Blocks.farmland);
      worldObj.playSoundEffect(dirtLoc.x + 0.5F, dirtLoc.y + 0.5F, dirtLoc.z + 0.5F,
          Blocks.farmland.stepSound.getStepResourcePath(), (Blocks.farmland.stepSound.getVolume() + 1.0F) / 2.0F,
          Blocks.farmland.stepSound.getPitch() * 0.8F);
      farm.actionPerformed(false);
      return true;
    }
    return false;
  }

  @Override
  @Optional.Method(modid = "AgriCraftAPI")
  public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    if (agricraft == null || !api().isActive(farm.getWorldObj())) {
      return false;
    }

    return api().isMature(farm.getWorldObj(), bc.x, bc.y, bc.z);
  }

  @Override
  @Optional.Method(modid = "AgriCraftAPI")
  public boolean canPlant(ItemStack stack) {
    if (agricraft == null) {
      return false;
    }

    return api().isHandledByAgricraft(stack);
  }

  @Override
  @Optional.Method(modid = "AgriCraftAPI")
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    if (agricraft == null || !api().isActive(farm.getWorldObj()) || !canHarvest(farm, bc, block, meta)) {
      return null;
    }

    if (!farm.hasHoe()) {
      farm.setNotification(TileFarmStation.NOTIFICATION_NO_HOE);
      return null;
    }

    List<ItemStack> drops = api().harvest(farm.getWorldObj(), bc.x, bc.y, bc.z);
    if (drops == null) {
      return null;
    }
    farm.damageHoe(1, bc);
    farm.actionPerformed(false);

    List<EntityItem> result = new ArrayList<EntityItem>();
    for (ItemStack stack : drops) {
      result.add(new EntityItem(farm.getWorldObj(), bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, stack.copy()));
    }

    return new HarvestResult(result, bc);
  }

  @Override
  @Optional.Method(modid = "AgriCraftAPI")
  public boolean canBePlantedNormally(ItemStack stack) {
    return agricraft == null || !api().isNativePlantingDisabled(stack);
  }

}
