package fr.nil.bettervillagers.client;

import fr.nil.bettervillagers.Core;
import fr.nil.bettervillagers.utils.packet.PacketsEnum;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import org.apache.logging.log4j.Level;

public class CoreClient implements ClientModInitializer {

    public static boolean isServerModded;
    @Override
    public void onInitializeClient() {

        ClientPlayNetworking.registerGlobalReceiver(PacketsEnum.PACKET_HI.getIdentifier(), (client, handler, buf, responseSender) -> {
            String serverVersion = buf.readString();
            client.execute(() -> {
                System.out.println("Server is using BetterVillager v" + serverVersion);
                isServerModded = true;

                if(!serverVersion.equalsIgnoreCase(Core.VERSION))
                {
                    Core.log(Level.WARN,  "Server is not using the same version as the client (Client version : " + Core.VERSION + " | Server version : " + serverVersion +")");
                }

                ClientPlayNetworking.send(PacketsEnum.PACKET_REQUEST_ALL_VILLAGERS.getIdentifier(), PacketByteBufs.empty());

            });
        });


        ClientPlayNetworking.registerGlobalReceiver(PacketsEnum.PACKET_SEND_ENTITY.getIdentifier(), (client, handler, buf, responseSender) -> {
            Core.log(Level.DEBUG,"Received an entity update from server");
            int entityID = buf.readInt();
            NbtCompound tag = buf.readNbt();

            client.execute(() -> {
                assert client.player != null;
                Entity entity = client.player.getWorld().getEntityById(entityID);
                if(entity != null){
                    assert tag != null;
                    if (entity instanceof VillagerEntity) {
                        VillagerEntity villager = (VillagerEntity) entity;
                        villager.readNbt(tag);
                        villager.readCustomDataFromNbt(tag);
                        villager.saveNbt(tag);
                    }
                }
            });
        });
    }
}
