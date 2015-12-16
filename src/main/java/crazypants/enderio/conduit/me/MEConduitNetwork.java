package crazypants.enderio.conduit.me;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.IConduit;

public class MEConduitNetwork extends AbstractConduitNetwork<IMEConduit, MEConduit> {

  public MEConduitNetwork() {
    super(MEConduit.class, IMEConduit.class);
  }

  @Override
  public void doNetworkTick() {
    List<IConduit> tmp = new ArrayList<IConduit>(conduits);
    for (IConduit conduit : tmp) {
      conduit.updateEntity();
    }
  }

}