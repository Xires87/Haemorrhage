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

    public static final HashMap<String, ArmorBleedProt> ARMOR_BLEED_PROT_MAP = FileHelper.getArmorBleedProtMap();

    private static boolean shouldSaveFile = false;


    public static HashMap<String, ArmorBleedProt> getArmorBleedProtMap() {
        File file = new File(FabricLoader.getInstance().getConfigDir().toString() + "/imbleeding/armor_bleed_prot_values.json");
        if(file.exists()){
            try(FileReader reader = new FileReader(file)){
                Gson gson = new Gson();
                TypeToken<HashMap<String, ArmorBleedProt>> mapToken = new TypeToken<>(){};

                return gson.fromJson(reader, mapToken);
            } catch (Exception e) {
                ImBleeding.LOGGER.error("An error occurred while reading the following file: '" + file.getPath() + "'", e);
            }
        }
        else {
            shouldSaveFile = true;
        }

        return new HashMap<>();
    }

    public static void saveArmorBleedProtMap() {
        if(shouldSaveFile){
            File file = new File(FabricLoader.getInstance().getConfigDir().toString() + "/imbleeding/armor_bleed_prot_values.json");
            if(!file.exists()){
                try{
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                } catch (Exception e) {
                    ImBleeding.LOGGER.error("An error occurred while creating the following file: " + file.getPath(), e);
                }
            }
            try(FileWriter writer = new FileWriter(file)){
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                writer.write(gson.toJson(ARMOR_BLEED_PROT_MAP));

            } catch (Exception e) {
                ImBleeding.LOGGER.error("An error occurred while writing to the following file: " + file.getPath(), e);
            }

            shouldSaveFile = false;
        }
    }
}
