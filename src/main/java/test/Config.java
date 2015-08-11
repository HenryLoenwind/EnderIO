package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

public class Config {
  Water config;
  List<Water> levels = new ArrayList<Water>();
  
  void compute() {
    Water input = config;
    for (int i = 0; i < 5; i++) {
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
      if (testing || mat.result.getItemStack() != null) {
        boolean good2go = true;
        for (Component comp : mat.components) {
          Double available = remains.contents.get(comp.name);
          if (available == null || comp.granularities.size() <= level ||  available < comp.granularities.get(level)) {
            good2go = false;
            //System.out.println("nogo: "+available+"..."+comp.granularities.get(level));
          }
        }
        if (good2go) {
          used.materials.add(mat);
        }
        while (good2go) {
          for (Component comp : mat.components) {
            Double needed = comp.granularities.get(level);
            remains.contents.put(comp.name, remains.contents.get(comp.name) - needed);
            if (used.contents.get(comp.name) == null) {
              used.contents.put(comp.name, needed * comp.factor);
            } else {
              used.contents.put(comp.name, used.contents.get(comp.name) + needed * comp.factor);
            }
            if (remains.contents.get(comp.name) < needed) {
              good2go = false;
            }
          }
          
        }
      }
    }
    
    for (Entry<String, Double> content : remains.contents.entrySet()) {
      content.setValue(content.getValue() * 10);
    }
    
    levels.add(level, used);
    return remains;
  }
  
}
