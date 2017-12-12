package crazypants.enderio.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class LangParser {

  private static Map<String, Map<String, String>> byKey = new HashMap<>(); // key, lang, val
  private static Map<String, Map<String, String>> byVal = new HashMap<>(); // lang, val, key
  private static Map<String, Map<String, String>> result = new HashMap<>(); // lang, key, val

  // public static void main(String[] args) {
  public static void curesewontallowmainsinthejar() {
    try {

      int langs = 0, vals = 0;
      File f = new File("C:/github/EnderIO_110/resources/assets/enderio/lang/");

      File[] files = f.listFiles();

      for (File file : files) {
        if (file.getName().endsWith(".lang")) {
          String lang = file.getName().replaceAll("\\..*$", "").toLowerCase(Locale.ENGLISH);

          if (!byVal.containsKey(lang)) {
            byVal.put(lang, new HashMap<>());
            result.put(lang, new HashMap<>());
          }
          List<String> lines = FileUtils.readLines(file, "UTF-8");
          langs++;
          for (String line : lines) {
            line = line.trim();
            if (!line.startsWith("/") && !line.startsWith("#") && !line.startsWith("#") && line.contains("=")) {
              String[] split = line.split("=", 2);
              String key = split[0].trim();
              String val = split[1].trim();
              if (!byKey.containsKey(key)) {
                byKey.put(key, new HashMap<>());
              }
              byKey.get(key).put(lang, val);
              byVal.get(lang).put(val, key);
              vals++;
            }
          }

        }
      }

      System.out.println("Read " + langs + " langs with " + vals + " values");

      File f2 = new File("./resources/assets/enderio/lang/en_us.lang");

      List<String> lines = FileUtils.readLines(f2, "UTF-8");
      for (String line : lines) {
        line = line.trim();
        if (!line.startsWith("/") && !line.startsWith("#") && !line.startsWith("#") && line.contains("=")) {
          String[] split = line.split("=", 2);
          String key = split[0].trim();
          String val = split[1].trim();
          String offset = "                                                                                                              ".substring(0,
              key.length() - 4);

          for (String lang : result.keySet()) {
            if (byVal.get("en_us").containsKey(val) && byKey.containsKey(byVal.get("en_us").get(val))
                && byKey.get(byVal.get("en_us").get(val)).containsKey(lang)) {
              result.get(lang).put(key, byKey.get(byVal.get("en_us").get(val)).get(lang) + "\n#" + offset + "en: " + val);
            } else if (byKey.containsKey(key) && byKey.get(key).containsKey(lang)) {
              result.get(lang).put(key, byKey.get(key).get(lang) + "\n#" + offset + "en: " + val);
            } else {
              result.get(lang).put(key, "" + "\n#" + offset + "en: " + val);
            }
          }
        }
      }

      for (File file : new File("./resources/assets/enderio/lang/").listFiles()) {
        if (file.getName().endsWith(".lang")) {
          String lang = file.getName().replaceAll("\\..*$", "").toLowerCase(Locale.ENGLISH);

          if (!result.containsKey(lang)) {
            result.put(lang, new HashMap<>());
          }
          for (String line : FileUtils.readLines(file, "UTF-8")) {
            line = line.trim();
            if (!line.startsWith("/") && !line.startsWith("#") && !line.startsWith("#") && line.contains("=")) {
              String[] split = line.split("=", 2);
              String key = split[0].trim();
              result.get(lang).remove(key);
            }
          }

        }
      }

      result.remove("en_us");


      for (String lang : result.keySet()) {
        lines.clear();
        for (String key : result.get(lang).keySet()) {
          lines.add(key + "=" + result.get(lang).get(key));
        }
        File f3 = new File("./resources/assets/enderio/lang/" + lang + ".guess");
        Collections.sort(lines);
        lines.add(0, "# This file has been auto-generated by the 'LangParser' tool. Do NOT edit!");
        lines.add(1, "#");
        lines.add(2, "# Please wait until the en_us lang file has been completed before translating.");
        lines.add(3, "#");
        System.out.println("Writing guess file for " + lang + " with " + lines.size() + " lines");
        FileUtils.writeLines(f3, lines);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
