package crazypants.enderio.fluid;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;

public class BlockFluidEio extends BlockFluidClassic {

  public static BlockFluidEio create(Fluid fluid, Material material) {
    BlockFluidEio res = new BlockFluidEio(fluid, material);
    res.init();
    fluid.setBlock(res);    
    return res;
  }

  protected Fluid fluid;
  protected boolean finite = false;

  protected BlockFluidEio(Fluid fluid, Material material) {
    super(fluid, material);
    this.fluid = fluid;
    setBlockName(fluid.getUnlocalizedName());
  }

  protected void init() {
    GameRegistry.registerBlock(this, "block" + StringUtils.capitalize(fluidName));
  }

  public boolean isFinite() {
    return finite;
  }

  public void setFinite(boolean finite) {
    this.finite = finite;
  }

  @SideOnly(Side.CLIENT)
  protected IIcon[] icons;

  @Override
  public IIcon getIcon(int side, int meta) {
    return side != 0 && side != 1 ? this.icons[1] : this.icons[0];
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iconRegister) {
    icons = new IIcon[] { iconRegister.registerIcon("enderio:" + fluidName + "_still"),
        iconRegister.registerIcon("enderio:" + fluidName + "_flow") };

    fluid.setIcons(icons[0], icons[1]);
  }

  @Override
  public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
    if(world.getBlock(x, y, z).getMaterial().isLiquid()) {
      return false;
    }
    return super.canDisplace(world, x, y, z);
  }

  @Override
  public boolean displaceIfPossible(World world, int x, int y, int z) {
    if(world.getBlock(x, y, z).getMaterial().isLiquid()) {
      return false;
    }
    return super.displaceIfPossible(world, x, y, z);
  }

  @Override
  public void onEntityCollidedWithBlock(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity entity) {
    if(entity.worldObj.isRemote) {
      super.onEntityCollidedWithBlock(p_149670_1_, p_149670_2_, p_149670_3_, p_149670_4_, entity);
      return;
    }

    if(this == EnderIO.blockFireWater) {
      entity.setFire(50);
    } else if(this == EnderIO.blockRocketFuel && entity instanceof EntityLivingBase) {
      ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.jump.id, 150, 3, true));
    } else if(this == EnderIO.blockNutrientDistillation && entity instanceof EntityPlayerMP) {
      long time = entity.worldObj.getTotalWorldTime();
      EntityPlayerMP player = (EntityPlayerMP) entity;
      if(time % Config.nutrientFoodBoostDelay == 0 && player.getEntityData().getLong("eioLastFoodBoost") != time) {
        player.getFoodStats().addStats(1, 0.1f);
        player.getEntityData().setLong("eioLastFoodBoost", time);
      }
    } else if (this == EnderIO.blockHootch && entity instanceof EntityLivingBase) {
      ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.confusion.id, 150, 0, true));
    }

    super.onEntityCollidedWithBlock(p_149670_1_, p_149670_2_, p_149670_3_, p_149670_4_, entity);
  }
  
  /*
   * Start of finite liquid handling
   * 
   * Finite liquids fall like sand blocks. In addition, our finite liquids also follow the flow.
   * 
   */
  @Override
  public void updateTick(World world, int x, int y, int z, Random rand) {
    if (finite) {
      if (isSourceBlock(world, x, y, z)) { // Simple falling
        if (!isInWorld(y + densityDir) || isFlowing(world, x, y + densityDir, z)) {
          doFlow(world, x, y, z, x, y + densityDir, z);
        }
      } else { // Follow the flow
        Set<Triple> seen = new HashSet<Triple>();
        findAndPullSourceBlock(world, seen, x, y, z, false);
      }
    }
    super.updateTick(world, x, y, z, rand);
  }
  
  private static boolean isInWorld(int y) {
    return y > 0 && y <= 255;
  }
  
  /*
   * same liquid
   */
  protected boolean isSame(World world, int x, int y, int z) {
    return (world.getBlock(x, y, z) == this);
  }
  
  /*
   * same liquid and not a source block
   */
  protected boolean isFlowing(World world, int x, int y, int z) {
    return (world.getBlock(x, y, z) == this && world.getBlockMetadata(x, y, z) > 0);
  }
  
  /*
   * Replacement for isFlowingVertically() that does the right thing
   */
  public boolean isFlowingVertically2(IBlockAccess world, int x, int y, int z) {
    return world.getBlock(x, y + densityDir, z) == this && world.getBlock(x, y - densityDir, z) == this;
  }

  /*
   * same liquid an nearer to a source block
   */
  protected boolean isUpflow(World world, int x, int y, int z, int meta) {
    return (world.getBlock(x, y, z) == this && world.getBlockMetadata(x, y, z) <= meta);
  }

  /*
   * move a source block, or delete it if it flows out of the world
   */
  protected void doFlow(World world, int x0, int y0, int z0, int x1, int y1, int z1) {
    if (isInWorld(y1)) {
      world.setBlock(x1, y1, z1, world.getBlock(x0, y0, z0), 0, 3);
    }
    world.setBlockToAir(x0, y0, z0);
  }

  private static final int[][] dirs = { {1,0}, {0,-1}, {0,1}, {-1,0} };
  
  /*
   * Follow the upstream till we find a source block. Make it flow into our direction.
   * 
   * This will not be the direct path, but the expected use case is for one block to settle into a
   * stable position, not for complicated puddles to be drained.
   */
  protected boolean findAndPullSourceBlock(World world, Set<Triple> seen, int x, int y, int z, boolean foundStepUp) {
    Triple t = new Triple(x, y, z);
    if (!seen.contains(t)) {
      seen.add(t);

      // try to go up first
      if (isInWorld(y - densityDir) && isSame(world, x, y - densityDir, z)) {
        if (isSourceBlock(world, x, y - densityDir, z)) {
          doFlow(world, x, y - densityDir, z, x, y, z);
          return true;
        } else if (findAndPullSourceBlock(world, seen, x, y - densityDir, z, true)) {
          return true;
        }
      }
      
      // then look around
      for (int[] dir : dirs) {
        int x2 = dir[0] + x;
        int z2 = dir[1] + z;
        int meta = world.getBlockMetadata(x, y, z);
        if (isSourceBlock(world, x2, y, z2)) {
          if (foundStepUp) { // don't flow unless there is a "down" to flow to
            doFlow(world,x2, y, z2, x, y, z);
          }
          return true;
        } else if (isUpflow(world, x2, y, z2, meta) && !isFlowingVertically2(world, x2, y, z2)) {
          if (findAndPullSourceBlock(world, seen, x2, y, z2, foundStepUp)) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  protected static class Triple {
    private final int x;
    private final int y;
    private final int z;
    Triple(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
    public int hashCode() {
      return (int) (this.x ^ this.y ^ this.z);
    }
    public boolean equals(Object paramObject) {
      if (paramObject instanceof Triple)
        return (this.x == ((Triple) paramObject).x && this.y == ((Triple) paramObject).y && this.z == ((Triple) paramObject).z);
      return false;
    }

  }

}