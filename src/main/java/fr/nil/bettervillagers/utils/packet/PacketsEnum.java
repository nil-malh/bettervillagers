package fr.nil.bettervillagers.utils.packet;

import fr.nil.bettervillagers.utils.NetworkRegistryUtils;
import net.minecraft.util.Identifier;

public enum PacketsEnum {


    PACKET_HI(NetworkRegistryUtils.id("hi")),
    PACKET_REQUEST_ENTITY(NetworkRegistryUtils.id("request_entity")),
    PACKET_SEND_ENTITY(NetworkRegistryUtils.id("send_entity")),
    PACKET_REQUEST_ALL_VILLAGERS(NetworkRegistryUtils.id("request_all_entity"));

    private Identifier identifier;
    PacketsEnum(Identifier id){

        identifier = id;


    }

    public Identifier getIdentifier() {
        return identifier;
    }
}
