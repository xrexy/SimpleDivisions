package me.xrexy.simpledivisions.files;

import me.xrexy.simpledivisions.SimpleDivisions;
import me.xrexy.simpledivisions.utils.Utils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

public class FileAPI {
    private final HashMap<String, FileConfiguration> files = new HashMap<>();
    private final SimpleDivisions plugin = SimpleDivisions.getInstance();

    public void loadFile(String fileName, boolean isResource) {
        if (!files.containsKey(fileName)) {
            try {
                files.put(fileName, createFile(fileName, isResource));
            } catch (IOException | InvalidConfigurationException exception) {
                if (plugin.getConfig().getBoolean("debug")) exception.printStackTrace();

                Utils.log(Level.SEVERE, "Couldn't load file " + fileName);
            }
        }
    }

    private FileConfiguration createFile(String fileName, boolean isResource) throws IOException, InvalidConfigurationException {
        File file = new File(plugin.getDataFolder(), fileName);
        FileConfiguration config = new YamlConfiguration();

        if (!file.exists()) {
            file.getParentFile().mkdirs();

            if (!isResource) {
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                plugin.saveResource(fileName, plugin.getResource(fileName) == null);
            }
        }
        config.load(file);
        return config;
    }

    public HashMap<String, FileConfiguration> getFiles() {
        return files;
    }

    public FileConfiguration getFile(String fileName) {
        return files.get(fileName);
    }
}
