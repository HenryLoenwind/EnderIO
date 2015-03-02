package crazypants.enderio.material;

import org.apache.commons.lang3.StringUtils;

public enum Alloy {

  ELECTRICAL_STEEL("electricalSteel", 6.0f, 0),
  ENERGETIC_ALLOY("energeticAlloy", 7.0f, 0),
  PHASED_GOLD("phasedGold", 4.0f, 0),
  REDSTONE_ALLOY("redstoneAlloy", 1.0f, 7),
  CONDUCTIVE_IRON("conductiveIron", 5.2f, 0),
  PHASED_IRON("phasedIron", 7.0f, 0),
  DARK_STEEL("darkSteel", 10.0f, 0),
  SOULARIUM("soularium", 10.0f, 0);

  public final String unlocalisedName;
  public final String iconKey;
  public final String oredictIngotName;
  public final String oredictBlockName;
  private final float hardness;
  public final int redStonePowerLevel;

  private Alloy(String baseName, float hardness, int redStonePowerLevel) {
    this.unlocalisedName = "enderio." + baseName;
    this.iconKey = "enderio:" + baseName;
    this.oredictIngotName = "ingot" + StringUtils.capitalize(baseName);
    this.oredictBlockName = "block" + StringUtils.capitalize(baseName);
    this.hardness = hardness;
    this.redStonePowerLevel = redStonePowerLevel;
  }

  public float getHardness() {
    return hardness;
  }
}
