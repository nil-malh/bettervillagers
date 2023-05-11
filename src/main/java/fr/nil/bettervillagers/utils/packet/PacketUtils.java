package fr.nil.bettervillagers.utils.packet;

import fr.nil.bettervillagers.Core;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;


public class PacketUtils {


    public static PacketByteBuf sendEntityUpdateToClient(Entity entity)
    {
        PacketByteBuf packet = PacketByteBufs.create();
        packet.writeInt(entity.getId());
        packet.writeNbt(entity.writeNbt(new NbtCompound()));
            return packet;
    }

    public static PacketByteBuf sendVersion()
    {
        PacketByteBuf packet = PacketByteBufs.create();
        packet.writeString(Core.VERSION);
        return packet;

    }

    public static PacketByteBuf requestEntityUpdateFromServer(Entity entity)
    {
        PacketByteBuf packetByteBuf = PacketByteBufs.create();
        packetByteBuf.writeInt(entity.getId());
        return packetByteBuf;
    }




}
