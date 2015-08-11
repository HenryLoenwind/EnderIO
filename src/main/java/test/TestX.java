package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.extended.NamedMapConverter;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;

public class TestX {

  private String configPath = ".";

  public static void main(String[] args) throws IOException {
    (new TestX()).run();
  }

  private void run() throws IOException {
    XStream xstream = new XStream();
    if (this.getClass().getClassLoader() != null) {
      xstream.setClassLoader(this.getClass().getClassLoader());
    }
    
//    xstream.setMode(XStream.ID_REFERENCES);
    xstream.aliasPackage("", "test");
    xstream.registerLocalConverter(Water.class, "contents",
    new NamedMapConverter(xstream.getMapper(), "component", "name", String.class, "ppm", Double.class, true, true, xstream.getConverterLookup())
    );
    xstream.addImplicitCollection(Material.class, "components");
    xstream.useAttributeFor(Material.class, "name");
    xstream.useAttributeFor(Material.class, "prio");
    xstream.useAttributeFor(Material.class, "volume");
    xstream.useAttributeFor(Material.class, "density");
    xstream.useAttributeFor(Component.class, "name");
    xstream.useAttributeFor(Component.class, "factor");
    xstream.useAttributeFor(Component.class, "count");

    ClassAliasingMapper mapper = new ClassAliasingMapper(xstream.getMapper());  
    mapper.addClassAlias("granularity", Double.class);
    xstream.registerLocalConverter(
        Component.class,
        "granularities",
        new CollectionConverter(mapper)
    );
    
    //readConfig(xstream, "test.xml");
    go(xstream);
  }

  private Object readConfig(XStream xstream, String fileName) throws IOException {
    File configFile = new File(configPath, fileName);
    
    if(configFile.exists()) {
      return xstream.fromXML(configFile);
    }

    InputStream defaultFile = this.getClass().getResourceAsStream("/assets/enderio/config/" + fileName);
    if(defaultFile == null) {
      throw new IOException("Could not get resource /assets/enderio/config/" + fileName + " from classpath. ");
    }
    
    Object myObject = xstream.fromXML(defaultFile);
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(configFile, false));
      xstream.toXML(myObject, writer);
    } finally {
      IOUtils.closeQuietly(writer);
    }
    return myObject;
  }

  void go(XStream xstream) throws IOException {
    Water wc = new Water();
    wc.contents = new HashMap<String, Double>();
    wc.materials = new ArrayList<Material>();
    
    wc.contents.put("Chloride", 18980.0);
    wc.contents.put("Sodium", 10561.0);
    wc.contents.put("Aluminium", 0.001);
    
    Material m = new Material();
    m.name = "Aluminium";
    m.result = new Item();
    m.result.name = "blockAluminium";
    m.result.damage = 0;
    m.prio = 1;
    m.volume = 1000000.0;
    wc.materials.add(m);
    m.components = new ArrayList<Component>();
    
    Component c = new Component();
    c.name = "Aluminium";
    c.factor = 1.0;
    m.density = 2.70;
    c.count = 1;
    c.granularities = new ArrayList<Double>();
    c.granularities.add(1.0);
    c.granularities.add(1.0);
    c.granularities.add(1.0);
    c.granularities.add(1.0);
    c.granularities.add(1.0);
    m.components.add(c);
    
    
    m = new Material();
    m.name = "Salt";
    m.result = new Item();
    m.result.name = "blockSalt";
    m.result.damage = 0;
    m.prio = 1;
    m.volume = 1000000.0;
    wc.materials.add(m);
    m.components = new ArrayList<Component>();
    
    c = new Component();
    c.name = "Chloride";
    c.factor = 0.01;
    m.density = 2.165;
    c.count = 1;
    c.granularities = new ArrayList<Double>();
    c.granularities.add(1000.0);
    c.granularities.add(1000.0);
    c.granularities.add(1000.0);
    c.granularities.add(1000.0);
    c.granularities.add(1000.0);
    m.components.add(c);
    
    c = new Component();
    c.name = "Sodium";
    c.factor = 0.01;
    c.count = 1;
    c.granularities = new ArrayList<Double>();
    c.granularities.add(1000.0);
    c.granularities.add(1000.0);
    c.granularities.add(1000.0);
    c.granularities.add(1000.0);
    c.granularities.add(1000.0);
    m.components.add(c);
    
    
    
    Config cfg = new Config();
    cfg.config = wc;
    cfg.compute();
    
    File configFile = new File(configPath, "test.xml");
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(configFile, false));
      xstream.toXML(/*wc*/cfg, writer);
    } finally {
      IOUtils.closeQuietly(writer);
    }
  }
  
}
