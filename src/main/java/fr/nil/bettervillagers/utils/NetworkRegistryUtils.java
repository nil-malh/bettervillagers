package fr.nil.bettervillagers.utils;

import fr.nil.bettervillagers.Core;
import net.minecraft.util.Identifier;

public class NetworkRegistryUtils {
    public static Identifier id(String name) {
        return new Identifier(Core.MOD_ID, name);
    }

}
