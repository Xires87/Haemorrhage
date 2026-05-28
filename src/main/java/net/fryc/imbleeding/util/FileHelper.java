package net.fryc.imbleeding.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.attributes.ArmorBleedProt;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public class FileHelper {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final String IMBLEEDING_PATH = FabricLoader.getInstance().getConfigDir().toString() + "/imbleeding";
    private static final String UNSYNCHRONIZED_PATH = "/unsynchronized";
    private static final String ARMOR_BLEED_PROT_FILE = "/armor_bleed_prot_values.json";

    public static final HashMap<String, ArmorBleedProt> ARMOR_BLEED_PROT_MAP = FileHelper.getArmorBleedProtMap();


    public static HashMap<String, ArmorBleedProt> getArmorBleedProtMap() {
        File file = new File(IMBLEEDING_PATH + UNSYNCHRONIZED_PATH + ARMOR_BLEED_PROT_FILE);
        if(file.exists()){
            try(FileReader reader = new FileReader(file)){
                TypeToken<HashMap<String, ArmorBleedProt>> mapToken = new TypeToken<>(){};

                return GSON.fromJson(reader, mapToken);
            } catch (Exception e) {
                ImBleeding.LOGGER.error("An error occurred while reading the following file: '" + file.getPath() + "'", e);
            }
        }

        return new HashMap<>();
    }

    public static void writeToFile(String path, String content, boolean overwrite) {
        File file = new File(path);
        if(overwrite || !file.exists()){
            try{
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                ImBleeding.LOGGER.error("An error occurred while creating the following file: " + file.getPath(), e);
            }

            try(FileWriter writer = new FileWriter(file)){
                writer.write(content);

            } catch (Exception e) {
                ImBleeding.LOGGER.error("An error occurred while writing to the following file: " + file.getPath(), e);
            }
        }
    }

    public static void saveArmorBleedProtMap() {
        writeToFile(
                IMBLEEDING_PATH + UNSYNCHRONIZED_PATH + ARMOR_BLEED_PROT_FILE,
                GSON.toJson(ARMOR_BLEED_PROT_MAP),
                false
        );
    }

    public static void createReadme() {
        writeToFile(
                IMBLEEDING_PATH + UNSYNCHRONIZED_PATH + "/README.txt",
                "These config files are not synchronized between server and client - you may experience visual bugs if your files differ from the ones used on server! \n " +
                        "For example, if you make Diamond Chestplate give +200 bleed protection in your (client) config, and server makes it give +50 bleed protection, the tooltip will say it gives +200 bleed protection, but actually it will give you +50 (value from server config)",
                false
        );
    }
}
