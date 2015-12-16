package crazypants.enderio.conduit.me;

import java.util.EnumSet;

import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitType;

public interface IMEConduit extends IConduitType {

  MEConduitGrid getGrid();
  
  EnumSet<ForgeDirection> getConnections();
  
  boolean isDense();

  int getChannelsInUse();

}
