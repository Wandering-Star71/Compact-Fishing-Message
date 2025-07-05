package starship.fishhelper.modMenu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigData {
    public boolean enableTreasureReciMsg = false;
    public boolean enableCompactFishmsg = true;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/fishhelper.json");

    private static ConfigData instance = new ConfigData();

    public static ConfigData getInstance() {
        return instance;
    }

    public static void load() {
        if (!CONFIG_FILE.exists()) {
//            System.out.println("[FishHelper] 配置文件不存在，使用默认值。");
            save(); // 生成默认配置文件
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            instance = GSON.fromJson(reader, ConfigData.class);
//            System.out.println("[FishHelper] 配置加载成功。");
        } catch (IOException e) {
//            System.err.println("[FishHelper] 配置加载失败：" + e.getMessage());
        }
    }

    public static void save() {
        try {
            if (!CONFIG_FILE.getParentFile().exists()) {
                CONFIG_FILE.getParentFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(instance, writer);
//                System.out.println("[FishHelper] 配置保存成功。");
            }
        } catch (IOException e) {
//            System.err.println("[FishHelper] 配置保存失败：" + e.getMessage());
        }
    }

    public void updateFrom(ConfigData newData) {
        this.enableTreasureReciMsg = newData.enableTreasureReciMsg;
        this.enableCompactFishmsg = newData.enableCompactFishmsg;
    }
}
