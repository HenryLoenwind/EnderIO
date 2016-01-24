package crazypants.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Log;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class TEUtil {

  private TEUtil() {
  }

  public static @Nonnull <T extends TileEntity> T getTileEntity(Class<T> clazz, IBlockAccess world, BlockCoord bc) {
    return getTileEntity(clazz, world, bc.x, bc.y, bc.z);
  }

  public static @Nonnull <T extends TileEntity> T getTileEntity(Class<T> clazz, IBlockAccess world, int x, int y, int z) {
    @Nullable
    TileEntity te = world.getTileEntity(x, y, z);
    if (clazz.isInstance(te) && te != null) {
      return (T) te;
    }
    Log.error("Fatal error accessing world. The TileEntity at " + x + "/" + y + "/" + z + " was supposed to be a " + clazz
        + ". Instead it is " + te + ". Diagnostic information follows:");
    for (int y0 = y - 2; y0 <= y + 2; y0++) {
      if (y0 <= 0 && y0 <= 255) {
        for (int x0 = x - 2; x0 <= x + 2; x0++) {
          for (int z0 = z - 2; z0 <= z + 2; z0++) {
            Block block = world.getBlock(x0, y0, z0);
            int blockMetadata = world.getBlockMetadata(x0, y0, z0);
            @Nullable
            TileEntity tileEntity = world.getTileEntity(x0, y0, z0);
            if (tileEntity == null) {
              Log.info(" Block at " + x0 + "/" + y0 + "/" + z0 + " is " + block.getUnlocalizedName() + "/" + block.getClass()
                  + " with meta " + blockMetadata);
            } else {
              Log.info(" Block at " + x0 + "/" + y0 + "/" + z0 + " is " + block.getUnlocalizedName() + "/" + block.getClass()
                  + " with meta " + blockMetadata + ". TileEntity is of class " + tileEntity.getClass().toString() + ": "
                  + tileEntity + " with position info of " + tileEntity.xCoord + "/" + tileEntity.yCoord + "/" + tileEntity.zCoord);
              if (x0 != tileEntity.xCoord || y0 != tileEntity.yCoord || z0 != tileEntity.zCoord) {
                Log.error("Coordinate mismatch. This TileEntity does not belong here!");
              }
            }
          }
        }
      }
    }
    if (te == null) {
      Log.info("Trying to recover by retrying.");
    } else {
      Log.info("Trying to recover by invalidating the TileEntity.");
      te.invalidate();
    }
    @Nullable
    TileEntity te2 = world.getTileEntity(x, y, z);
    if (te2 == null) {
      Log.error("Recovery failed. Got a null TileEntity.");
      throw new RuntimeException("See logfile");
    } else if (te == te2) {
      Log.error("Invalidating the TileEntity did not work, the wrong TileEntity did not go away.");
      throw new RuntimeException("See logfile");
    } else if (clazz.isInstance(te2)) {
      Log.info("Invalidating the TileEntity worked. But you may have lost some machine contents.");
      return (T) te2;
    } else {
      Log.error("Invalidating the TileEntity did not work, got another wrong TileEntity instead: " + te);
      throw new RuntimeException("See logfile");
    }
  }

}
