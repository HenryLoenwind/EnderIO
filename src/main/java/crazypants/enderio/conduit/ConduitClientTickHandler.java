package crazypants.enderio.conduit;

import static crazypants.enderio.conduit.IConduitBundle.FacadeRenderState.FULL;
import static crazypants.enderio.conduit.IConduitBundle.FacadeRenderState.WIRE_FRAME;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.IConduitBundle.FacadeRenderState;

public class ConduitClientTickHandler {

  public static final ConduitClientTickHandler instance = new ConduitClientTickHandler();

  private static final Map<IConduitBundle, Boolean> conduits = new WeakHashMap<IConduitBundle, Boolean>();

  private ConduitClientTickHandler() {
    FMLCommonHandler.instance().bus().register(this);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (event.phase == Phase.END) {
      doClientTick();
    }
  }

  private boolean lastHideFacades = false;
  private ConduitDisplayMode lastDisplayMode = null;

  @SideOnly(Side.CLIENT)
  public void doClientTick() {
    final EntityPlayer clientPlayer = EnderIO.proxy.getClientPlayer();
    if (clientPlayer != null) {
      boolean hideFacades = ConduitUtil.shouldHeldItemHideFacades(clientPlayer);
      if (lastHideFacades != hideFacades) {
        lastHideFacades = hideFacades;
        FacadeRenderState rs = hideFacades ? WIRE_FRAME : FULL;
        System.out.println("FCR change");
        for (IConduitBundle te : conduits.keySet()) {
          if (te.hasFacade()) {
            te.updateFacadeRenderState(rs);
          }
        }
      }
      ConduitDisplayMode curMode = ConduitDisplayMode.getDisplayMode(clientPlayer.getCurrentEquippedItem());
      if (lastDisplayMode != curMode) {
        lastDisplayMode = curMode;
        System.out.println("CDM change");
        for (IConduitBundle te : conduits.keySet()) {
          te.updateConduitDisplayMode(curMode);
        }
      }
    }
  }

  /**
   * Update a conduit's facade render state and display mode when it is added to
   * a network. This is needed so conduits that are added while the player is
   * holding a wrench (e.g. by another player or by loading a chunk) are updated
   * correctly.
   * 
   * @param con
   *          The conduit to update
   */
  private void welcomeConduit(IConduitBundle te) {
    System.out.println("Welcome conduit");
    if (lastDisplayMode != null && te.getWorld().isRemote) {
      if (te.hasFacade()) {
        FacadeRenderState rs = lastHideFacades ? WIRE_FRAME : FULL;
        te.updateFacadeRenderState(rs);
      }
      te.updateConduitDisplayMode(lastDisplayMode);
    }
  }

  public static void registerConduit(IConduitBundle te) {
    instance.conduits.put(te, null);
    instance.welcomeConduit(te);
  }

}
