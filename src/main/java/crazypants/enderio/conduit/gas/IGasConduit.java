package crazypants.enderio.conduit.gas;

import mekanism.api.gas.IGasHandler;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.Optional.Interface;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitType;
import crazypants.enderio.conduit.IExtractor;

@Interface(iface = "mekanism.api.gas.IGasHandler", modid = GasUtil.API_NAME)
public interface IGasConduit extends IGasHandler, IExtractor {

  boolean canOutputToDir(ForgeDirection dir);

  boolean isExtractingFromDir(ForgeDirection dir);

}
