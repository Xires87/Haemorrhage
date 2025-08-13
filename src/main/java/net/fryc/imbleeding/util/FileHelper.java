package net.fryc.imbleeding.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.attributes.ArmorBleedProt;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class FileHelper {

    public static boolean shouldSaveFile = true;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    public static HashMap<String, ArmorBleedProt> getArmorBleedProtMap() {
        File file = new File("config/imbleeding/armor_bleed_prot_values.json");
        if(file.exists()){
            try(FileReader reader = new FileReader(file)){
                TypeToken<HashMap<String, ArmorBleedProt>> mapToken = new TypeToken<>(){};

                return GSON.fromJson(reader, mapToken);
            } catch (Exception e) {
                shouldSaveFile = false;
                ImBleeding.LOGGER.error("An error occurred while reading the following file: '" + file.getPath() + "'", e);
            }
        }

        return new HashMap<>();
    }
}
