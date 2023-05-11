package fr.nil.bettervillagers.server;

import com.google.common.collect.Lists;
import fr.nil.bettervillagers.Core;
import fr.nil.bettervillagers.utils.packet.PacketUtils;
import fr.nil.bettervillagers.utils.packet.PacketsEnum;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.Level;

import java.util.List;

public class CoreServer implements DedicatedServerModInitializer {

    public static final List<ServerPlayerEntity> moddedPlayers = Lists.newArrayList();

    @Override
    public void onInitializeServer() {

        ServerPlayNetworking.registerGlobalReceiver(PacketsEnum.PACKET_HI.getIdentifier(), (server, player, handler, buf, responseSender) -> {

            String clientVersion = buf.readString();

            server.execute(() -> {

                if (!clientVersion.equalsIgnoreCase(Core.VERSION)) {
                    Core.log(Level.WARN, player.getName().getString() + " is not using the same version as the server (Client version : " + clientVersion + " | Server version : " + Core.VERSION + ")");
                }

                Core.log(Level.INFO, player.getName().getString() + " is using BetterVillager v" + clientVersion);
                moddedPlayers.add(player);
                PacketByteBuf buffer = PacketByteBufs.create();
                buffer.writeString(Core.VERSION);
                ServerPlayNetworking.send(player, PacketsEnum.PACKET_HI.getIdentifier(), buffer);

            });
        });

        ServerPlayNetworking.registerGlobalReceiver(PacketsEnum.PACKET_REQUEST_ENTITY.getIdentifier(), (server, player, handler, buf, responseSender) -> {
            Core.log(Level.DEBUG, "Received a entity request from client.");
            int entityID = buf.readVarInt();
            server.execute(() -> {
                Entity entity = player.getWorld().getEntityById(entityID);
                if (entity != null) {
                    PacketByteBuf packet = PacketUtils.sendEntityUpdateToClient(entity);
                    ServerPlayNetworking.send(player, PacketsEnum.PACKET_SEND_ENTITY.getIdentifier(), packet);
                    Core.log(Level.DEBUG, "Client should've received an entity packet.");
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(PacketsEnum.PACKET_REQUEST_ALL_VILLAGERS.getIdentifier(), (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                Core.log(Level.INFO, player.getName().getString() + " has requested all villagers");
                player.getWorld().getEntitiesByType(EntityType.VILLAGER, villagerEntity -> true).forEach(villagerEntity -> {
                    ServerPlayNetworking.send(player, PacketsEnum.PACKET_SEND_ENTITY.getIdentifier(), PacketUtils.sendEntityUpdateToClient(villagerEntity));

                });

            });

        });

    }
}
