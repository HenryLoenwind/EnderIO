package crazypants.enderio.conduit;

import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IClientTickingConduit {

  @SideOnly(Side.CLIENT)
  void updateEntityClient();

}
