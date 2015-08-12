package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

public class Config {
  int level_reduction_factor = 10;
  int num_levels = 5;
  Water config;
  List<Water> levels = new ArrayList<Water>();
  
  void compute() {
    Water input = config;
    for (int i = 0; i < num_levels; i++) {
      input = computeLevel(input, config, i);
    }
  }
  
  boolean testing = true;
  
  Water computeLevel(Water input, Water mats, int level) {
    Water remains = new Water();
    Water used = new Water();
    
    remains.contents.putAll(input.contents);
    Collections.sort(mats.materials);
    
    for (Material mat : mats.materials) {
      if (testing || mat.item.getItemStack() != null) {
        boolean good2go = true;
        for (Component comp : mat.components) {
          Double available = remains.contents.get(comp.name);
          if (available == null || available < comp.granularity) {
            good2go = false;
          }
        }
        if (good2go) {
          used.materials.add(mat);
          while (good2go) {
            for (Component comp : mat.components) {
              Double needed = comp.granularity;
              Double available = remains.contents.get(comp.name);
              remains.contents.put(comp.name, available - needed);
              if (used.contents.get(comp.name) == null) {
                used.contents.put(comp.name, needed * comp.factor);
              } else {
                used.contents.put(comp.name, used.contents.get(comp.name) + needed * comp.factor);
              }
              if (available < 2 * needed) {
                good2go = false;
              }
            }
          }
        }
      }
    }
    
    for (Entry<String, Double> content : remains.contents.entrySet()) {
      content.setValue(content.getValue() * level_reduction_factor);
    }
    
    levels.add(level, used);
    return remains;
  }
  
}
