package fr.nil.bettervillagers;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Core implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "bettervillagers";
    public static final String MOD_NAME = "Better Villagers";
    public static String VERSION;

    public static final String PREFIX = "§a[§2Better Villager§a] §8 >> §r";

    @Override
    public void onInitialize() {

        VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString();
        log(Level.INFO, "Initializing " + Core.MOD_NAME + " v" + Core.VERSION);

    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

}