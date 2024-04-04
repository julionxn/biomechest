package me.julionxn.biomechest.persistent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.julionxn.biomechest.BiomeChest;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {

    private Data data;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File configFile = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("biomechest.json")
            .toFile();

    /**
     * Inicializar la configuración. En caso de no existir el archivo json, crea uno nuevo;
     * En caso de estar presente, se carga la configuración.
     */
    public void init(){
        if (!configFile.exists()){
            BiomeChest.LOGGER.error("File biomechest.json not found.");
            data = new Data();
            save();
            return;
        }
        load();
    }

    /**
     * Carga el archivo json.
     */
    public void load(){
        try (FileReader reader = new FileReader(configFile)) {
            data = gson.fromJson(reader, Data.class);
        } catch (IOException e) {
            BiomeChest.LOGGER.error("Something went wrong loading the configuration", e);
            data = new Data();
        }
    }

    /**
     * Guarda el estado actual al archivo json.
     */
    public void save(){
        try(FileWriter fileWriter = new FileWriter(configFile)){
            gson.toJson(data, fileWriter);
        } catch (IOException e) {
            BiomeChest.LOGGER.error("Something went wrong saving the configuration.", e);
        }
    }

    public Data getData(){
        return data;
    }

    public static Config getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final Config INSTANCE = new Config();
    }

}
