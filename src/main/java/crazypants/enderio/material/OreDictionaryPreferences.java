package crazypants.enderio.material;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.enderio.core.common.util.OreDictionaryHelper;

import crazypants.util.ItemStackHolder;

public final class OreDictionaryPreferences {

  public static final OreDictionaryPreferences instance = new OreDictionaryPreferences();

  public static void loadConfig() {
    OreDictionaryPreferenceParser.loadConfig();
  }

  private Map<String, ItemStackHolder> preferences = new HashMap<String, ItemStackHolder>();
  private Map<ItemStackHolder, ItemStackHolder> stackCache = new HashMap<ItemStackHolder, ItemStackHolder>();

  public void setPreference(String oreDictName, ItemStack stack) {
    if(oreDictName == null || stack == null) {
      return;
    }
    preferences.put(oreDictName, new ItemStackHolder(stack));
  }

  public ItemStack getPreferred(String oreDictName) {
    if (!preferences.containsKey(oreDictName)) {
      List<ItemStack> ores = OreDictionaryHelper.getOres(oreDictName);
      if(!ores.isEmpty() && ores.get(0) != null) {
        preferences.put(oreDictName, new ItemStackHolder(ores.get(0)));
      } else {
        preferences.put(oreDictName, ItemStackHolder.NULL);
      }
    }
    return preferences.get(oreDictName).getItemStack();
  }

  public ItemStack getPreferred(ItemStack stack) {
    if(stack == null || stack.getItem() == null) {
      return stack;
    }
    ItemStackHolder key = new ItemStackHolder(stack);
    if (!stackCache.containsKey(key)) {
      stackCache.put(key, key);

      int[] ids = OreDictionary.getOreIDs(stack);
      if (ids != null) {
        for (int id : ids) {
          String oreDict = OreDictionary.getOreName(id);
          if (preferences.containsKey(oreDict)) {
            stackCache.put(key, preferences.get(oreDict));
            break;
          }
        }
      }
    }

    return stackCache.get(key).getItemStack();
  }

}
