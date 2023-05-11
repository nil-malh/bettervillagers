package fr.nil.bettervillagers.mixins.client;

import fr.nil.bettervillagers.Core;
import fr.nil.bettervillagers.utils.packet.PacketUtils;
import fr.nil.bettervillagers.utils.packet.PacketsEnum;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)

public abstract class ClientPlayNetworkHandlerMixin {



    //@Inject(method = "onGameJoin", at = @At("INVOKE"), target = "this.player.joinWorld(world)"))


    @Shadow public abstract void onGameJoin(GameJoinS2CPacket packet);
    @Shadow @Final private ClientConnection connection;

    @Inject(method = "onGameJoin", at = @At(value = "RETURN", target = "Lnet/minecraft/client/MinecraftClient;joinWorld(Lnet/minecraft/client/world/ClientWorld;)V"))
    private void playerJoinClientHookOnGameJoin$bettervillagers(CallbackInfo ci) {
       Core.log(Level.DEBUG, "Joining a server world sending Hi.");

            ClientPlayNetworking.send(PacketsEnum.PACKET_HI.getIdentifier(), PacketUtils.sendVersion());

    }



}
